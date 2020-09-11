package me.stuhlmeier.client;

import me.stuhlmeier.service.StringService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiceClient implements AutoCloseable {
    private record LoadedServiceData(
            URLClassLoader classLoader,
            List<StringService> implementations
    ) implements AutoCloseable {
        @Override
        public void close() {
            try {
                classLoader.close();
                implementations.clear();
            } catch (IOException e) {
                System.err.printf("Error closing URLClassLoader for URLs %s%n", Arrays.toString(classLoader.getURLs()));
                e.printStackTrace();
            }
        }
    }

    private final Map<Path, LoadedServiceData> loaded = new LinkedHashMap<>();

    public void loadJar(Path path) {
        System.out.printf("Loading JAR from path %s%n", path);

        URLClassLoader classLoader;
        try {
            classLoader = new URLClassLoader(
                    new URL[]{path.toUri().toURL()},
                    ServiceClient.class.getClassLoader()
            );
        } catch (MalformedURLException e) {
            System.err.printf("Error creating URL for path %s%n", path);
            e.printStackTrace();
            return;
        }

        var implementations = ServiceLoader.load(StringService.class, classLoader)
                .stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toList());

        loaded.put(path.normalize(), new LoadedServiceData(classLoader, implementations));
    }

    public boolean unloadJar(Path path) {
        var data = loaded.remove(path.normalize());
        if (data == null) return false;
        data.close();
        return true;
    }

    public Stream<StringService> stream() {
        return loaded.values().stream().flatMap(l -> l.implementations.stream());
    }

    @Override
    public void close() {
        loaded.forEach((__, data) -> data.close());
        loaded.clear();
    }

    public static void main(String[] args) throws IOException {
        var jarPaths = Arrays.stream(args).map(Path::of).collect(Collectors.toList());
        var replacement = jarPaths.remove(jarPaths.size() - 1);

        try (var client = new ServiceClient()) {
            // Load first two services
            System.out.printf("Loading impl-1 and impl-2%n");
            jarPaths.forEach(client::loadJar);

            client.stream().forEach(service -> System.out.printf("%s: %s%n", service.getClass().getSimpleName(), service.getString()));
            System.out.println();

            // Remove second service
            System.out.printf("Removing second service: %s%n", client.unloadJar(jarPaths.get(1)));

            client.stream().forEach(service -> System.out.printf("%s: %s%n", service.getClass().getSimpleName(), service.getString()));
            System.out.println();

            // Replace first service
            System.out.printf("Replacing first service%n");
            Files.copy(replacement, jarPaths.get(0), StandardCopyOption.REPLACE_EXISTING);

            client.loadJar(jarPaths.get(0));

            client.stream().forEach(service -> System.out.printf("%s: %s%n", service.getClass().getSimpleName(), service.getString()));
            System.out.println();
        }
    }
}

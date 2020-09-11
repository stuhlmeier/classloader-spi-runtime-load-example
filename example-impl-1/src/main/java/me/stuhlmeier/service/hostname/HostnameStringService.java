package me.stuhlmeier.service.hostname;

import me.stuhlmeier.service.StringService;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostnameStringService implements StringService {
    @Override
    public String getString() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }
}

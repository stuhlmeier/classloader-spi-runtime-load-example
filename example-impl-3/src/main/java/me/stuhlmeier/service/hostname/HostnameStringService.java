package me.stuhlmeier.service.hostname;

import me.stuhlmeier.service.StringService;

public class HostnameStringService implements StringService {
    @Override
    public String getString() {
        return String.format("This implementation should have replaced the old %s", HostnameStringService.class.getSimpleName());
    }
}

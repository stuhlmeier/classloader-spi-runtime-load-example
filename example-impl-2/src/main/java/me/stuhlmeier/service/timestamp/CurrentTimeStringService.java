package me.stuhlmeier.service.timestamp;

import me.stuhlmeier.service.StringService;

import java.time.Instant;

public class CurrentTimeStringService implements StringService {
    @Override
    public String getString() {
        return Instant.now().toString();
    }
}

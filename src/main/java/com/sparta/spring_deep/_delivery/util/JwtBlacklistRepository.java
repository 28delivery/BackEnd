package com.sparta.spring_deep._delivery.util;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class JwtBlacklistRepository {
    private final ConcurrentHashMap<String, Boolean> blacklist = new ConcurrentHashMap<>();

    public void addBlackList(String token) {
        blacklist.put(token, true);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}

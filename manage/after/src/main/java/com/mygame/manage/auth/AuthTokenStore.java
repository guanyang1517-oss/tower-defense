package com.mygame.manage.auth;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthTokenStore {

    private final Map<String, Long> tokenAccountMap = new ConcurrentHashMap<String, Long>();

    public String createToken(Long accountId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenAccountMap.put(token, accountId);
        return token;
    }

    public Long getAccountId(String token) {
        return tokenAccountMap.get(token);
    }

    public void removeToken(String token) {
        tokenAccountMap.remove(token);
    }
}

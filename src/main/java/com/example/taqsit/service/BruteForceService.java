package com.example.taqsit.service;

import com.example.taqsit.payload.AllApiResponse;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Data
public class BruteForceService {
    public static final int MAX_ATTEMPT = 4;
    private final LoadingCache<String, Integer> attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES).build(new CacheLoader<>() {
        @Override
        public Integer load(final String key) {
            return 0;
        }
    });

    @Autowired
    private HttpServletRequest request;

    public void loginFailed(final String key) {
        int attempts;
        try {
            attempts = attemptsCache.get(key);
        } catch (final ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked() {
        try {
            return attemptsCache.get(getClientIP()) >= MAX_ATTEMPT;
        } catch (final ExecutionException e) {
            return false;
        }
    }

    public int getUserReamingAttempts() {
        try {
            return MAX_ATTEMPT - attemptsCache.get(getClientIP());
        } catch (Exception e) {
            return MAX_ATTEMPT;
        }
    }

    public HttpEntity<?> getUserAttempts() {
        try {
            return AllApiResponse.response(1, "Attempts", attemptsCache.asMap());
        } catch (Exception e) {
            return AllApiResponse.response(500, 0, "Error");
        }
    }

    public HttpEntity<?> removeAttemptByIp(String ip) {
        try {
            attemptsCache.put(ip, 0);
            return AllApiResponse.response(1, "Success!");
        } catch (Exception e) {
            return AllApiResponse.response(50, 0, "Error");
        }
    }

    public void setUserAttempt(int attempt) {
        attemptsCache.put(getClientIP(), attempt);
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}

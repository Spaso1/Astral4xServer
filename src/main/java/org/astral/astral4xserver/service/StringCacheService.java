package org.astral.astral4xserver.service;

import org.astral.astral4xserver.cache.TimeLimitedCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class StringCacheService {
    @Autowired
    private TimeLimitedCache<String, String> time3LimitedCache;

    public void setData(String key, String value) {
        time3LimitedCache.put(key, value);
    }

    public String getData(String key) {
        return time3LimitedCache.get(key);
    }
}

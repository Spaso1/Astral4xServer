package org.astral.astral4xserver.service;

import org.astral.astral4xserver.been.User;
import org.astral.astral4xserver.cache.TimeLimitedCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataCacheService {

    @Autowired
    private TimeLimitedCache<String, User> timeLimitedCache;

    public void setData(String key, User value) {
        timeLimitedCache.put(key, value);
    }

    public User getData(String key) {
        return timeLimitedCache.get(key);
    }
}

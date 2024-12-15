package org.astral.astral4xserver.service;

import org.astral.astral4xserver.been.Auth;
import org.astral.astral4xserver.been.User;
import org.astral.astral4xserver.cache.TimeLimitedCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthCacheService {
    @Autowired
    private TimeLimitedCache<String, Auth> time2LimitedCache;

    public void setData(String key, Auth value) {
        time2LimitedCache.put(key, value);
    }

    public Auth getData(String key) {
        return time2LimitedCache.get(key);
    }
}

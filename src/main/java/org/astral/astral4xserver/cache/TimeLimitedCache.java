package org.astral.astral4xserver.cache;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeLimitedCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final long expirationTimeMillis;

    public TimeLimitedCache(long expirationTimeMillis) {
        this.expirationTimeMillis = expirationTimeMillis;
        startCleanupTask();
    }

    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null || isExpired(entry)) {
            return null;
        }
        return entry.getValue();
    }

    public void remove(K key) {
        cache.remove(key);
    }

    private boolean isExpired(CacheEntry<V> entry) {
        return System.currentTimeMillis() - entry.getTimestamp() > expirationTimeMillis;
    }

    private void startCleanupTask() {
        scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            AtomicInteger removedCount = new AtomicInteger();
            cache.forEach((key, entry) -> {
                if (isExpired(entry)) {
                    cache.remove(key);
                    removedCount.incrementAndGet();
                }
            });
            if (removedCount.get() > 0) {
                System.out.println("Removed " + removedCount.get() + " expired entries.");
            }
        }, expirationTimeMillis, expirationTimeMillis, TimeUnit.MILLISECONDS);
    }

    private static class CacheEntry<V> {
        private final V value;
        private final long timestamp;

        public CacheEntry(V value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        public V getValue() {
            return value;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}

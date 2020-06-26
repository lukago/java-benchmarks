package cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class LRUCache<K, V> implements Cache<K, V> {

    private final Map<K, V> cache;
    private final int capacity;

    private int evicted;
    private int missed;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new FIFOMap<>();
        this.evicted = 0;
        this.missed = 0;
    }

    @Override
    public void cache(K key, V value) {
        cache.remove(key);
        cache.put(key, value);
    }

    @Override
    public Optional<V> load(K key) {
        var res = cache.get(key);
        if (res == null) {
            missed++;
        }
        return Optional.ofNullable(res);
    }

    @Override
    public int evictedCount() {
        return evicted;
    }

    @Override
    public int missCount() {
        return missed;
    }

    @Override
    public void clearStats() {
        missed = 0;
        evicted = 0;
    }

    private class FIFOMap<KEY, VAL> extends LinkedHashMap<KEY, VAL> {
        @Override
        protected boolean removeEldestEntry(Map.Entry<KEY, VAL> eldest) {
            boolean shouldRemove = size() > capacity;
            if (shouldRemove) {
                evicted++;
            }
            return shouldRemove;
        }
    }

}

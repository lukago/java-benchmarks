package cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class RRCache<K, V> implements Cache<K, V> {

    private static final Random rnd = new Random();

    private final Map<K, V> cache;
    private final int capacity;

    private int evicted;
    private int missed;

    public RRCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.evicted = 0;
        this.missed = 0;
    }

    @Override
    public void cache(K key, V value) {
        cache.put(key, value);
        if (cache.size() > capacity) {
            removeRandom();
            evicted++;
        }
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

    private void removeRandom() {
        List<K> keys = new ArrayList<K>(cache.keySet());
        int randomIndex = new Random().nextInt(keys.size());
        K randomKey = keys.get(randomIndex);
        cache.remove(randomKey);
    }
}

package cache;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Optional;

public class LFRUCache<K, V> implements Cache<K, V> {

    private final HashMap<K, V> cache;
    private final HashMap<K, Long> frequency;
    private final HashMap<Long, LinkedHashSet<K>> frequencySets;
    private final int capacity;

    private long min;
    private int evicted;
    private int missed;

    public LFRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.frequency = new HashMap<>();
        this.frequencySets = new HashMap<>();
        this.frequencySets.put(1L, new LinkedHashSet<>());
        this.min = -1;
        this.evicted = 0;
        this.missed = 0;
    }

    @Override
    public Optional<V> load(K key) {
        if (!cache.containsKey(key)) {
            missed++;
            return Optional.empty();
        }

        long count = frequency.get(key);
        long newCount = count + 1L;
        frequency.put(key, newCount);
        frequencySets.get(count).remove(key);

        if (count == min && frequencySets.get(count).size() == 0) {
            min++;
        }

        if (!frequencySets.containsKey(newCount)) {
            frequencySets.put(newCount, new LinkedHashSet<>());
        }

        frequencySets.get(newCount).add(key);

        return Optional.of(cache.get(key));
    }

    @Override
    public void cache(K key, V value) {
        if (cache.containsKey(key)) {
            cache.put(key, value);
            load(key);
            return;
        }

        if (cache.size() >= capacity) {
            K evict = frequencySets.get(min).iterator().next();
            frequencySets.get(min).remove(evict);
            cache.remove(evict);
            evicted++;
        }

        cache.put(key, value);
        frequency.put(key, 1L);
        min = 1L;
        frequencySets.get(1L).add(key);
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
}

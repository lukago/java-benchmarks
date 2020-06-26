package cache;

import java.util.Optional;

public interface Cache<K, V> {

    void cache(K key, V value);

    Optional<V> load(K key);

    int evictedCount();

    int missCount();

    void clearStats();
}

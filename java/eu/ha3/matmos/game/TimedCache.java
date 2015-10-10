package eu.ha3.matmos.game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dags_ <dags@dags.me>
 */

public class TimedCache<K, V>
{
    private final Map<K, CachedItem<V>> cache = new ConcurrentHashMap<K, CachedItem<V>>();
    private final long expiryPeriod;

    public TimedCache(long expiryPeriod)
    {
        this.expiryPeriod = expiryPeriod;
    }

    public boolean contains(K key)
    {
        return cache.containsKey(key) && !cache.get(key).expired();
    }

    public V get(K key)
    {
        if (cache.containsKey(key))
        {
            CachedItem<V> item = cache.get(key);
            if (!item.expired())
            {
                return item.get();
            }
            cache.remove(key);
        }
        return null;
    }

    public CachedItem<V> put(K key, V value)
    {
        return cache.put(key, new CachedItem<V>(value));
    }

    private class CachedItem<V>
    {
        private final V value;
        private final long entryTime;

        private CachedItem(V t)
        {
            value = t;
            entryTime = System.currentTimeMillis();
        }

        public boolean expired()
        {
            return System.currentTimeMillis() - entryTime > expiryPeriod;
        }

        public V get()
        {
            return value;
        }
    }
}

package com.meto.api.infrastructure.adapter.external;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Petit cache en mémoire avec durée de vie, borné en taille (pas de dépendance supplémentaire).
 * Au-delà de maxEntries, les entrées expirées sont retirées, puis tout le cache si cela ne suffit pas.
 */
final class TtlCache<K, V> {

    private record Entry<V>(V value, Instant expiresAt) {
    }

    private final Map<K, Entry<V>> entries = new ConcurrentHashMap<>();
    private final Duration ttl;
    private final int maxEntries;
    private final Clock clock;

    TtlCache(Duration ttl, int maxEntries, Clock clock) {
        this.ttl = ttl;
        this.maxEntries = maxEntries;
        this.clock = clock;
    }

    V get(K key, Supplier<V> loader) {
        Instant now = clock.instant();
        Entry<V> entry = entries.get(key);
        if (entry != null && entry.expiresAt().isAfter(now)) {
            return entry.value();
        }
        V value = loader.get(); // une erreur du fournisseur n'est jamais mise en cache
        if (entries.size() >= maxEntries) {
            entries.values().removeIf(e -> !e.expiresAt().isAfter(now));
            if (entries.size() >= maxEntries) {
                entries.clear();
            }
        }
        entries.put(key, new Entry<>(value, now.plus(ttl)));
        return value;
    }

    int size() {
        return entries.size();
    }
}

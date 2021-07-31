package eu.ha3.matmos.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface BetterStreams<T> {

    static <T> BetterStreams<T> of(Collection<? extends T> iterable) {
        return () -> iterable.stream();
    }

    @SafeVarargs
    static <T> BetterStreams<T> of(Collection<? extends T>... collections) {
        return () -> Stream.of(collections).flatMap(Collection::stream);
    }

    @SafeVarargs
    static <T> BetterStreams<T> of(Map<?, ? extends T>... maps) {
        return () -> Stream.of(maps).map(Map::values).flatMap(Collection::stream);
    }

    Stream<? extends T> s();

    default BetterStreams<T> unique() {
        return () -> s().distinct();
    }

    default <V> BetterStreams<V> flatten(Function<? super T, Collection<V>> convert) {
        return () -> s().flatMap(a -> convert.apply(a).stream());
    }

    default <V> BetterStreams<V> map(Function<? super T, V> convert) {
        return () -> s().map(convert);
    }

    default BetterStreams<T> join(Collection<? extends T> iterable) {
        return () -> Stream.concat(s(), iterable.stream());
    }

    default BetterStreams<T> join(Map<?, ? extends T> map) {
        return () -> Stream.concat(s(), map.values().stream());
    }

    default BetterStreams<T> filter(Predicate<? super T> test) {
        return () -> s().filter(test);
    }

    default List<T> asList() {
        return s().collect(Collectors.toList());
    }

    default Set<T> asSet() {
        return s().collect(Collectors.toSet());
    }

    default void forEach(Consumer<? super T> action) {
        s().forEach(action);
    }
}

package io.github.troimaclure.selectors;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author ajosse
 */

public class SingleSelector<T> {
    private List<T> list;

    public SingleSelector(List<T> list) {
        this.list = list;
    }

    public SingleSelector<T> where(Predicate<T> p) {
        this.list = list.stream().filter(p).collect(Collectors.toList());
        return this;
    }

    public T firstOrDefault(Predicate<T> p, T def) {
        return list.stream().filter(p).findFirst().orElse(def);
    }

    public T firstOrDefault(Predicate<T> p) {
        return firstOrDefault(p, null);
    }

    public T firstOrDefault() {
        return list.stream().findFirst().orElse(null);
    }

    public Optional<T> first(Predicate<T> p) {
        return list.stream().filter(p).findFirst();
    }

    public T firstOrThrow(Predicate<T> p, RuntimeException ex) {
        var t = firstOrDefault(p, null);
        if (t == null)
            throw ex;
        return t;
    }

    public SingleSelector<T> foreach(Consumer<T> c) {
        for (T t : list) {
            c.accept(t);
        }
        return this;
    }

    public <K> SingleSelector<K> select(Function<T, K> func) {
        List<K> collect = this.list.stream().map(e -> func.apply((T) e)).collect(Collectors.toList());
        return new SingleSelector<K>(collect);
    }

    public <U extends Comparable<? super U>> SingleSelector<T> orderBy(Function<? super T, ? extends U> keyExtractor) {
        this.list = this.list.stream().sorted(Comparator.comparing(keyExtractor)).collect(Collectors.toList());
        return this;
    }

    public <U extends Comparable<Double>> Double sumDouble(Function<? super T, Double> keyExtractor) {
        return this.list.stream().reduce(0d, (subtotal, add) -> subtotal + keyExtractor.apply(add),
                Double::sum);
    }

    public <U extends Comparable<Long>> Long sumLong(Function<? super T, Long> keyExtractor) {
        return this.list.stream().reduce(0L, (subtotal, add) -> subtotal + keyExtractor.apply(add), Long::sum);
    }

    public <U extends Comparable<Integer>> Integer sumInteger(Function<? super T, Integer> keyExtractor) {
        return this.list.stream().reduce(0, (subtotal, add) -> subtotal + keyExtractor.apply(add),
                Integer::sum);
    }

    public <U extends Comparable<? super U>> SingleSelector<T> orderBy(Function<? super T, ? extends U> keyExtractor,
            Comparator<? super U> comparator) {
        this.list = this.list.stream().sorted(Comparator.comparing(keyExtractor, comparator))
                .collect(Collectors.toList());
        return this;
    }

    public String toString(Function<? super T, String> keyExtractor) {
        return toString(keyExtractor, "");
    }

    public String toString(Function<? super T, String> keyExtractor, String separator) {
        return list.stream().map(e -> keyExtractor.apply(e)).collect(Collectors.joining(separator));
    }

    public <U> DualSelector<T, U> join(List<U> uList) {
        return new DualSelector<>(list, uList);
    }

    public <U extends Comparable<? super U>> SingleSelector<T> orderByDesc(
            Function<? super T, ? extends U> keyExtractor) {
        this.list = this.list.stream().sorted(Comparator.comparing(keyExtractor).reversed())
                .collect(Collectors.toList());
        return this;
    }

    public <U extends Comparable<? super U>> SingleSelector<T> orderByDesc(
            Function<? super T, ? extends U> keyExtractor, Comparator<? super U> comparator) {
        this.list = this.list.stream().sorted(Comparator.comparing(keyExtractor, comparator).reversed())
                .collect(Collectors.toList());
        return this;
    }

    public SingleSelector<T> concat(SingleSelector<T> single) {
        this.list = Stream.concat(this.list.stream(), single.toList().stream()).collect(Collectors.toList());
        return this;
    }

    public SingleSelector<T> concat(List<T> list) {
        this.list = Stream.concat(this.list.stream(), list.stream()).collect(Collectors.toList());
        return this;
    }

    public <R> SingleSelector<R> flat(Function<T, List<R>> func) {

        List<R> newlist = new ArrayList<R>();
        for (T t : this.list) {
            newlist.addAll(func.apply(t));
        }

        return new SingleSelector<R>(newlist);
    }

    private Predicate<T> predicateDistinct(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public SingleSelector<T> distinct(Function<? super T, ?> keyExtractor) {
        this.list = this.list.stream().filter(this.predicateDistinct(keyExtractor)).collect(Collectors.toList());
        return this;
    }

    public boolean contains(T t) {
        return this.list.stream().anyMatch(e -> e.equals(t));
    }

    public boolean allSame(Function<? super T, ?> keyExtractor) {
        var first = list.stream().findFirst();
        if (!first.isPresent())
            return false;
        for (T t : list) {
            if (!keyExtractor.apply(t).equals(keyExtractor.apply(first.get()))) {
                return false;
            }
        }
        return true;
    }

    public <K> SingleSelector<Entry<K, List<T>>> groupBy(Function<? super T, K> keyExtractor) {
        var list = this.list.stream().collect(Collectors.groupingBy(keyExtractor)).entrySet().stream()
                .collect(Collectors.toList());
        return new SingleSelector<Entry<K, List<T>>>(list);
    }

    public boolean any(Predicate<T> p) {
        return this.list.stream().anyMatch(p);
    }

    public boolean all(Predicate<T> p) {
        return this.list.stream().allMatch(p);
    }

    public List<T> toList() {
        return this.list;
    }

    public <K, E> Map<K, E> toMap(Function<? super T, K> keyExtractor, Function<? super T, E> keyExtractor2) {
        return this.list.stream().collect(Collectors.toMap(keyExtractor, keyExtractor2));
    }

    // need class to be send to avoir primitive try transform
    public T[] toArray(Class<T> c) {
        var array = (T[]) Array.newInstance(c, this.list.size());
        return this.list.toArray(array);
    }

}

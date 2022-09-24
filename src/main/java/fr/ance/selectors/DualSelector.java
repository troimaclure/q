package fr.ance.selectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import fr.ance.Q;
import fr.ance.exceptions.QException;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author ajosse
 */
@RequiredArgsConstructor
public class DualSelector<T, U> {

    private final List<T> list;
    private final List<U> compare;
    private List<Pair<T, U>> pairs;

    public DualSelector<T, U> on(BiPredicate<T, U> consumer) {
        pairs = new ArrayList<>();
        for (T t1 : list) {
            var collect = compare.stream().filter(e -> consumer.test(t1, e)).collect(Collectors.toList());
            for (var u1 : collect) {
                pairs.add(Pair.of(t1, u1));
            }
        }
        return this;
    }

    public DualSelector<T, U> where(Predicate<Pair<T, U>> predicate) {
        checkPair();
        pairs = pairs.stream().filter(predicate).collect(Collectors.toList());
        return this;
    }

    public <F extends Comparable<? super F>> SingleSelector<Pair<T, List<U>>> groupByLeft(
            Function<? super T, ? extends F> keyExtractor) {
        var list = new ArrayList<Pair<T, List<U>>>();
        for (var pair : this.pairs) {
            if (pair.getLeft() == null)
                continue;
            Pair<T, List<U>> lookup = null;
            for (var pair2 : list) {
                if (keyExtractor.apply(pair.getLeft()).equals(keyExtractor.apply(pair2.getLeft()))) {
                    lookup = pair2;
                    break;
                }
            }
            if (lookup == null) {
                lookup = Pair.of(pair.getLeft(), new ArrayList<U>());
                list.add(lookup);
            }
            lookup.getRight().add(pair.getRight());
        }
        return Q.from(list);
    }

    public <K> SingleSelector<K> select(Function<? super Pair<T, U>, ? extends K> func) {
        checkPair();
        List<K> collect = this.pairs.stream().map(e -> func.apply(e)).collect(Collectors.toList());
        return new SingleSelector<K>(collect);
    }

    public List<Pair<T, U>> toList() {
        checkPair();
        return this.pairs;
    }

    public <K, E> Map<K, E> toMap(Function<? super Pair<T, U>, K> keyExtractor,
            Function<? super Pair<T, U>, E> keyExtractor2) {
        return this.toList().stream().collect(Collectors.toMap(keyExtractor, keyExtractor2));
    }

    private void checkPair() {
        if (this.pairs == null) {
            throw new QException("You have to call ON with JOIN, the set is empty", "JOIN_EX");
        }
    }
}

package io.github.troimaclure;

import java.util.Arrays;
import java.util.List;

import io.github.troimaclure.selectors.DualSelector;
import io.github.troimaclure.selectors.SingleSelector;

/**
 *
 * @author Arthur JOSSEAU
 */
public class Q {
    public static <T> SingleSelector<T> from(List<T> list) {
        return new SingleSelector<>(list);
    }

    public static <T> SingleSelector<T> from(T[] array) {
        return from(Arrays.asList(array));
    }

    public static <T, U> DualSelector<T, U> join(List<T> t, List<U> u) {
        return new DualSelector<>(t, u);
    }
}
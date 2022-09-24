package fr.ance;

import java.util.Arrays;
import java.util.List;

import fr.ance.selectors.DualSelector;
import fr.ance.selectors.SingleSelector;

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
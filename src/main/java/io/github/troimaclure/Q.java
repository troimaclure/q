package io.github.troimaclure;

import java.util.ArrayList;
import java.util.List;

import io.github.troimaclure.selectors.DualSelector;
import io.github.troimaclure.selectors.SingleSelector;

/**
 *
 * @author Arthur JOSSEAU
 */
public class Q {
    /**
     * <h3>Usage</h3>
     * <p>
     * Start Q functional chaining
     * </p>
     * 
     * <pre>
     * {@code
     * 
     * Q.from(Arrays.asList("a", "b", "c"));
     * }
     * </pre>
     * 
     * or
     * 
     * <pre>
     * {@code
     * var list = new ArrayList<String>();
     * list.add("a");
     * list.add("b");
     * Q.from(list);
     * }
     * </pre>
     * 
     * @param <T>
     * @param list
     * @return {@code SingleSelector<T>}
     */
    public static <T> SingleSelector<T> from(List<T> list) {
        return new SingleSelector<>(list);
    }

    /**
     * <h3>Usage</h3>
     * <p>
     * Start Q functional chaining with array (will create an ArrayList<T> with all
     * array's element)
     * </p>
     * <b>!!This SignleSelector will not affect referenced array arg.!!</b>
     * 
     * <pre>
     * {@code
     * var array = new String[] { "a", "b" };
     * Q.from(array);
     * }
     * </pre>
     * 
     * @param <T>
     * @param list
     * @return {@code SingleSelector<T>}
     */
    public static <T> SingleSelector<T> from(T[] array) {
        var list = new ArrayList<T>();
        for (T a : array) {
            list.add(a);
        }
        return from(list);
    }

    /**
     * <h3>Usage</h3>
     * 
     * <pre>
     *  {@code 
     *  Create a DualSelector to join two list
     * }
     * </pre>
     * 
     * @param <T>
     * @param <U>
     * @param t
     * @param u
     * @return {@code DualSelector<T, U>}
     */
    public static <T, U> DualSelector<T, U> join(List<T> t, List<U> u) {
        return new DualSelector<>(t, u);
    }
}
package io.github.troimaclure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.troimaclure.exceptions.QException;
import io.github.troimaclure.selectors.DualSelector;

public class QTest {

    static List<Product> products;
    static List<Category> categories;
    static List<Pair<String, Double>> pairs;

    @BeforeEach
    public void setUpClass() {
        products = Arrays.asList(new Product("Cat good", 1, 1), new Product("Cat gg ", 1, 2),
                new Product("dog good", 2, 3), new Product("bird gg", 3, 4), new Product("dog gg", 2, 5),
                new Product("test", 6, 7), new Product("a", 7, 8), new Product("z", 8, 9));
        categories = Arrays.asList(new Category("cat", 1, 123456), new Category("dog", 2, 123456),
                new Category("bird", 3, 1234567890));

        pairs = Arrays.asList(Pair.of("pair", 20d), Pair.of("pair", 20d), Pair.of("pair", 20.5d));

    }

    @AfterEach
    public void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of join method, of class JoinUtils.
     */
    @Test
    public void test_from() {
        assertEquals(products, Q.from(products).toList());
    }

    @Test
    public void testToArray() {
        assertEquals(Q.from(products).toArray(Product.class).length, products.toArray().length);
    }

    @Test
    public void test_where() {
        int size = products.size();
        List<Product> list = Q.from(products).where(e -> e.getId() == 1).toList();
        assertEquals(1, list.size());
        assertEquals(products.size(), size);
    }

    @Test
    public void test_from_dual() {
        DualSelector<Product, Category> on = Q.join(products, categories);
        assertNotEquals(on, null);

    }

    @Test
    public void test_join() {
        DualSelector<Product, Category> on = Q.join(products, categories).on((e, u) -> e.getId_category() == u.getId());
        assertEquals(on.toList().size(), 5);
    }

    @Test
    public void test_join_without_on() {
        assertThrows(QException.class, () -> Q.join(products, categories).select(CategoryProduct::from).toList());
    }

    @Test
    public void test_select_dual() {

        List<CategoryProduct> list = Q.join(products, categories).on((e, u) -> e.getId_category() == u.getId())
                .select(CategoryProduct::from).toList();
        assertEquals(5, list.size());
        for (CategoryProduct categoryProduct : list) {
            assertEquals(categoryProduct.getClass(), CategoryProduct.class);
        }
    }

    @Test
    public void test_order_by_single() {
        List<Product> list = Q.from(products).orderBy(Product::getName, String.CASE_INSENSITIVE_ORDER).toList();
        assertEquals(list.get(0).getName(), "a");
        List<Product> list2 = Q.from(products).orderBy(Product::getName).toList();
        assertNotEquals(list2.get(0).getName(), "a");

    }

    @Test
    public void test_order_by_single_reveresed() {
        List<Product> list = Q.from(products).orderByDesc(Product::getName, String.CASE_INSENSITIVE_ORDER).toList();
        assertEquals(list.get(0).getName(), "z");
        assertEquals(list.get(list.size() - 1).getName(), "a");
        List<Product> list2 = Q.from(products).orderByDesc(Product::getName).toList();
        assertEquals(list2.get(0).getName(), "z");
        assertNotEquals(list2.get(list.size() - 1).getName(), "a");
    }

    @Test
    public void test_any() {
        boolean found = Q.from(products).any(e -> e.getId() == 1);
        boolean notFound = Q.from(products).any(e -> e.getId() == 100);
        assertEquals(found, true);
        assertEquals(notFound, false);
    }

    @Test
    public void testContains() {
        Product p = products.get(0);
        boolean found = Q.from(products).contains(p);
        boolean notFound = Q.from(products).contains(new Product("coucou", 1, 10));
        assertEquals(found, true);
        assertEquals(notFound, false);
    }

    @Test
    public void testFirstOrDefault() {
        Product p = Q.from(products).firstOrDefault();
        assertEquals(p, products.get(0));
        Product p2 = Q.from(products).firstOrDefault(e -> e.getName().equals("dog gg"));
        assertEquals(p2.getName(), "dog gg");
        Product p3 = Q.from(products).firstOrDefault(e -> e.getId() == 100, new Product("FirstOrdefault", 1, 1));
        assertEquals(p3.getName(), "FirstOrdefault");
    }

    @Test
    public void testFirstOrDefaultEmptyList() {
        Object p = Q.from(new ArrayList<Object>()).firstOrDefault(e -> e != null, null);
    }

    @Test
    public void testfirstOrThrow() {
        assertThrows(NullPointerException.class,
                () -> Q.from(products).firstOrThrow(e -> e.getId() == 100, new NullPointerException()));
    }

    @Test
    public void testConcat() {

        assertEquals(Q.from(products).concat(products).toList().size(), products.size() * 2);
        assertEquals(Q.from(products).concat(Q.from(products)).toList().size(), products.size() * 2);
    }

    @Test
    public void testGroupByLeft() {
        List<Pair<Category, List<Product>>> list = Q.join(categories, products)
                .on((c, p) -> c.getId() == (p.getId_category())).groupByLeft(Category::getId).toList();

        assertEquals(list.size(), categories.size());
    }

    @Test
    public void testSum() {
        var doubles = Arrays.asList(Pair.of("pair", 20d), Pair.of("pair", 20d), Pair.of("pair", 20.5d));
        var integers = Arrays.asList(Pair.of("pair", 1), Pair.of("pair ", 3));
        var longs = Arrays.asList(Pair.of("pair", 1L), Pair.of("pair ", 3L));

        assertEquals(Q.from(doubles).sumDouble(Pair::getRight), Double.valueOf(60.5d));
        assertEquals(Q.from(integers).sumInteger(Pair::getRight), Integer.valueOf(4));
        assertEquals(Q.from(longs).sumLong(Pair::getRight), Long.valueOf(4L));
    }

    @Test
    public void testFlat() {
        var list = Arrays.asList("1", "2", "3");
        var list2 = Arrays.asList("1", "3");
        List<String> list3 = Arrays.asList("1", "2", "3");
        var join = Arrays.asList(list, list2, list3);
        List<String> strinfs = join.stream().flatMap(Collection::stream).collect(Collectors.toList());
        List<String> result = Q.from(join).flat(e -> e).toList();

        assertEquals(result.size(), 8);
        assertEquals(result, strinfs);

    }

    @Test
    public void testJoinToString() {
        var result = Q.from(categories).toString(e -> e.getName(), ",");
        assertEquals(result, "cat,dog,bird");
    }

    @Test
    public void testJoinBySingleSelector() {
        var result = Q.from(products).join(categories).on((p, c) -> p.getId_category() == (c.getId())).toList();
        assertEquals(result.size(), 5);
    }

    @Test
    public void testDistinct() {
        var list = Arrays.asList("1", "2", "3", "1", "2", "3");
        var t = Q.from(list).distinct(e -> e).toList();
        assertEquals(t.size(), 3);
    }

    @Test
    public void testForeach() {
        Q.from(products).foreach((c) -> c.setId(1));
        for (Product product : products) {
            assertEquals(1, product.getId());
        }
    }

    private String delayedExec(String name) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(name);
        return name;
    }

    @Test
    public void testMap() {
        var map = Q.from(categories).toMap(Category::getId, Category::getName);
        assertEquals(map.get(1), "cat");
        assertNotEquals(map.get(1), "dog");
        assertEquals(map.get(2), "dog");
        assertEquals(map.get(3), "bird");
    }

    @Test
    public void testDualMap() {
        var map = Q.from(categories).join(products).on((c, p) -> c.getId() == (p.getId_category()))
                .groupByLeft(Category::getId).toMap(e -> e.getLeft(),
                        e -> e.getRight());
        assertTrue(map.get(categories.get(1)).size() == 2);
    }

    @Test
    public void testGrouping() {
        var map = Q.from(products).groupBy(Product::getId_category).toList();
        System.out.println(map);
        assertTrue(map.size() == 6);
    }

    @Test
    public void testGroupingMap() {
        var map = Q.from(products).groupBy(Product::getId_category).toMap(e -> e.getKey(), e -> e.getValue());
        System.out.println(map);
        assertTrue(map.keySet().size() == 6);
    }
}
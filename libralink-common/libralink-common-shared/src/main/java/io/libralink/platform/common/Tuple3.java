package io.libralink.platform.common;

public final class Tuple3<K, V, N> {

    private K first;
    private V second;

    private N third;

    private Tuple3() {}

    public static <K, V, N> Tuple3<K, V, N> create(K first, V second, N third) {
        Tuple3<K, V, N> tuple = new Tuple3<>();
        tuple.first = first;
        tuple.second = second;
        tuple.third = third;
        return tuple;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    public N getThird() {
        return third;
    }
}

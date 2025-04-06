package io.libralink.platform.common;

public final class Tuple2<K, V> {

    private K first;
    private V second;

    private Tuple2() {}

    public static <K, V> Tuple2<K, V> create(K first, V second) {
        Tuple2<K, V> tuple = new Tuple2<>();
        tuple.first = first;
        tuple.second = second;
        return tuple;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }
}

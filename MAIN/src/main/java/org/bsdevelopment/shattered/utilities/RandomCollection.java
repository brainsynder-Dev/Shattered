package org.bsdevelopment.shattered.utilities;

import java.util.*;
import java.util.Map.Entry;

public class RandomCollection<E> {
    private final LinkedList<E> order;
    private final NavigableMap<Double, E> map;
    private final Random random;
    private double total;

    public static <E> E randomize(Collection<E> list) {
        return randomize(list, 50);
    }

    public static <E> E randomize(Collection<E> list, int percent) {
        RandomCollection<E> collection = new RandomCollection();
        list.forEach((e) -> {
            collection.add(percent, e);
        });
        return collection.next();
    }

    public static <E> RandomCollection<E> fromCollection(Collection<E> list) {
        return fromCollection(list, 50);
    }

    public static <E> RandomCollection<E> fromCollection(Collection<E> list, int percent) {
        RandomCollection<E> collection = new RandomCollection();
        list.forEach((e) -> {
            collection.add(percent, e);
        });
        return collection;
    }

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random var1) {
        order = new LinkedList<>();
        this.map = new TreeMap();
        this.total = 0.0D;
        this.random = var1;
    }

    public void clear () {
        this.map.clear();
        this.total = 0D;
    }

    public void add(E value) {
        this.add(50.0D, value);
    }

    public void add(double percent, E value) {
        if (percent > 0.0D) {
            order.addLast(value);
            this.total += percent;
            this.map.put(this.total, value);
        }

    }

    public LinkedList<E> values() {
        return this.order;
    }

    public E next() {
        double var1 = this.random.nextDouble() * this.total;
        return this.map.ceilingEntry(var1).getValue();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public int getSize() {
        return this.map.size();
    }

    public E nextRemove() {
        if (this.map.isEmpty()) {
            return null;
        } else {
            double var1 = this.random.nextDouble() * this.total;
            Entry<Double, E> entry = this.map.ceilingEntry(var1);
            E value = entry.getValue();
            this.total -= entry.getKey();
            this.map.remove(entry.getKey());
            order.remove(value);
            return value;
        }
    }
}
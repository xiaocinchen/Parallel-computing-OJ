package com.offer.oj.util;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

@Data
public class FrequencySet<T> implements Iterable<T>, Serializable{
    private final Map<T, Integer> elementCount;
    private final TreeSet<T> sortedElements;

    public FrequencySet() {
        elementCount = new HashMap<>();
        sortedElements = new TreeSet<>(new SerializableComparator());
    }

    public void add(T element) {
        int count = elementCount.getOrDefault(element, 0);
        elementCount.put(element, count + 1);
        sortedElements.add(element);
    }

    public void remove(T element) {
        elementCount.remove(element);
        sortedElements.remove(element);
    }

    public T getMostFrequent() {
        return sortedElements.first();
    }

    public boolean containsKey(T element) {
        return sortedElements.contains(element);
    }

    public TreeSet<T> getAll() {
        return new TreeSet<>(sortedElements);
    }

    @SafeVarargs
    public static <T> FrequencySet<T> of(T... elements) {
        FrequencySet<T> set = new FrequencySet<>();
        for (T element : elements) {
            set.add(element);
        }
        return set;
    }

    @Override
    public Iterator<T> iterator() {
        return sortedElements.iterator();
    }

    public Stream<T> stream() {
        return sortedElements.stream();
    }

    public Stream<T> parallelStream() {
        return sortedElements.parallelStream();
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private class SerializableComparator implements Comparator<Object>, Serializable{
        @Override
        public int compare(Object e1, Object e2) {
            int count1 = elementCount.get(e1);
            int count2 = elementCount.get(e2);
            if (count1 != count2) {
                return Integer.compare(count2, count1); // 降序排列
            } else {
                return e1.toString().compareTo(e2.toString()); // 访问次数相同时按照元素值的字典序排列
            }
        }
    }

}

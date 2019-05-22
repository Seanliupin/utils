package com.dotgoing.utils.cat;

@FunctionalInterface
public interface Supplier<T> {

    /**
     * pass the Exception.
     *
     * @return a result
     */
    T get(Exception e);
}


package com.myplaylist.iterator;

public interface Container<T> {
    Iterator<T> getIterator();
}
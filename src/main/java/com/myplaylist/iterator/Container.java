package com.myplaylist.iterator;

// Interface ini harus public
public interface Container<T> {
    Iterator<T> getIterator();
}
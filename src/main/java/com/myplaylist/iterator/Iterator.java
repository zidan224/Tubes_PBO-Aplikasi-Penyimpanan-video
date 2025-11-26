package com.myplaylist.iterator;

// Menerapkan Generic <T> agar bisa dipakai untuk Video, User, atau tipe data lain
public interface Iterator<T> {
    boolean hasNext();

    T next();
}
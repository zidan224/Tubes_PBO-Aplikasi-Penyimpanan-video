package com.myplaylist.iterator;

public interface Iterator<T> {
    boolean hasNext();
    T next();
    
    // Fitur tambahan untuk Player (Back/Prev)
    boolean hasPrev();
    T prev();
    
    // Untuk mengambil item saat ini tanpa pindah
    T current();
}
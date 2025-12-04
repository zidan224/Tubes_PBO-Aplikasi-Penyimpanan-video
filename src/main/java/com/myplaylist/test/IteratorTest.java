package com.myplaylist.test;

import com.myplaylist.iterator.Container;
import com.myplaylist.iterator.Iterator;
import com.myplaylist.iterator.VideoListContainer;
import com.myplaylist.model.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IteratorTest {

    private Container<Video> container;
    private List<Video> dummyVideos;

    @BeforeEach
    void setUp() {
        // Data dummy
        dummyVideos = new ArrayList<>();
        dummyVideos.add(new Video(1, "Video A", "Creator 1", "Edu", 2023, "Tech", 10.0, "img/a.jpg"));
        dummyVideos.add(new Video(2, "Video B", "Creator 2", "Ent", 2023, "Vlog", 15.0, "img/b.jpg"));
        dummyVideos.add(new Video(3, "Video C", "Creator 3", "Edu", 2024, "Doc", 20.0, "img/c.jpg"));

        // Masukkan ke Container kita
        container = new VideoListContainer(dummyVideos);
    }

    @Test
    void testForwardTraversal() {
        System.out.println("=== Test 1: Maju (Next) ===");
        Iterator<Video> iterator = container.getIterator();

        // Cek Video 1
        assertTrue(iterator.hasNext(), "Harus ada video pertama");
        Video v1 = iterator.next();
        assertEquals("Video A", v1.getTitle());
        System.out.println("1. " + v1.getTitle());

        // Cek Video 2
        assertTrue(iterator.hasNext());
        Video v2 = iterator.next();
        assertEquals("Video B", v2.getTitle());
        System.out.println("2. " + v2.getTitle());

        // Cek Video 3
        assertTrue(iterator.hasNext());
        Video v3 = iterator.next();
        assertEquals("Video C", v3.getTitle());
        System.out.println("3. " + v3.getTitle());

        assertFalse(iterator.hasNext(), "Video harusnya sudah habis");
        assertNull(iterator.next(), "Jika habis, next() return null");
        
        System.out.println(">>> Status: LULUS (Maju Berhasil)");
    }

    @Test
    void testBackwardTraversal() {
        System.out.println("\n=== Test 2: Mundur (Prev) ===");
        Iterator<Video> iterator = container.getIterator();

        while(iterator.hasNext()) {
            iterator.next();
        }

        assertTrue(iterator.hasPrev(), "Harus bisa mundur dari akhir");
        Video v3 = iterator.prev();
        assertEquals("Video C", v3.getTitle());
        System.out.println("Mundur ke: " + v3.getTitle());

        assertTrue(iterator.hasPrev());
        Video v2 = iterator.prev();
        assertEquals("Video B", v2.getTitle());
        System.out.println("Mundur ke: " + v2.getTitle());
        

        System.out.println(">>> Status: LULUS (Mundur Berhasil)");
    }
    
    @Test
    void testEmptyList() {
        System.out.println("\n=== Test 3: List Kosong ===");
        Container<Video> emptyContainer = new VideoListContainer(new ArrayList<>());
        Iterator<Video> emptyIterator = emptyContainer.getIterator();
        
        assertFalse(emptyIterator.hasNext(), "List kosong tidak boleh punya next");
        assertNull(emptyIterator.next(), "Next pada list kosong harus null");
        System.out.println(">>> Status: LULUS (Aman)");
    }
}
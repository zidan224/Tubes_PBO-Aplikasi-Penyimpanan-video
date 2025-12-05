package com.myplaylist.test;

import com.myplaylist.iterator.Container;
import com.myplaylist.iterator.Iterator;
import com.myplaylist.iterator.VideoListContainer;
import com.myplaylist.model.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger; 

import static org.junit.jupiter.api.Assertions.*;

public class IteratorTest {

    private static final Logger LOGGER = Logger.getLogger(IteratorTest.class.getName());

    private static final String TITLE_A = "Video A";
    private static final String TITLE_B = "Video B";
    private static final String TITLE_C = "Video C";

    private Container<Video> container;

    @BeforeEach
    void setUp() {
        List<Video> dummyVideos = new ArrayList<>();
        
        dummyVideos.add(new Video(1, TITLE_A, "Creator 1", "Edu", 2023, "Tech", 10.0, "img/a.jpg"));
        dummyVideos.add(new Video(2, TITLE_B, "Creator 2", "Ent", 2023, "Vlog", 15.0, "img/b.jpg"));
        dummyVideos.add(new Video(3, TITLE_C, "Creator 3", "Edu", 2024, "Doc", 20.0, "img/c.jpg"));

        container = new VideoListContainer(dummyVideos);
    }

    @Test
    void testForwardTraversal() {
        LOGGER.info("=== Test 1: Maju (Next) ===");
        Iterator<Video> iterator = container.getIterator();

        // Cek Video 1
        assertTrue(iterator.hasNext(), "Harus ada video pertama");
        Video v1 = iterator.next();
        assertEquals(TITLE_A, v1.getTitle());

        // Cek Video 2
        assertTrue(iterator.hasNext());
        Video v2 = iterator.next();
        assertEquals(TITLE_B, v2.getTitle());

        // Cek Video 3
        assertTrue(iterator.hasNext());
        Video v3 = iterator.next();
        assertEquals(TITLE_C, v3.getTitle());

        // Pastikan habis
        assertFalse(iterator.hasNext(), "Video harusnya sudah habis");
        assertNull(iterator.next(), "Jika habis, next() return null");
        
        LOGGER.info(">>> Status: LULUS (Maju Berhasil)");
    }

    @Test
    void testBackwardTraversal() {
        LOGGER.info("\n=== Test 2: Mundur (Prev) ===");
        Iterator<Video> iterator = container.getIterator();

        // Maju dulu sampai akhir (index ada di ujung)
        while(iterator.hasNext()) {
            iterator.next();
        }

        // Sekarang kita tes Mundur
        assertTrue(iterator.hasPrev(), "Harus bisa mundur dari akhir");
        Video v3 = iterator.prev();
        assertEquals(TITLE_C, v3.getTitle());

        assertTrue(iterator.hasPrev());
        Video v2 = iterator.prev();
        assertEquals(TITLE_B, v2.getTitle());
        
        LOGGER.info(">>> Status: LULUS (Mundur Berhasil)");
    }
    
    @Test
    void testEmptyList() {
        LOGGER.info("\n=== Test 3: List Kosong ===");
        
        // Gunakan Collections.emptyList() agar lebih efisien
        Container<Video> emptyContainer = new VideoListContainer(Collections.emptyList());
        Iterator<Video> emptyIterator = emptyContainer.getIterator();
        
        assertFalse(emptyIterator.hasNext(), "List kosong tidak boleh punya next");
        assertNull(emptyIterator.next(), "Next pada list kosong harus null");
        
        LOGGER.info(">>> Status: LULUS (Aman)");
    }
}
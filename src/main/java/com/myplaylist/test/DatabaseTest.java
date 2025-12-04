package com.myplaylist.test;

import com.myplaylist.db.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {
    
    // Variabel global untuk menampung instance
    private Database dbReference;

    // SETUP: Dijalankan sebelum SETIAP test
    @BeforeEach
    void setUp() {
        dbReference = Database.getInstance();
    }

    // 1. TEST KETERSEDIAAN (Non-Null)
    @Test
    void testInstanceIsNotNull() {
        System.out.println("Test 1: Cek Ketersediaan (Not Null)...");
        assertNotNull(dbReference, "Gagal: Instance Database null!");
        System.out.println(">>> LULUS");
    }

    // 2. TEST IDENTITAS (Singleton Pattern)
    @Test
    void testSingletonIdentity() {
        System.out.println("Test 2: Cek Identitas Singleton...");
        
        Database dbNewCall = Database.getInstance();

        System.out.println("HashCode Awal: " + System.identityHashCode(dbReference));
        System.out.println("HashCode Baru: " + System.identityHashCode(dbNewCall));

        assertSame(dbReference, dbNewCall, "Gagal: Objek tidak identik! Singleton gagal.");
        System.out.println(">>> LULUS");
    }

    // 3. TEST FUNGSIONALITAS (Koneksi Database)
    @Test
    void testConnectionValidity() {
        System.out.println("Test 3: Cek Koneksi MySQL...");
        try {
            boolean isValid = dbReference.getConnection().isValid(2);
            assertTrue(isValid, "Gagal: Koneksi database tidak valid/gagal connect.");
            System.out.println(">>> LULUS");
        } catch (Exception e) {
            fail("Terjadi Error SQL: " + e.getMessage());
        }
    }
}
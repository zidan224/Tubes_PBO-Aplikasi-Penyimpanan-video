package com.myplaylist.test;

import com.myplaylist.db.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.logging.Level;
import java.util.logging.Logger; 

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {
    
    // Setup Logger
    private static final Logger LOGGER = Logger.getLogger(DatabaseTest.class.getName());

    // Setup Konstanta (Solusi untuk warning duplikasi string ">>> LULUS")
    private static final String SUCCESS_MSG = ">>> LULUS";

    private Database dbReference;

    @BeforeEach
    void setUp() {
        dbReference = Database.getInstance();
    }

    // TEST KETERSEDIAAN (Non-Null)
    @Test
    void testInstanceIsNotNull() {
        LOGGER.info("Test 1: Cek Ketersediaan (Not Null)...");
        
        assertNotNull(dbReference, "Gagal: Instance Database null!");
        
        LOGGER.info(SUCCESS_MSG); // Gunakan konstanta
    }

    // TEST IDENTITAS (Singleton Pattern)
    @Test
    void testSingletonIdentity() {
        LOGGER.info("Test 2: Cek Identitas Singleton...");
        
        Database dbNewCall = Database.getInstance();

        // Gunakan Logger.info untuk mencetak hasil
        LOGGER.log(Level.INFO, "HashCode Awal: {0}", System.identityHashCode(dbReference));
        LOGGER.log(Level.INFO, "HashCode Baru: {0}", System.identityHashCode(dbNewCall));

        assertSame(dbReference, dbNewCall, "Gagal: Objek tidak identik! Singleton gagal.");
        LOGGER.info(SUCCESS_MSG); 
    }

    // TEST FUNGSIONALITAS (Koneksi Database)
    @Test
    void testConnectionValidity() {
        LOGGER.info("Test 3: Cek Koneksi MySQL...");
        try {
            boolean isValid = dbReference.getConnection().isValid(2);
            assertTrue(isValid, "Gagal: Koneksi database tidak valid/gagal connect.");
            
            LOGGER.info(SUCCESS_MSG); // Gunakan konstanta
        } catch (Exception e) {
            fail("Terjadi Error SQL: " + e.getMessage());
        }
    }
}
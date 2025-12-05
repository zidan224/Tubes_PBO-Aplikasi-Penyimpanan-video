package com.myplaylist.test;

import com.myplaylist.facade.AppFacade;
import com.myplaylist.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.logging.Level;
import java.util.logging.Logger; 

import static org.junit.jupiter.api.Assertions.*;

public class FacadeTest {

    // 2. Inisialisasi Logger
    private static final Logger LOGGER = Logger.getLogger(FacadeTest.class.getName());

    private AppFacade appFacade;

    @BeforeEach
    void setUp() {
        appFacade = new AppFacade();
    }

    // TEST 1: Cek Proteksi Watchlist (Security Check)
    @Test
    void testAccessWatchlistWithoutLogin() {
        LOGGER.info("\n=== Test 1: Proteksi Watchlist (Tanpa Login) ===");
        
        assertNull(appFacade.getCurrentUser(), "Awalnya user harus null");

        String result = appFacade.addToWatchlist(1);
        
        assertEquals("Please login first.", result, "Harus menolak akses jika belum login");
        
        LOGGER.log(Level.INFO, ">>> Hasil: {0}", result);
        LOGGER.info(">>> Status: LULUS (Sistem menolak akses)");
    }

    // TEST 2: Alur Register -> Login -> Cek State -> Logout
    @Test
    void testUserSessionFlow() {
        LOGGER.info("\n=== Test 2: Alur User Session (Register-Login-Logout) ===");

        // 1. REGISTER USER BARU 
        String uniqueUser = "UserTes_" + System.currentTimeMillis();
        String password = "password123";
        
        boolean regSuccess = appFacade.register(uniqueUser, password);
        assertTrue(regSuccess, "Register user baru harus berhasil");
        
        LOGGER.log(Level.INFO, "1. Register User: {0}", uniqueUser);

        // 2. LOGIN
        boolean loginSuccess = appFacade.login(uniqueUser, password);
        assertTrue(loginSuccess, "Login harus berhasil dengan user yang baru dibuat");
        
        User currentUser = appFacade.getCurrentUser();
        assertNotNull(currentUser, "Setelah login, currentUser tidak boleh null");
        assertEquals(uniqueUser, currentUser.getUsername(), "Username yang login harus sesuai");
        LOGGER.info("2. Login Sukses. Current User: " + currentUser.getUsername());

        // 3. LOGOUT
        appFacade.logout();
        assertNull(appFacade.getCurrentUser(), "Setelah logout, currentUser harus null kembali");
        LOGGER.info("3. Logout Sukses.");
        
        LOGGER.info(">>> Status: LULUS (Alur Session Valid)");
    }

    // TEST 3: Cek Login Gagal
    @Test
    void testLoginFailed() {
        LOGGER.info("\n=== Test 3: Login Gagal ===");
        
        boolean result = appFacade.login("user_hantu_belau", "password_ngawur");
        
        assertFalse(result, "Login harus gagal jika user tidak ada");
        assertNull(appFacade.getCurrentUser(), "User tidak boleh tersimpan jika login gagal");
        
        LOGGER.info(">>> Status: LULUS (Login gagal ditangani dengan benar)");
    }
}
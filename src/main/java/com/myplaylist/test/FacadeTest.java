package com.myplaylist.test;

import com.myplaylist.facade.AppFacade;
import com.myplaylist.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FacadeTest {

    private AppFacade appFacade;

    @BeforeEach
    void setUp() {
        appFacade = new AppFacade();
    }

    // TEST 1: Cek Proteksi Watchlist (Security Check)
    // Skenario: User mencoba akses fitur watchlist TANPA login
    @Test
    void testAccessWatchlistWithoutLogin() {
        System.out.println("\n=== Test 1: Proteksi Watchlist (Tanpa Login) ===");
        
        assertNull(appFacade.getCurrentUser(), "Awalnya user harus null");

        String result = appFacade.addToWatchlist(1);
        
        assertEquals("Please login first.", result, "Harus menolak akses jika belum login");
        
        System.out.println(">>> Hasil: " + result);
        System.out.println(">>> Status: LULUS (Sistem menolak akses)");
    }

    // TEST 2: Alur Register -> Login -> Cek State -> Logout
    // Skenario Lengkap User Journey
    @Test
    void testUserSessionFlow() {
        System.out.println("\n=== Test 2: Alur User Session (Register-Login-Logout) ===");

        // 1. REGISTER USER BARU 
        String uniqueUser = "UserTes_" + System.currentTimeMillis();
        String password = "password123";
        
        boolean regSuccess = appFacade.register(uniqueUser, password);
        assertTrue(regSuccess, "Register user baru harus berhasil");
        System.out.println("1. Register User: " + uniqueUser);

        // 2. LOGIN
        boolean loginSuccess = appFacade.login(uniqueUser, password);
        assertTrue(loginSuccess, "Login harus berhasil dengan user yang baru dibuat");
        
        // Cek apakah State Facade berubah (currentUser terisi)
        User currentUser = appFacade.getCurrentUser();
        assertNotNull(currentUser, "Setelah login, currentUser tidak boleh null");
        assertEquals(uniqueUser, currentUser.getUsername(), "Username yang login harus sesuai");
        System.out.println("2. Login Sukses. Current User: " + currentUser.getUsername());

        // 3. LOGOUT
        appFacade.logout();
        assertNull(appFacade.getCurrentUser(), "Setelah logout, currentUser harus null kembali");
        System.out.println("3. Logout Sukses.");
        
        System.out.println(">>> Status: LULUS (Alur Session Valid)");
    }

    // TEST 3: Cek Login Gagal
    @Test
    void testLoginFailed() {
        System.out.println("\n=== Test 3: Login Gagal ===");
        
        boolean result = appFacade.login("user_hantu_belau", "password_ngawur");
        
        assertFalse(result, "Login harus gagal jika user tidak ada");
        assertNull(appFacade.getCurrentUser(), "User tidak boleh tersimpan jika login gagal");
        
        System.out.println(">>> Status: LULUS (Login gagal ditangani dengan benar)");
    }
}
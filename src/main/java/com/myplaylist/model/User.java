package com.myplaylist.model;

public class User {
    private int id; // <--- TAMBAHAN PENTING
    private String username;
    private String password;
    private String role;

    // Constructor untuk INSERT (Belum punya ID)
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Constructor untuk SELECT (Sudah punya ID dari DB)
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
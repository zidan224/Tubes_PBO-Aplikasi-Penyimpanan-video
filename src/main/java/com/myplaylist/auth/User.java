package com.myplaylist.auth;

public class User {
    private String username;
    private String password;
    private String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Overloaded constructor for login/find operations where role might not be immediately needed
    public User(String username, String password) {
        this(username, password, "user"); // Default to "user"
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}

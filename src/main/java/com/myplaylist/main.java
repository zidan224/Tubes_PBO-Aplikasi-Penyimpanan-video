package com.myplaylist;

import com.myplaylist.db.Database;
import com.myplaylist.ui.LoginGUI;
import javax.swing.SwingUtilities;

public class main {
    public static void main(String[] args) {
        // Create database and tables if they don't exist
        Database.createTablesAndDummyData();

        // Start the application
        SwingUtilities.invokeLater(() -> {
            new LoginGUI().setVisible(true);
        });
    }
}
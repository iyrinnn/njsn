// ====================================================================================
// Package: com.yourorg.app
// File: MainApp.java
// Description: The main entry point for the Smart Revision & Resource Organizer application.
// ====================================================================================
package com.yourorg.app;
 // Fixed: Changed from 'package com;' to 'package com.yourorg.app;'

import com.yourorg.app.gui.MainApplicationFrame;
import com.yourorg.app.service.DataManager;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        // Set a modern look and feel if available
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to default if not available
            System.err.println("Could not set System Look and Feel: " + e.getMessage());
        }

        // Ensure the GUI is created and updated on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Create DataManager instance
            DataManager dataManager = new DataManager();

            // Create and show the main application frame
            MainApplicationFrame frame = new MainApplicationFrame(dataManager);
            frame.setVisible(true);
        });
    }
}

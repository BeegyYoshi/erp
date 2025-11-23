package edu.univ.erp;

import edu.univ.erp.ui.LoginPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private CardLayout layout = new CardLayout();
    private JPanel container = new JPanel(layout);

    // Map panel name → panel instance
    private Map<String, JPanel> screens = new HashMap<>();

    public MainFrame() {
        super("University ERP");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Load ONLY the login panel
        loadPanel("login", new LoginPanel(this));

        add(container);
        setVisible(true);

        // Show login screen initially
        showScreen("login");
    }

    // Add a new panel
    public void loadPanel(String name, JPanel panel) {
        screens.put(name, panel);
        container.add(panel, name);
    }

    // Switch to a panel
    public void showScreen(String name) {
        layout.show(container, name);
    }
}

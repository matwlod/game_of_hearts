package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.example.Client;
public class Login {
    private static final int SCREEN_WIDTH = 1000;
    private static final int SCREEN_HEIGHT = 1000;

    public static void main(String[] args) {
        Client test=new org.example.Client();




        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Multi-Screen App");
            frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create panels for each screen
            JPanel mainScreen = createMainScreen();
            JPanel loginScreen = createLoginScreen();
            JPanel registerScreen = createRegisterScreen();

            // Create a CardLayout for switching between screens
            CardLayout cardLayout = new CardLayout();
            JPanel cardPanel = new JPanel(cardLayout);
            cardPanel.add(mainScreen, "Main");
            cardPanel.add(loginScreen, "Login");
            cardPanel.add(registerScreen, "Register");

            frame.getContentPane().add(cardPanel);

            // Set up action listeners for buttons
            JButton loginButton = new JButton("Login");
            JButton registerButton = new JButton("Register");

            loginButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
            registerButton.addActionListener(e -> cardLayout.show(cardPanel, "Register"));

            mainScreen.add(loginButton);
            mainScreen.add(registerButton);

            // Make the frame visible
            frame.setVisible(true);
        });
    }

    private static JPanel createMainScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        return panel;
    }

    private static JPanel createLoginScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        JLabel nameLabel = new JLabel("Username:");
        JTextField nameField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton backButton = new JButton("Back");
        JButton loginButton = new JButton("Login");

        backButton.addActionListener(e -> {
            CardLayout layout = (CardLayout) panel.getParent().getLayout();
            layout.show(panel.getParent(), "Main");
        });

        loginButton.addActionListener(e -> {
            String username = nameField.getText();
            String password = new String(passField.getPassword());
            System.out.println("Login: Username=" + username + ", Password=" + password);
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(backButton);
        panel.add(loginButton);

        return panel;
    }

    private static JPanel createRegisterScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        JLabel nameLabel = new JLabel("Username:");
        JTextField nameField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton backButton = new JButton("Back");
        JButton registerButton = new JButton("Register");

        backButton.addActionListener(e -> {
            CardLayout layout = (CardLayout) panel.getParent().getLayout();
            layout.show(panel.getParent(), "Main");
        });

        registerButton.addActionListener(e -> {
            String username = nameField.getText();
            String password = new String(passField.getPassword());
            System.out.println("Register: Username=" + username + ", Password=" + password);
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(backButton);
        panel.add(registerButton);

        return panel;
    }
}

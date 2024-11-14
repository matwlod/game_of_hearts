package org.example;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class Auth {




    public static boolean registerUser(String username, String hashedPassword) {
        String url = "jdbc:mysql://localhost:3306/java";
        String user = "java";
        String pass = "java";

        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            String query = "INSERT INTO users (nickname, password) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, hashedPassword);

                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected>0;
            }
        } catch (SQLException e) {

            if (e.getSQLState().equals("23000")) {
                return false;
            } else {
                e.printStackTrace();
                return false;
            }
        }

    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password.", e);
        }
    }

    public static boolean checkLogin(String username,String password) {

        String url = "jdbc:mysql://localhost:3306/java";
        String user = "java";
        String pass = "java";
        String hashedPassword=hashPassword(password);
        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            String query = "SELECT * FROM users WHERE nickname = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, hashedPassword);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // Returns true if there is a match
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
   }
}

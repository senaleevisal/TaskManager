package edu.nibm.taskmanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterDialog extends JDialog {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnRegister;
    private boolean successfulRegistration;

    private Frame parent;

    public RegisterDialog(Frame parent) {
        super(parent, "Register", true);
        this.parent = parent;
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(235, 245, 251)); // Light blue background
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbUsername = new JLabel("Username: ");
        lbUsername.setForeground(Color.DARK_GRAY);
        lbUsername.setFont(new Font("Arial", Font.BOLD, 14));
        cs.gridx = 0; cs.gridy = 0; cs.gridwidth = 1;
        cs.insets = new Insets(10, 10, 10, 10);
        panel.add(lbUsername, cs);

        tfUsername = new JTextField(20);
        tfUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        cs.gridx = 1; cs.gridy = 0; cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        JLabel lbPassword = new JLabel("Password: ");
        lbPassword.setForeground(Color.DARK_GRAY);
        lbPassword.setFont(new Font("Arial", Font.BOLD, 14));
        cs.gridx = 0; cs.gridy = 1; cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        pfPassword = new JPasswordField(20);
        pfPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        cs.gridx = 1; cs.gridy = 1; cs.gridwidth = 2;
        panel.add(pfPassword, cs);

        btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegister.setForeground(Color.black);
        btnRegister.setBackground(new Color(13, 59, 102));
        btnRegister.addActionListener(e -> {
            if (registerUser(getUsername(), getPassword())) {
                JOptionPane.showMessageDialog(RegisterDialog.this,
                        "You have successfully registered.",
                        "Register", JOptionPane.INFORMATION_MESSAGE);
                successfulRegistration = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(RegisterDialog.this,
                        "Registration failed. Username might already exist.",
                        "Register", JOptionPane.ERROR_MESSAGE);
                tfUsername.setText("");
                pfPassword.setText("");
                successfulRegistration = false;
            }
        });

        JPanel bp = new JPanel();
        bp.setBackground(panel.getBackground());
        bp.add(btnRegister);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private String getUsername() {
        return tfUsername.getText().trim();
    }

    private String getPassword() {
        return new String(pfPassword.getPassword());
    }

    private boolean registerUser(String username, String password) {
        // Connect to the database
        try (Connection conn = DatabaseConnection.connect()) {
            // Check if the username already exists
            String checkUserSql = "SELECT COUNT(*) FROM Users WHERE Username = ?";
            try (PreparedStatement checkUserStmt = conn.prepareStatement(checkUserSql)) {
                checkUserStmt.setString(1, username);
                ResultSet resultSet = checkUserStmt.executeQuery();
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // Username already exists
                    return false;
                }
            }

            // Insert the new user into the database
            String insertUserSql = "INSERT INTO Users (Username, Password) VALUES (?, ?)";
            try (PreparedStatement insertUserStmt = conn.prepareStatement(insertUserSql)) {
                insertUserStmt.setString(1, username);
                // In a real application, you should hash the password here
                insertUserStmt.setString(2, password);
                insertUserStmt.executeUpdate();
                new LoginDialog(new TaskManager()).setVisible(true);
                return true; // Registration successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Registration failed due to SQL error
        }
    }


    public boolean isRegistrationSuccessful() {
        return successfulRegistration;
    }
}

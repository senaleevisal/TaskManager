package edu.nibm.taskmanager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginDialog extends JDialog {
    private final JTextField tfUsername;
    private final JPasswordField pfPassword;
    private final JButton btnLogin;
    private final JButton btnRegister; // Button to switch to the registration dialog
    private boolean succeeded;

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(235, 245, 251)); // Light blue background
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;

        // Username Label and Field
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

        // Password Label and Field
        JLabel lbPassword = new JLabel("Password: ");
        lbPassword.setForeground(Color.DARK_GRAY);
        lbPassword.setFont(new Font("Arial", Font.BOLD, 14));
        cs.gridx = 0; cs.gridy = 1; cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        pfPassword = new JPasswordField(20);
        pfPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        cs.gridx = 1; cs.gridy = 1; cs.gridwidth = 2;
        panel.add(pfPassword, cs);

        // Login Button
        btnLogin = new JButton("Login");
        styleButton(btnLogin);
        btnLogin.addActionListener(e -> {

            if (authenticate(getUsername(), getPassword())) {
                JOptionPane.showMessageDialog(LoginDialog.this,
                        "Hi " + getUsername() + "! You have successfully logged in.",
                        "Login", JOptionPane.INFORMATION_MESSAGE);
                succeeded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginDialog.this,
                        "Invalid username or password",
                        "Login", JOptionPane.ERROR_MESSAGE);
                tfUsername.setText("");
                pfPassword.setText("");
                succeeded = false;
            }
        });

        // Register Button
        btnRegister = new JButton("Register");
        styleButton(btnRegister);
        btnRegister.addActionListener(e -> {
            RegisterDialog registerDialog = new RegisterDialog((Frame) this.getParent());
            this.hide();
            registerDialog.setVisible(true);
        });

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bp.setBackground(new Color(235, 245, 251)); // Light blue background
        bp.add(btnLogin);
        bp.add(btnRegister);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public String getUsername() {
        return tfUsername.getText().trim();
    }

    public String getPassword() {
        return new String(pfPassword.getPassword());
    }


    private void styleButton(JButton button) {
        button.setBackground(new Color(33, 37, 41)); // Bootstrap's dark color
        button.setForeground(Color.black);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
    }

    public boolean isSucceeded() {
        return succeeded;
    }
    private boolean authenticate(String username, String password) {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement("SELECT UserId, Password FROM Users WHERE Username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getString("Password").equals(password)) {
                TaskManager.loggedInUserId = rs.getInt("UserId"); // Store the logged-in user's UserId
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}


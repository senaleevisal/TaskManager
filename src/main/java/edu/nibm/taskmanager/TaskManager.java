package edu.nibm.taskmanager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class TaskManager extends JFrame {
    private JTable tableTasks;
    private DefaultTableModel tableModel;
    private JButton btnAddTask, btnRefresh;
    public static int loggedInUserId; // Assume this is set during the login process

    public TaskManager() {
        setTitle("Task Manager");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(202, 231, 185));

        initUIComponents();
    }

    private void initUIComponents() {
        // Define table columns
        String[] columnNames = {"Description", "Duration"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tableTasks = new JTable(tableModel);
        tableTasks.setFont(new Font("Arial", Font.PLAIN, 14));
        tableTasks.setRowHeight(22);
        tableTasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Customizing table look
        JScrollPane scrollPane = new JScrollPane(tableTasks);
        scrollPane.setBackground(new Color(229, 250, 232));
        scrollPane.setForeground(new Color(38, 50, 56));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(getContentPane().getBackground());

        btnAddTask = new JButton("Add Task");
        customizeButton(btnAddTask);
        btnAddTask.addActionListener(e -> addTask());
        panel.add(btnAddTask);

        btnRefresh = new JButton("Refresh");
        customizeButton(btnRefresh);
        btnRefresh.addActionListener(e -> refreshTasks());
        panel.add(btnRefresh);

        getContentPane().add(panel, BorderLayout.SOUTH);

        refreshTasks(); // Load tasks initially
    }

    private void customizeButton(JButton button) {
        button.setBackground(new Color(25, 118, 210));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    private void refreshTasks() {
        tableModel.setRowCount(0); // Clear existing data
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Description, Duration FROM Tasks WHERE UserId = " + loggedInUserId)) {
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("Description"));
                row.add(rs.getString("Duration"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error refreshing tasks: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTask() {
        JTextField descriptionField = new JTextField();
        JTextField durationField = new JTextField();
        Object[] message = {
                "Task Description:", descriptionField,
                "Task Duration (HH:MM:SS):", durationField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add New Task", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String description = descriptionField.getText();
            String duration = durationField.getText();
            if (!description.trim().isEmpty() && !duration.trim().isEmpty()) {
                try (Connection conn = DatabaseConnection.connect();
                     PreparedStatement ps = conn.prepareStatement("INSERT INTO Tasks (UserId, Description, Duration) VALUES (?, ?, ?)")) {
                    ps.setInt(1, loggedInUserId);
                    ps.setString(2, description);
                    ps.setString(3, duration);
                    ps.executeUpdate();
                    refreshTasks();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error adding task: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaskManager app = new TaskManager();
            LoginDialog loginDlg = new LoginDialog(app);
            loginDlg.setVisible(true);
            if (loginDlg.isSucceeded()) {
                app.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}

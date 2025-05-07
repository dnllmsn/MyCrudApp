import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Crud1 {
    private JTextField nameField;
    private JTextField emailField;
    private JTextArea displayArea;
    private int offset;
    private final DatabaseConfig dbConfig;
    private final DatabaseConnection dbConnection;

    public Crud1() {
        // Initialize DatabaseConfig with connection details
        dbConfig = new DatabaseConfig("root", "", "jdbc:mysql://localhost:3306/myoop");
        dbConnection = new DatabaseConnection(dbConfig);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Crud1().createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        offset = 0;

        JFrame frame = new JFrame("User Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(600, 700));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Pateros Technological College", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Object Oriented Programming - JDBC", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        // System name
        JLabel systemNameLabel = new JLabel("SAMPLE CRUD DATABASE", JLabel.CENTER);
        systemNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridy = 2;
        mainPanel.add(systemNameLabel, gbc);

        // Data Entry Panel
        JPanel entryPanel = new JPanel(new GridBagLayout());
        entryPanel.setBorder(BorderFactory.createTitledBorder("Data Entry"));
        GridBagConstraints epc = new GridBagConstraints();
        epc.insets = new Insets(5, 5, 5, 5);
        epc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(20);
        emailField = new JTextField(20);

        epc.gridx = 0;
        epc.gridy = 0;
        entryPanel.add(new JLabel("Name:"), epc);
        epc.gridx = 1;
        entryPanel.add(nameField, epc);

        epc.gridx = 0;
        epc.gridy = 1;
        entryPanel.add(new JLabel("Email:"), epc);
        epc.gridx = 1;
        entryPanel.add(emailField, epc);

        JButton clearButton = new JButton("Clear Fields");
        clearButton.addActionListener(e -> {
            nameField.setText("");
            emailField.setText("");
        });
        epc.gridx = 0;
        epc.gridy = 2;
        epc.gridwidth = 2;
        entryPanel.add(clearButton, epc);

        gbc.gridy = 3;
        mainPanel.add(entryPanel, gbc);

        // Control Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        JButton addButton = new JButton("Add User");
        addButton.addActionListener(e -> addUser());
        buttonPanel.add(addButton);

        JButton showButton = new JButton("Show Records");
        showButton.addActionListener(e -> showRecords());
        buttonPanel.add(showButton);

        JButton searchButton = new JButton("Search User");
        searchButton.addActionListener(e -> searchUser());
        buttonPanel.add(searchButton);

        JButton editButton = new JButton("Edit User");
        editButton.addActionListener(e -> editUser());
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Delete User");
        deleteButton.addActionListener(e -> deleteUser());
        buttonPanel.add(deleteButton);

        gbc.gridy = 4;
        mainPanel.add(buttonPanel, gbc);

        // Display Area
        displayArea = new JTextArea(15, 40);
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        gbc.gridy = 5;
        mainPanel.add(scrollPane, gbc);

        // Footer
        JLabel footerLabel = new JLabel("Prepared by JSTJR 2024", JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 6;
        mainPanel.add(footerLabel, gbc);

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private void addUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Name and Email cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AddUser addUser = new AddUser(dbConnection);
        addUser.addUser(name, email);
        nameField.setText("");
        emailField.setText("");
        showRecords(0); // Refresh display
        JOptionPane.showMessageDialog(null, "User added successfully!");
    }

    private void showRecords() {
        ShowRecords showRecords = new ShowRecords(dbConnection);
        displayArea.setText(showRecords.getRecords(offset));
        offset += 10;
    }

    private void showRecords(int newOffset) {
        offset = newOffset;
        showRecords();
    }

    private void searchUser() {
        String input = JOptionPane.showInputDialog("Enter User ID to search:");
        if (input == null || input.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(input.trim());
            SearchUser searchUser = new SearchUser(dbConnection);
            String result = searchUser.searchById(id);
            displayArea.setText(result);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid numeric ID", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editUser() {
        String idInput = JOptionPane.showInputDialog("Enter User ID to edit:");
        if (idInput == null || idInput.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(idInput.trim());
            String newName = JOptionPane.showInputDialog("Enter new name:");
            if (newName == null) return;
            
            String newEmail = JOptionPane.showInputDialog("Enter new email:");
            if (newEmail == null) return;

            EditUser editUser = new EditUser(dbConnection);
            editUser.editUser(id, newName.trim(), newEmail.trim());
            showRecords(0); // Refresh display
            JOptionPane.showMessageDialog(null, "User updated successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid numeric ID", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        String input = JOptionPane.showInputDialog("Enter User ID to delete:");
        if (input == null || input.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(input.trim());
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Are you sure you want to delete user with ID " + id + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                DeleteUser deleteUser = new DeleteUser(dbConnection);
                deleteUser.deleteUser(id);
                showRecords(0); // Refresh display
                JOptionPane.showMessageDialog(null, "User deleted successfully!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid numeric ID", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

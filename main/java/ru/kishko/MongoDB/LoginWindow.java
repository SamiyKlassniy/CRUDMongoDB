package ru.kishko.MongoDB;

import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import org.bson.Document;

import javax.swing.*;

public class LoginWindow extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;
    private Connection connection;
    public static String collectionString;
    private JPanel authPanel;
    private JTabbedPane tabbedPane;
    private Document user;
    public static String role;
    public static String login;

    public LoginWindow(String collectionString) {

        LoginWindow.collectionString = collectionString;

        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel loginLabel = new JLabel("Login:");
        loginField = new JTextField(10);
        JPanel loginPanel = new JPanel();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(10);
        JPanel passwordPanel = new JPanel();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            login = loginField.getText();
            String password = String.valueOf(passwordField.getPassword());

            connection = new Connection(login, password);

            try {

                user = Connection.userCollection.find(new Document("username", login)).first();

                role = user.getString("role");

                System.out.println(role);

                if (role.equals("admin")) {
                    dispose();
                    new AdminWindow(connection.getCollection());
                } else {
                    dispose();
                    new UserWindow(connection.getCollection());
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Unknown user", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        loginPanel.add(loginLabel);
        loginPanel.add(loginField);

        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        panel.add(loginPanel);
        panel.add(passwordPanel);
        panel.add(loginButton);

        add(panel);
        setVisible(true);
    }

}
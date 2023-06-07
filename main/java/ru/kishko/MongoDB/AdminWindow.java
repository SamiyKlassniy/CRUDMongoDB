package ru.kishko.MongoDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

public class AdminWindow extends JFrame {
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField ageField;
    private JButton addButton;
    private JButton removeButton;
    private JButton updateButton;
    private JTable table;
    private DefaultTableModel model;
    private MongoCollection<Document> collection;
    private boolean setVisible = true;

    // конструктор класса
    public AdminWindow(MongoCollection<Document> collection) {
        super("AdminWindow");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // создаем панель с полями ввода текста
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Surname:"));
        surnameField = new JTextField();
        inputPanel.add(surnameField);
        inputPanel.add(new JLabel("Age:"));
        ageField = new JTextField();
        inputPanel.add(ageField);

        // создаем кнопку добавления записи
        addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String surname = surnameField.getText();
            Integer age = Integer.parseInt(ageField.getText());

            // проверяем, что поля не пустые
            if (name.isEmpty() || surname.isEmpty() || ageField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Some fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // добавляем запись в MongoDB
            PersonDAO.create(new Person(name, surname, age));

            // обновляем таблицу
            updateTable();
        });

        // создаем кнопку удаления записи
        removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> {
            int index = table.getSelectedRow();

            // проверяем, что выбрана запись
            if (index == -1) {
                JOptionPane.showMessageDialog(null, "Select a record to remove", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // удаляем запись из MongoDB
            String id = model.getValueAt(index, 0).toString();

            PersonDAO.delete(id);

            System.out.println("Removed " + PersonDAO.read(id));
            // обновляем таблицу
            updateTable();
        });

        // создаем кнопку обновления записи
        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            int index = table.getSelectedRow();

            // проверяем, что выбрана запись
            if (index == -1) {
                JOptionPane.showMessageDialog(null, "Select a record to update", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // обновляем запись в MongoDB
            String id = model.getValueAt(index, 0).toString();
            String name = model.getValueAt(index, 1).toString();
            String surname = model.getValueAt(index, 2).toString();
            Integer age = Integer.parseInt(model.getValueAt(index, 3).toString());

            PersonDAO.update(new Person(name, surname, age), id);

            System.out.println("Updated " + PersonDAO.read(id));

            // обновляем таблицу
            updateTable();
        });

        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> {
            dispose();
            LoginWindow loginWindow = new LoginWindow(LoginWindow.collectionString);
            loginWindow.setVisible(true);
        });

        JButton userButton = new JButton("Go to user's page");
        userButton.addActionListener(e -> {
            dispose();
            UserWindow userWindow = new UserWindow(collection);
            userWindow.setVisible(true);
        });

        // создаем панель с кнопками
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(backButton);
        buttonPanel.add(userButton);

        // создаем модель таблицы и заполняем ее данными из коллекции
        model = new DefaultTableModel();
        table = new JTable(model);

        // создаем панель с таблицей для отображения данных
        JPanel tablePanel = new JPanel(new BorderLayout());
        JScrollPane tableScroll = new JScrollPane(table);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        // объединяем панели в одну
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        // выводим панель на экран
        setContentPane(panel);
        setSize(600, 400);
        setLocationRelativeTo(null);

        setCollection(collection);

        setVisible(setVisible);
    }

    private void updateTable() {

        nameField.setText("");
        surnameField.setText("");
        ageField.setText("");
        model.setRowCount(0);
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            String[] fields = doc.keySet().toArray(new String[doc.keySet().size()]);
            if (model.getColumnCount() == 0) {
                model.setColumnIdentifiers(fields);
            }
            Object[] vals = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                vals[i] = doc.get(fields[i]);
            }
            model.addRow(vals);
        }
        cursor.close();

    }

    // устанавливаем коллекцию данных MongoDB
    public void setCollection(MongoCollection<Document> collection) {
        this.collection = collection;
        try {
            updateTable();
        } catch (Exception e) {
            setVisible = false;
            dispose();
            JOptionPane.showMessageDialog(null, "Unknown user", "Error", JOptionPane.ERROR_MESSAGE);
            new LoginWindow(MongoDB.collectionString);
        }
    }

}

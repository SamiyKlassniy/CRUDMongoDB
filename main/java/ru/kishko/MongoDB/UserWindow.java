package ru.kishko.MongoDB;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;


public class UserWindow extends JFrame {

    private JTextField textField1, textField2, textField3;
    private JButton button1, button2, button3;
    private JTable table;
    private DefaultTableModel tableModel;

    private MongoCollection<Document> collection;

    public UserWindow(MongoCollection<Document> collection) {
        super("UserWindow");

        this.collection = collection;
        // Создаем поля ввода
        textField1 = new JTextField(10);
        textField2 = new JTextField(10);
        textField3 = new JTextField(10);

        // Создаем кнопки
        button1 = new JButton("Кнопка");
        button2 = new JButton("Кнопка");
        button3 = new JButton("Кнопка");

        // Создаем таблицу и модель таблицы
        tableModel = new DefaultTableModel();
        tableModel.addColumn("id");
        tableModel.addColumn("name");
        tableModel.addColumn("surname");
        tableModel.addColumn("age");
        table = new JTable(tableModel);

        // Добавляем элементы на панель
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));
        panel.add(new JLabel("Поиск по имени:"));
        panel.add(textField1);
        panel.add(button1);
        panel.add(new JLabel("Поиск до возраста включительно:"));
        panel.add(textField2);
        panel.add(button2);
        panel.add(new JLabel("Поиск больше возраста:"));
        panel.add(textField3);
        panel.add(button3);
        // Добавляем панель и таблицу на окно
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        // Добавляем обработчики событий на кнопки
        button1.addActionListener(e -> {

            if (checkField(textField1))
            {

                tableModel.setRowCount(0);
                List<Bson> pipeline = Arrays.asList(
                        match(regex("name", ".*" + textField1.getText() + ".*")), // ищем документы, у которых имя начинается с заданной буквы
                        project(fields(include("_id", "name", "surname", "age"))) // выбираем только нужные поля
                );
                // выполняем агрегацию и выводим результаты

                Aggregation(pipeline);
            }

        });

        button2.addActionListener(e -> {

            if (checkField(textField2)) {

                // Aggregation pipeline stages
                Document match = new Document("$match", Filters.lt("age",
                        Integer.parseInt(textField2.getText())));
                Document project = new Document("$project",
                        Projections.include("_id", "name", "surname", "age"));
                Document sort = new Document("$sort",
                        Sorts.ascending("age"));

                // Aggregation pipeline
                List<Document> pipeline = new ArrayList<>();
                pipeline.add(match);
                pipeline.add(project);
                pipeline.add(sort);

                Aggregation(pipeline);
            }

        });

        button3.addActionListener(e -> {

            if (checkField(textField3)) {

                // Aggregation pipeline stages
                Document match = new Document("$match", Filters.gt("age",
                        Integer.parseInt(textField3.getText())));
                Document project = new Document("$project",
                        Projections.include("_id", "name", "surname", "age"));
                Document sort = new Document("$sort",
                        Sorts.ascending("age"));
                // Aggregation pipeline
                List<Document> pipeline = new ArrayList<>();
                pipeline.add(match);
                pipeline.add(project);
                pipeline.add(sort);

                Aggregation(pipeline);
            }

        });

        JButton backButton = new JButton("Back to Login");
        panel.add(backButton, BorderLayout.SOUTH);
        backButton.addActionListener(e -> {
            dispose();
            LoginWindow loginWindow = new LoginWindow(LoginWindow.collectionString);
            loginWindow.setVisible(true);
        });

        JButton adminButton = new JButton("Go to admin's page");
        panel.add(adminButton, BorderLayout.SOUTH);
        adminButton.addActionListener(e -> {
            if (LoginWindow.role.equals("admin")) {
                dispose();
                AdminWindow adminWindow = new AdminWindow(collection);
                adminWindow.setVisible(true);
            } else JOptionPane.showMessageDialog(null, "No permission", "Error", JOptionPane.ERROR_MESSAGE);

        });

        JButton allDataButton = new JButton("All data");
        panel.add(allDataButton, BorderLayout.SOUTH);

        allDataButton.addActionListener(e ->

        {
            if (LoginWindow.role.equals("user")) {
                JOptionPane.showMessageDialog(null, "No permission", "Error", JOptionPane.ERROR_MESSAGE);
            } else {

                MongoCursor<Document> cursor = null;
                try {
                    cursor = collection.find().iterator();
                    updateTable(cursor);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "No permission", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });


        // Настраиваем окно
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(400, 300);

        setLocationRelativeTo(null);

        setVisible(true);

    }

    private void updateTable(MongoCursor<Document> cursor) {

        textField1.setText("");
        textField2.setText("");
        textField3.setText("");
        tableModel.setRowCount(0);
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            System.out.println(doc.toJson());
            String[] fields = doc.keySet().toArray(new String[doc.keySet().size()]);
            if (tableModel.getColumnCount() == 0) {
                tableModel.setColumnIdentifiers(fields);
            }
            Object[] vals = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                vals[i] = doc.get(fields[i]);
            }
            tableModel.addRow(vals);
        }
        cursor.close();

    }

    private void Aggregation(List<? extends Bson> pipeline) {

        try {
            MongoCursor<Document> cursor = collection.aggregate(pipeline).iterator();
            updateTable(cursor);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No permission", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private boolean checkField(JTextField textField) {

        if (textField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Some fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;

    }
}
package com.example.currencyconvector;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Controller {
    private Database database;
    @FXML
    private Label resultLabel;

    @FXML
    private ComboBox<String> currencyComboBox;

    @FXML
    private TextField amountTextField;

    @FXML
    private void initialize() {
        try {
            database = new Database();
            currencyComboBox.setItems(database.getCurrency());
            currencyComboBox.setEditable(true);
            AutoCompleteComboBoxListener<String> test = new AutoCompleteComboBoxListener<>(currencyComboBox);
            currencyComboBox.setOnAction(event -> {
                String selected = currencyComboBox.getSelectionModel().getSelectedItem();
                System.out.println("Выбрано: " + selected);
            });
        } catch (Exception e) {
            System.err.println("Ошибка инициализации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void shutdown() {
        if (database != null) {
            database.close();
        }
    }
}
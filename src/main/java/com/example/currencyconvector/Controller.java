package com.example.currencyconvector;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Controller {
    double[] currencies = new double[2];
    double[] result = new double[2];
    String code1,code2;
    String valueAmount1,valueAmount2;

    private Database database;
    @FXML
    private Label resultLabel;

    @FXML
    private Label exchangeRateLabel;

    @FXML
    private ComboBox<String> currencyComboBox1;

    @FXML
    private ComboBox<String> currencyComboBox2;

    @FXML
    private  Label calculateLabel;

    @FXML
    private TextField amountTextField1;

    @FXML
    private TextField amountTextField2;

    @FXML
    private Button convertButton;

    @FXML
    private void initialize() {
        try {
            database = new Database();
            currencyComboBox1.setItems(database.getCurrency());
            currencyComboBox1.setEditable(true);
            new AutoCompleteComboBoxListener<>(currencyComboBox1);

            currencyComboBox2.setItems(database.getCurrency());
            currencyComboBox2.setEditable(true);
            new AutoCompleteComboBoxListener<>(currencyComboBox2);

            currencyComboBox1.setOnAction(e -> {
                String selected = currencyComboBox1.getSelectionModel().getSelectedItem();
                System.out.println("Выбрана первая валюта: " + selected);
                updateExchange();
            });

            currencyComboBox2.setOnAction(e -> {
                String selected = currencyComboBox2.getSelectionModel().getSelectedItem();
                System.out.println("Выбрана вторая валюта: " + selected);
                updateExchange();
            });

            convertButton.setOnAction(e->{
                Calculate();
            });

        } catch (Exception e) {
            System.err.println("Ошибка инициализации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateExchange(){
        String currency1 = currencyComboBox1.getValue();
        String currency2 = currencyComboBox2.getValue();

        if (currency1 != null && currency2 != null && !currency1.isEmpty() && !currency2.isEmpty()){
            try {
                code1 = currency1.split(" - ")[0];
                code2 = currency2.split(" - ")[0];

                currencies = database.getExchange(code1,code2,1,1);

                String exchangeText = String.format("1 %s = %.4f %s\n1 %s = %.4f %s",
                        code1,currencies[0],code2,
                        code2,currencies[1],code1);

                exchangeRateLabel.setText(exchangeText);

            } catch (Exception e) {
                exchangeRateLabel.setText("Ошибка получения курса");
                System.err.println("Ошибка при обновлении курса: " + e.getMessage());

            }
        }
    }

    private void Calculate(){
        valueAmount1 = amountTextField1.getText();
        valueAmount2 = amountTextField2.getText();

        if (valueAmount1 != null && valueAmount2 != null && !valueAmount1.isEmpty() && !valueAmount2.isEmpty()){
            try {

                result = database.getExchange(code1,code2,Double.parseDouble(valueAmount1),Double.parseDouble(valueAmount2));

                String calculateText = String.format("%s %s = %.4f %s\n%s %s = %.4f %s",
                        valueAmount1,code1,result[0],code2,
                        valueAmount2,code2,result[1],code1);

                calculateLabel.setText(calculateText);
            }catch (Exception e) {
                exchangeRateLabel.setText("Ошибка рассчета");
                System.err.println("Ошибка при рассчете : " + e.getMessage());

            }
        } else if (valueAmount1 != null && !valueAmount1.isEmpty() && (valueAmount2==null || valueAmount2.isEmpty())) {
            try {

                double amount = Double.parseDouble(valueAmount1);
                result = database.getExchange(code1, code2, amount, 0);

                String calculateText = String.format("%s %s = %.4f %s",
                        valueAmount1,code1,result[0],code2);

                calculateLabel.setText(calculateText);
            }catch (Exception e) {
                exchangeRateLabel.setText("Ошибка рассчета");
                System.err.println("Ошибка при рассчете : " + e.getMessage());

            }

        }else if (valueAmount2 != null && !valueAmount2.isEmpty() && (valueAmount1==null || valueAmount1.isEmpty())) {
            try {

                double amount = Double.parseDouble(valueAmount2);
                result = database.getExchange(code1, code2, 0, amount);

                String calculateText = String.format("%s %s = %.4f %s",
                        valueAmount2,code2,result[1],code1);

                calculateLabel.setText(calculateText);
            }catch (Exception e) {
                exchangeRateLabel.setText("Ошибка рассчета");
                System.err.println("Ошибка при рассчете : " + e.getMessage());

            }

        }else {
            calculateLabel.setText("Введите сумму\nдля конвертации");
        }
    }

    public void shutdown() {
        if (database != null) {
            database.close();
        }
    }
}
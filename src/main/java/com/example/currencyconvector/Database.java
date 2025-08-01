package com.example.currencyconvector;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONObject;

import  java.sql.*;
import  java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:currency.db";
    private Connection connection;

    public Database(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA encoding = 'UTF-8'");
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            createTables();
        } catch (Exception e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
        }
    }


    private void createTables(){
        String createCurrencyTable = "CREATE TABLE IF NOT EXISTS currency ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "currency_code TEXT NOT NULL UNIQUE,"
                + "currency_name TEXT,"
                + "value REAL,"
                + "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "is_crypto BOOLEAN"
                + ")";
        try (Statement statement = connection.createStatement()){
            statement.execute(createCurrencyTable);
            System.out.println("Таблица создана или уже существует");

        } catch (SQLException e) {
            System.err.println("Ошибка создания таблицы: " + e.getMessage());
        }
    }

    public void close(){

        try {
            if(connection != null && !connection.isClosed()){
                connection.close();
                System.out.println("Соединение с БД закрыто");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }

    public boolean addRequest(JSONObject json) {
        try {
            JSONObject data = json.getJSONObject("data");

            String insertSQL = "INSERT INTO currency (currency_code, currency_name, value, is_crypto, last_updated) " +
                    "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                    "ON CONFLICT(currency_code) DO UPDATE SET " +
                    "currency_name = excluded.currency_name, " +
                    "value = excluded.value, " +
                    "is_crypto = excluded.is_crypto, " +
                    "last_updated = CURRENT_TIMESTAMP";

            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                for (String currencyCode : data.keySet()) {
                    JSONObject currencyInfo = data.getJSONObject(currencyCode);
                    String code = currencyInfo.getString("code");
                    double value = currencyInfo.getDouble("value");
                    CurrencyNames currencyNames = new CurrencyNames();
                    String name = currencyNames.getNames(code);

                    boolean isCrypto = code.equalsIgnoreCase("BTC") ||
                            code.equalsIgnoreCase("ETH") ||
                            code.equalsIgnoreCase("BNB") ||
                            code.equalsIgnoreCase("ADA") ||
                            code.equalsIgnoreCase("DOT") ||
                            code.equalsIgnoreCase("XRP") ||
                            code.equalsIgnoreCase("LTC") ||
                            code.equalsIgnoreCase("SOL") ||
                            code.equalsIgnoreCase("MATIC") ||
                            code.equalsIgnoreCase("AVAX") ||
                            code.equalsIgnoreCase("USDT") ||
                            code.equalsIgnoreCase("USDC");

                    pstmt.setString(1, code);
                    pstmt.setString(2, name);
                    pstmt.setDouble(3, value);
                    pstmt.setBoolean(4, isCrypto);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            System.out.println("Данные успешно добавлены/обновлены в БД");
            return true;

        } catch (Exception e) {
            System.err.println("Ошибка при добавлении данных в БД: " + e.getMessage());
            return false;
        }
    }

    public void PrintAll(){
        String sql = "SELECT * FROM currency";
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);
            while (result.next()){
                int id = result.getInt("id");
                String code = result.getString("currency_code");
                String name = result.getString("currency_name");
                double value = result.getDouble("value");
                boolean isCrypto = result.getBoolean("is_crypto");
                String lastUpdated = result.getString("last_updated");

                System.out.printf("ID: %d,Code: %s, Name: %s, Value: %.6f, Crypto: %b, Updated: %s%n",
                        id,code, name, value, isCrypto, lastUpdated);
            }
        }catch (SQLException e){
            System.err.println("Ошибка : " + e.getMessage());
        }
    }

    public ObservableList<String> getCurrency() {
        ObservableList<String> currencies = FXCollections.observableArrayList();
        String sql = "SELECT currency_code, currency_name FROM currency";

        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {
                String code = result.getString("currency_code");
                String name = result.getString("currency_name");
                currencies.add(code + " - " + name);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения валют: " + e.getMessage());
        }

        return currencies;
    }

    public double[] getExchange(String code1,String code2,double amount1,double amount2){
        double[] result = new double[2];
        String sql = "SELECT (SELECT value FROM currency WHERE currency_code = ?) as val1, " +
                "(SELECT value FROM currency WHERE currency_code = ?) as val2";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,code1);
            statement.setString(2,code2);

            ResultSet resultSet = statement.executeQuery();
            double value1= resultSet.getDouble("val1");
            double value2 = resultSet.getDouble("val2");
            System.out.println("1$ = "+value1+" "+code1+"\n"+"1$ = "+value2+" "+code2);
            result[0] = (amount1/value1)* value2;
            result[1] = (amount2/value2)* value1;
        }catch (SQLException e) {
            System.err.println("Ошибка обмена: " + e.getMessage());
        }


        return result;
    }

}

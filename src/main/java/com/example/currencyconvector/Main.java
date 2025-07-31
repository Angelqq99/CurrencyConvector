package com.example.currencyconvector;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Main {
        public static void main(String[] args) {
            try {
                String apiKey = "your_api_key";
                String baseUrl = "https://api.currencyapi.com/v3/latest";

                URL url = new URL(baseUrl + "?apikey=" + apiKey + "&base_currency=USD");

                // Открываем соединение
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Получаем ответ
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();


                    JSONObject json = new JSONObject(response.toString());

                    Database db = new Database();
                    db.addRequest(json);
                    db.PrintAll();
                    db.close();
                } else {
                    System.out.println("GET запрос не сработал: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
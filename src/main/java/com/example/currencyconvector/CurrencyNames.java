package com.example.currencyconvector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CurrencyNames {
    private static final Map<String,String> currencyNames = new HashMap<>();

    public CurrencyNames(){
       try (InputStream input = CurrencyNames.class.getClassLoader().getResourceAsStream("currencies.properties")){
           if (input != null) {
               Properties props = new Properties();
               props.load(new InputStreamReader(input, "windows-1251"));
               for (String key : props.stringPropertyNames()) {
                   String value = props.getProperty(key).replace("\"", "");
                   currencyNames.put(key, value);
               }
           } else {
               System.err.println("Файл currencies.properties не найден.");
           }

       }catch (IOException e) {
           e.printStackTrace();
       }

    }

    public static String getNames(String code){
        return currencyNames.getOrDefault(code,code);
    }

}

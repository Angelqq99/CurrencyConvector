package com.example.currencyconvector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Locale;

public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

    private final ComboBox<T> comboBox;
    private final ObservableList<T> originalData;
    private boolean moveCaretToPos = false;
    private int caretPos;

    public AutoCompleteComboBoxListener(final ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        this.originalData = FXCollections.observableArrayList(comboBox.getItems());

        this.comboBox.setEditable(true);
        this.comboBox.setOnKeyPressed(t -> comboBox.hide());
        this.comboBox.setOnKeyReleased(this);
    }

    @Override
    public void handle(KeyEvent event) {
        // Обработка специальных клавиш
        if (event.getCode() == KeyCode.UP) {
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if (event.getCode() == KeyCode.DOWN) {
            if (!comboBox.isShowing()) {
                comboBox.show();
            }
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        }

        // Игнорируем некоторые клавиши
        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
                || event.isControlDown() || event.getCode() == KeyCode.HOME
                || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
            return;
        }

        // Получаем текст для поиска
        String searchText = comboBox.getEditor().getText();

        // Фильтрация с учетом русского языка
        ObservableList<T> filteredList = FXCollections.observableArrayList();
        for (T item : originalData) {
            String itemText = convertToLatinIfRussian(item.toString().toLowerCase(Locale.ROOT));
            String searchTextConverted = convertToLatinIfRussian(searchText.toLowerCase(Locale.ROOT).trim());

            if (itemText.contains(searchTextConverted)) {
                filteredList.add(item);
            }
        }

        comboBox.setItems(filteredList);

        // Обновление позиции курсора
        if (!moveCaretToPos) {
            caretPos = -1;
        }
        moveCaret(searchText.length());

        if (!filteredList.isEmpty()) {
            comboBox.show();
        }
    }

    // Метод для преобразования русских букв в латинские аналоги для поиска
    private String convertToLatinIfRussian(String text) {
        return text.replace('а', 'a')
                .replace('б', 'b')
                .replace('в', 'v')
                .replace('г', 'g')
                .replace('д', 'd')
                .replace('е', 'e')
                .replace('ё', 'e')
                .replace('ж', 'j')
                .replace('з', 'z')
                .replace('и', 'i')
                .replace('й', 'i')
                .replace('к', 'k')
                .replace('л', 'l')
                .replace('м', 'm')
                .replace('н', 'n')
                .replace('о', 'o')
                .replace('п', 'p')
                .replace('р', 'r')
                .replace('с', 's')
                .replace('т', 't')
                .replace('у', 'u')
                .replace('ф', 'f')
                .replace('х', 'h')
                .replace('ц', 'c')
                .replace('ч', 'c')
                .replace('ш', 's')
                .replace('щ', 's')
                .replace('ъ', 'b')
                .replace('ы', 'y')
                .replace('ь', 'b')
                .replace('э', 'e')
                .replace('ю', 'u')
                .replace('я', 'a');
    }

    private void moveCaret(int textLength) {
        if (caretPos == -1) {
            comboBox.getEditor().positionCaret(textLength);
        } else {
            comboBox.getEditor().positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }

    public static <T> T getComboBoxValue(ComboBox<T> comboBox) {
        if (comboBox.getSelectionModel().getSelectedIndex() < 0) {
            return null;
        } else {
            return comboBox.getItems().get(comboBox.getSelectionModel().getSelectedIndex());
        }
    }
}
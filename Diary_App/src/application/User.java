package application;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
/**
 * Класс, представляющий пользователя приложения.
 */
public class User {
    private static final String USERS_FILE = "users.txt";
    private Map<String, String> users;
    private LocalDate entryDate;
    private ListView<String> diaryListView;
    private boolean isLoggedIn;
    private String username;

    public User() {
        this.users = loadUsers();
        this.entryDate = LocalDate.now();
        this.isLoggedIn = false;
    }
    
    public User(String username, String password) {
        this.username = username;
        this.users = loadUsers();
        this.users.put(username, password);
        saveUsers();
    }
    
    // Загрузка информации о пользователях из файла
    private Map<String, String> loadUsers() {
        Map<String, String> loadedUsers = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    loadedUsers.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
        	createUsersFile();
        }
        return loadedUsers;
    }

    // Проверка существования пользователя
    public boolean userExists(String username) {
        return users.containsKey(username);
    }
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }
    public String getUsername() {
        return username;
    }
    // Проверка правильности пароля
    public boolean isPasswordCorrect(String username, String password) {
        return users.get(username).equals(password);
    }

    // Регистрация нового пользователя
    public void registerUser(String username, String password) {
        users.put(username, password);
        saveUsers();
    }

    // Сохранение информации о пользователях в файл
    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createUsersFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            writer.write("user1:password1");
            writer.newLine();
            writer.write("user2:password2");
            writer.newLine();
            writer.write("user3:password3");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
     // Загружает записи пользователя из файла.
    public void loadEntries(String username) {
        DiaryEntry.loadEntries(username);
    }
    
    // Добавление записи в ежедневник текущего пользователя с выбором настроения
    public void addEntryWithMood(String username) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Добавление записи с настроением");
        TextArea textArea = new TextArea();
        textArea.setWrapText(true); // Переносить текст на новую строку при достижении границы
        // Создание ChoiceBox для выбора настроения
        ChoiceBox<String> moodChoiceBox = new ChoiceBox<>();
        moodChoiceBox.getItems().addAll("sad", "sulky", "okey", "happy");
        moodChoiceBox.setValue("okey"); // Значение по умолчанию
        // Создание кнопки для сохранения записи
        Button saveButton = new Button("Сохранить");
        saveButton.setOnAction(e -> {
            String entryText = textArea.getText();
            String selectedMood = moodChoiceBox.getValue();
            if (!entryText.isEmpty()) {
                LocalDate entryDate = LocalDate.now();
                // Создаем запись с настроением
                DiaryEntry newEntry = new DiaryEntry(entryText, entryDate, selectedMood, username);
                displayEntries(username);
                System.out.println("обновляем экранчик");
                newEntry.saveUserEntries(username);
                dialogStage.close();
            } else {
            }
        });
        // Создание компоновщика
        VBox vbox = new VBox(textArea, moodChoiceBox, saveButton);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        // Создание сцены и установка сцены для диалогового окна
        Scene scene = new Scene(vbox);
        dialogStage.setScene(scene);
        dialogStage.show();
    }
    
    public void setDiaryListView(ListView<String> diaryListView) {
        this.diaryListView = diaryListView;
    }
    
     // Отображает записи пользователя в ListView.
    public void displayEntries(String username) {
        List<DiaryEntry> entries = DiaryEntry.getAllEntries(username);
        ObservableList<String> entriesText = FXCollections.observableArrayList();
        entriesText.clear();
        for (DiaryEntry entry : entries) {
            String entryText = entry.getEntryDate() + ": " + entry.getEntryText() + " - Mood: " + entry.getSelectedMood();
            entriesText.add(entryText);
        }
        diaryListView.setItems(entriesText);
    }
}

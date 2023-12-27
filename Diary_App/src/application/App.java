package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
/**
 * Основной класс приложения, создающий пользовательский интерфейс для ежедневника.
 */
public class App extends Application {
    private User userManager;
    private ListView<String> diaryListView;
    private Button addEntryButton;
    private Button deleteEntryButton;
    private Button logoutButton;

    public static void main(String[] args) {
        launch(args);
    }
    
     // Метод, вызываемый при старте приложения, инициализирует пользовательский интерфейс.
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ежедневник");
        // Создание элементов управления
        userManager = new User();
        TextField usernameField = new TextField(); 
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Войти");
        Button registerButton = new Button("Зарегистрироваться"); 
        diaryListView = new ListView<>();
        diaryListView.setPrefSize(400, 200);
        addEntryButton = new Button("Добавить запись");
        deleteEntryButton = new Button("Удалить запись");
        logoutButton = new Button("Выйти");
        
        // Установка видимости кнопок в false при создании интерфейса
        diaryListView.setVisible(false);
        addEntryButton.setVisible(false);
        //editEntryButton.setVisible(false);
        deleteEntryButton.setVisible(false);
        logoutButton.setVisible(false);

        // Обработчики событий
        diaryListView.setOnMouseClicked(event -> {
            String selectedEntry = diaryListView.getSelectionModel().getSelectedItem();
            System.out.println("click.");
        });
        // Логин
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));
        registerButton.setOnAction(e -> handleRegister(usernameField.getText(), passwordField.getText()));
        // Добавление записи
        addEntryButton.setOnAction(e -> {
            String username = usernameField.getText();
            userManager.addEntryWithMood(username);
            //refreshDiary(); // Обновление отображения записей в вашем приложении
        });
        // Удаление записи
        deleteEntryButton.setOnAction(e -> {
        	String username = usernameField.getText();
        	handleDeleteEntry(username);
        	});
        // Выход из аккаунта
        logoutButton.setOnAction(e -> handleLogout());
        // Создание сетки для размещения элементов
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        // Добавление элементов на сетку
        grid.add(new Label("Имя пользователя:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Пароль:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(diaryListView, 0, 2, 4, 1);
        grid.add(loginButton, 2, 1);
        grid.add(registerButton, 3, 1); 
        grid.add(addEntryButton, 0, 3);
        //grid.add(editEntryButton, 1, 3);
        grid.add(deleteEntryButton, 2, 3);
        grid.add(logoutButton, 3, 3); 
        userManager.setDiaryListView(diaryListView);
        Scene scene = new Scene(grid, 600, 400);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.show();
    }
    
     // Обработчик события входа в систему.
    private void handleLogin(String username, String password) {
        if (userManager.userExists(username) && userManager.isPasswordCorrect(username, password)) {
           userManager.loadEntries(username);
            diaryListView.setVisible(true);
            addEntryButton.setVisible(true);
            deleteEntryButton.setVisible(true);
            logoutButton.setVisible(true);
            System.out.println("Пользователь успешно вошел.");
            userManager.displayEntries(username);
        } else {
            System.out.println("Неверный пароль.");
        }
    }
    
     // Обработчик события регистрации нового пользователя.
    private void handleRegister(String username, String password) {
        if (!userManager.userExists(username)) {
            // Регистрация нового пользователя
            userManager.registerUser(username, password);
            System.out.println("Пользователь зарегистрирован успешно");
        } else {
            System.out.println("Пользователь с таким именем уже существует");
        }
    }
    
     // Обработчик события удаления записи.
    private void handleDeleteEntry(String username) {
        String selectedEntry = diaryListView.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            // Извлекаем текст записи из выделенной строки
            String entryText = selectedEntry.split(": ", 2)[1];
            // Печать для отладки
            System.out.println("Deleting entry: " + entryText);
            DiaryEntry.deleteEntry(username, entryText);
            userManager.displayEntries(username);
        } else {
            System.out.println("Please select an entry to delete.");
        }
    }
    
     // Обработчик события выхода из аккаунта.
    private void handleLogout() {
        diaryListView.getItems().clear(); // Очистка элементов в ListView

        // Установка видимости кнопок в false при выходе
        addEntryButton.setVisible(false);
        deleteEntryButton.setVisible(false);
        logoutButton.setVisible(false);
        userManager.setLoggedIn(false); 
        System.out.println("Пользователь успешно вышел.");
    }
}

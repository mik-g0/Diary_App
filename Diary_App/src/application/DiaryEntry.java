package application;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * Класс представляющий запись в ежедневнике.
 */
public class DiaryEntry {
    private static final String ENTRIES_DIRECTORY = "user_entries/";
    private String userFileName;
    private static List<DiaryEntry> allEntries = new ArrayList<>();

    private LocalDate entryDate;
    private String entryText;
    private String selectedMood;

    public DiaryEntry(String entryText, LocalDate entryDate, String selectedMood, String username) {
        this.entryDate = entryDate;
        this.entryText = entryText;
        this.selectedMood = selectedMood;
        this.userFileName = ENTRIES_DIRECTORY + username + "_entries.txt";
        allEntries.add(this);
        createEntriesDirectory();
        saveEntries(username, allEntries);
    }
    
    public LocalDate getEntryDate() {
        return entryDate;
    }
    public String getEntryText() {
        return entryText;
    }
    public String getSelectedMood() {
        return selectedMood;
    }
    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }
    public void setSelectedMood(String selectedMood) {
        this.selectedMood = selectedMood;
    }
    public String getUserFileName() {
        return userFileName;
    }
    @Override
    public String toString() {
        return entryDate + ": " + entryText;
    }
    
     // Получает все записи пользователя из хранилища.
    public static List<DiaryEntry> getAllEntries(String username) {
        List<DiaryEntry> userEntries = new ArrayList<>();
        for (DiaryEntry entry : allEntries) {
            if (entry.getUserFileName().equals(ENTRIES_DIRECTORY + username + "_entries.txt")) {
                userEntries.add(entry);
            }
        }
        return userEntries;
    }

     //Сохраняет записи пользователя в файл.
    public void saveUserEntries(String username) {
        List<DiaryEntry> entries = DiaryEntry.getAllEntries(username);
        saveEntries(userFileName, entries);
    }
    
     //Сохраняет все записи в файл.
    public static void saveEntries(String userFileName, List<DiaryEntry> entries) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFileName))) {
            for (DiaryEntry entry : entries) {
                // Преобразование объекта в строку и запись в файл
                String entryString = entry.getEntryDate() + ": " + entry.getEntryText() + "\n";
                writer.write(entryString);
            }
            System.out.println("Entries saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving entries: " + e.getMessage());
        }
    }
    
    // Загружает записи пользователя из файла.
    public static void loadEntries(String username) {
        String userFileName = ENTRIES_DIRECTORY + username + "_entries.txt";
        List<DiaryEntry> loadedEntries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(userFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(": ");
                if (parts.length == 2) {
                    LocalDate entryDate = LocalDate.parse(parts[0]);
                    String entryText = parts[1];
                    String selectedMood = "okey";
                    DiaryEntry newEntry = new DiaryEntry(entryText, entryDate, selectedMood, username);
                    loadedEntries.add(newEntry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Очистить существующие записи и добавить загруженные
        allEntries.clear();
        allEntries.addAll(loadedEntries);
    }

     // Поиск существующей записи по имени пользователя и тексту записи.
    private static DiaryEntry findEntry(String userFileName, String entryText) {
        for (DiaryEntry entry : getAllEntries(userFileName)) {
            String formattedEntryText = entry.getEntryText().substring(entry.getEntryText().indexOf(": ") + 2);
            if (entry.getUserFileName().equals(userFileName) && formattedEntryText.equals(entryText)) {
                return entry;
            }
        }
        return null;
    }
    // Удаляет запись пользователя.
    public static void deleteEntry(String userFileName, String entryText) {
        DiaryEntry entryToRemove = findEntry(userFileName, entryText);
        if (entryToRemove != null) {
            allEntries.remove(entryToRemove);
            System.out.println("Deleting entry: " + entryToRemove);
            System.out.println("Entries before save: " + allEntries);
            saveEntries(userFileName, getAllEntries(userFileName));
            System.out.println("Entries after save: " + allEntries);
        }
    }
    
    //Создает директорию для хранения записей пользователей.
    private static void createEntriesDirectory() {
        File directory = new File(ENTRIES_DIRECTORY);
        if (!directory.exists() && !directory.mkdirs()) {
            System.err.println("Unable to create user entries directory.");
        }
    }
}

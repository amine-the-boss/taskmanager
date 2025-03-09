package com.taskmanager.controllers;

import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.taskmanager.models.Task;
import com.taskmanager.models.Category;
import com.taskmanager.models.User;
import com.taskmanager.dao.TaskDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import javafx.util.StringConverter;




import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class MainController {
    // Composants FXML injectés
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, LocalDateTime> dueDateColumn;
    @FXML private TableColumn<Task, Task.Priority> priorityColumn;
    @FXML private TableColumn<Task, Category> categoryColumn;
    @FXML private TableColumn<Task, Boolean> statusColumn;

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dueDatePicker;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private ComboBox<Task.Priority> priorityCombo;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterCombo;
    @FXML private ListView<Category> categoryList;

    private TaskDAO taskDAO;
    private ObservableList<Task> tasks;
    private User currentUser;
    private FilteredList<Task> filteredTasks;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private Task selectedTask;

    @FXML
    private Label loggedInUsername;
    public void initData(User user) {
        this.currentUser = user;
        loadUserData();
        setupSearch();
        loggedInUsername.setText("User: " + user.getUsername());
    }

    @FXML private ComboBox<String> statusCombo;

    private final StringConverter<LocalDateTime> dateTimeConverter = new StringConverter<>() {
        @Override
        public String toString(LocalDateTime dateTime) {
            if (dateTime == null) {
                return "";
            }
            return DATE_FORMATTER.format(dateTime);
        }

        @Override
        public LocalDateTime fromString(String string) {
            if (string == null || string.trim().isEmpty()) {
                return null;
            }
            try {
                return LocalDateTime.parse(string, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                // Handle invalid input (e.g., show an error message)
                System.err.println("Invalid date/time format: " + string);
                return null; // or throw an exception if you want the edit to fail
            }
        }
    };

    @FXML
    public void initialize() {
        try {
            taskDAO = new TaskDAO();
            tasks = FXCollections.observableArrayList();

            setupTableColumns();
            setupControls();
            setupFilterCombo();
            setupTableEditing();
            setupStatusComboBox();


            loadUserData();

        } catch (Exception e) {
            showError("Erreur d'initialisation", "Impossible d'initialiser l'application: " + e.getMessage());
        }
    }

    private void setupTableEditing() {
        taskTable.setEditable(true);

        categoryColumn.setCellFactory(ComboBoxTableCell.forTableColumn(categoryCombo.getItems()));
        dueDateColumn.setCellFactory(TextFieldTableCell.forTableColumn(dateTimeConverter));
        priorityColumn.setCellFactory(ComboBoxTableCell.forTableColumn(Task.Priority.values()));

        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTask = newSelection;
                statusCombo.setValue(selectedTask.isCompleted() ? "Terminé" : "En cours");
            }
        });
        priorityColumn.setOnEditCommit(event -> {
            Task editedTask = event.getRowValue();
            Task.Priority newPriority = event.getNewValue();
            editedTask.setPriority(newPriority);
            try {
                taskDAO.updateTask(editedTask);
            } catch (SQLException e) {
                showError("Erreur de mise à jour", "Impossible de mettre à jour la priorité de la tâche: " + e.getMessage());
            }
        });
    }

    private void setupStatusComboBox() {
        statusCombo.setItems(FXCollections.observableArrayList("En cours", "Terminé"));
        statusCombo.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });

        statusCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedTask != null && newValue != null) {
                try {
                    boolean newStatus = newValue.equals("Terminé");
                    taskDAO.updateTaskStatus(selectedTask.getId(), newStatus);
                    selectedTask.setCompleted(newStatus);
                    taskTable.refresh();
                } catch (SQLException e) {
                    showError("Erreur de mise à jour", "Impossible de mettre à jour le statut de la tâche: " + e.getMessage());
                }
            }
        });
    }

//


    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("completed"));


        dueDateColumn.setCellFactory(column -> new TableCell<Task, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DATE_FORMATTER));
                }
            }
        });
    }
    @FXML
    private void handleCompleteTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            try {
                taskDAO.updateTaskStatus(selectedTask.getId(), true);
                selectedTask.setCompleted(true);
                taskTable.refresh();
            } catch (SQLException e) {
                showError("Erreur de mise à jour", "Impossible de mettre à jour le statut de la tâche: " + e.getMessage());
            }
        }
    }


    private void loadUserData() {
        try {
            tasks.setAll(taskDAO.getAllTasks(currentUser.getId()));
            taskTable.setItems(tasks);
            loadCategories();
        } catch (SQLException e) {
            showError("Erreur de chargement", "Impossible de charger les données: " + e.getMessage());
        }
    }

    private void setupControls() {
        priorityCombo.setItems(FXCollections.observableArrayList(Task.Priority.values()));

        taskTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showTaskDetails(newSelection);
                    }
                });
    }

    private void setupFilterCombo() {
        filterCombo.setItems(FXCollections.observableArrayList(
                "Toutes les tâches",
                "Tâches en cours",
                "Tâches terminées"
        ));
        filterCombo.setValue("Toutes les tâches");

        filterCombo.setOnAction(event -> applyFilters());
    }

    private void setupSearch() {
        filteredTasks = new FilteredList<>(tasks, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        SortedList<Task> sortedData = new SortedList<>(filteredTasks);
        sortedData.comparatorProperty().bind(taskTable.comparatorProperty());
        taskTable.setItems(sortedData);
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String filterValue = filterCombo.getValue();

        filteredTasks.setPredicate(task -> {
            if (searchText != null && !searchText.isEmpty()) {
                boolean matchesSearch = task.getTitle().toLowerCase().contains(searchText) ||
                        (task.getDescription() != null && task.getDescription().toLowerCase().contains(searchText));
                if (!matchesSearch) return false;
            }

            if (filterValue != null) {
                switch (filterValue) {
                    case "Tâches en cours":
                        if (task.isCompleted()) return false;
                        break;
                    case "Tâches terminées":
                        if (!task.isCompleted()) return false;
                        break;
                }
            }

            return true;
        });
    }

    private void loadCategories() {
        try {
            ObservableList<Category> categories = FXCollections.observableArrayList(
                    taskDAO.getAllCategories(currentUser.getId())
            );
            categoryCombo.setItems(categories);
            categoryList.setItems(categories);

            categoryCombo.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Category category) {
                    return category != null ? category.getName() : "";
                }

                @Override
                public Category fromString(String string) {
                    return categoryCombo.getItems().stream()
                            .filter(category -> category.getName().equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });
        } catch (SQLException e) {
            showError("Erreur de chargement", "Impossible de charger les catégories: " + e.getMessage());
        }
    }

    private void showTaskDetails(Task task) {
        titleField.setText(task.getTitle());
        descriptionArea.setText(task.getDescription());

        // Ensure the date is set in the picker
        if (task.getDueDate() != null) {
            dueDatePicker.setValue(task.getDueDate().toLocalDate());
            hourSpinner.getValueFactory().setValue(task.getDueDate().getHour());
            minuteSpinner.getValueFactory().setValue(task.getDueDate().getMinute());
        } else {
            dueDatePicker.setValue(null);
            hourSpinner.getValueFactory().setValue(0);  // Default to 0 hours
            minuteSpinner.getValueFactory().setValue(0);  // Default to 0 minutes
        }

        priorityCombo.setValue(task.getPriority());
        categoryCombo.setValue(task.getCategory());
    }


    public LocalDateTime getDueDate() {
        LocalDate date = dueDatePicker.getValue();
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();

        if (date != null) {
            return LocalDateTime.of(date, LocalTime.of(hour, minute));
        }
        return null;

    }

    @FXML
    private void handleSave() {
        try {
            if (validateInput()) {
                Task task = createTaskFromInput();
                taskDAO.createTask(task);
                tasks.add(task);
                clearInputs();
                showSuccess("Tâche créée", "La tâche a été créée avec succès.");
            }
        } catch (SQLException e) {
            showError("Erreur de sauvegarde",
                    "Impossible de sauvegarder la tâche: " + e.getMessage());
        }
    }

    private Task createTaskFromInput() {
        Task task = new Task();
        task.setTitle(titleField.getText().trim());
        task.setDescription(descriptionArea.getText().trim());

        if (dueDatePicker.getValue() != null) {
            LocalDate date = dueDatePicker.getValue();
            LocalTime time = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
            task.setDueDate(LocalDateTime.of(date, time)); // Combine date and time
        }

        task.setPriority(priorityCombo.getValue());
        task.setCategory(categoryCombo.getValue());
        task.setUser(currentUser);
        return task;
    }

    @FXML
    private void handleClear() {
        clearInputs();
    }

    private void clearInputs() {
        titleField.clear();
        descriptionArea.clear();
        dueDatePicker.setValue(null);
        priorityCombo.setValue(null);
        categoryCombo.setValue(null);
    }

    private boolean validateInput() {
        if (titleField.getText().trim().isEmpty()) {
            showError("Erreur de validation", "Le titre est obligatoire");
            return false;
        }
        if (priorityCombo.getValue() == null) {
            showError("Erreur de validation", "La priorité est obligatoire");
            return false;
        }
        return true;
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
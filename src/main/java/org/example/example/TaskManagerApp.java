package org.example.example;

import javafx.application.Application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;



public class TaskManagerApp extends Application {
    private TaskDao taskDao = new TaskDao();

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        // Создание списка задач
        ListView<Task> taskListView = new ListView<>();
        taskListView.setPrefWidth(300);
        updateTaskListView(taskListView);

        // Панель добавления задачи
        VBox addTaskBox = new VBox(10);
        addTaskBox.setPadding(new Insets(10));
        addTaskBox.getStyleClass().add("addTaskBox");


        Label titleLabel = new Label("Добавить задачу:");
        titleLabel.getStyleClass().add("label");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Описание");

        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Срок выполнения");

        Label priorityLabel = new Label("Установить приоритет");
        priorityLabel.getStyleClass().add("label");

        ComboBox<Integer> priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll(1, 2, 3);
        priorityComboBox.setValue(1);

        Button addButton = new Button("Добавить");
        addButton.setOnAction(e -> {
            String description = descriptionField.getText();
            long id = 0;
            if (!description.isEmpty() && dueDatePicker.getValue() != null) {
                Task newTask = new Task(id,description, java.sql.Date.valueOf(dueDatePicker.getValue()), priorityComboBox.getValue());
                taskDao.saveTask(newTask);
                updateTaskListView(taskListView);
                descriptionField.clear();
                dueDatePicker.getEditor().clear();
                priorityComboBox.setValue(1);
                id++;
            } else {
                showAlert("Ошибка", "Введите описание и выберите срок выполнения задачи.");
            }
        });
        addButton.getStyleClass().add("button");

        addTaskBox.getChildren().addAll(titleLabel, descriptionField, dueDatePicker, priorityLabel,priorityComboBox, addButton);

        // Панель удаления задачи
        VBox deleteTaskBox = new VBox(10);
        deleteTaskBox.setPadding(new Insets(10));

        Label deleteLabel = new Label("Удалить задачу:");
        TextField deleteIdField = new TextField();
        deleteIdField.setPromptText("ID задачи для удаления");

        Button deleteButton = new Button("Удалить");
        deleteButton.getStyleClass().add("buttonDelete");
        deleteButton.setOnAction(e -> {
            try {
                Long taskId = Long.parseLong(deleteIdField.getText());
                List<Task> tasks = taskDao.getAllTasks();
                boolean taskExists = tasks.stream().anyMatch(task -> task.getId().equals(taskId));

                if (taskExists) {
                    taskDao.deleteTask(taskId);
                    updateTaskListView(taskListView);
                    deleteIdField.clear();
                } else {
                    showAlert("Ошибка", "Задача с ID " + taskId + " не найдена.");
                }

            } catch (NumberFormatException ex) {
                showAlert("Ошибка", "Введите корректный ID задачи для удаления.");
            } catch (SQLException ex) {
                showAlert("Ошибка", "Ошибка при удалении задачи из базы данных.");
                throw new RuntimeException(ex);
            }
        });

        deleteTaskBox.getChildren().addAll(deleteLabel, deleteIdField, deleteButton);

        // Размещение компонентов в корневой панели
        VBox leftPanel = new VBox(10);
        leftPanel.getChildren().addAll(addTaskBox, deleteTaskBox);
        root.setLeft(leftPanel);
        root.setCenter(taskListView);

        primaryStage.setTitle("Менеджер задач");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateTaskListView(ListView<Task> taskListView) {
        try {
            List<Task> tasks = taskDao.getAllTasks();
            taskListView.getItems().clear();
            taskListView.getItems().addAll(tasks);

            // Настройка отображения элементов списка
            taskListView.setCellFactory(param -> new ListCell<Task>() {
                @Override
                protected void updateItem(Task task, boolean empty) {
                    super.updateItem(task, empty);
                    if (empty || task == null) {
                        setText(null);
                    } else {
                        String formattedText = String.format("ID: %d, Описание: %s, Срок: %s, Приоритет: %d",
                                task.getId(), task.getDescription(), task.getDueDate(), task.getPriority());
                        setText(formattedText);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        taskDao.close();
    }
}
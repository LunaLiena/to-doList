package org.example.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TaskDao {

    private static final String DATABASE_URL = "jdbc:sqlite:tasks.db";
    private Connection connection;

    public TaskDao() {
        try {
            this.connection = DriverManager.getConnection(DATABASE_URL);
            createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "description TEXT NOT NULL," +
                "due_date DATE NOT NULL," +
                "priority INTEGER NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveTask(Task task) {
        String sql = "INSERT INTO tasks (description, due_date, priority) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, task.getDescription());
            stmt.setDate(2, task.getDueDate());
            stmt.setInt(3, task.getPriority());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Создание задачи не удалось, нет записи в базе данных.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    task.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Создание задачи не удалось, нет ID задачи в базе данных.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY due_date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Long id = rs.getLong("id");
                String description = rs.getString("description");
                Date dueDate = rs.getDate("due_date");
                int priority = rs.getInt("priority");
                tasks.add(new Task(id, description, dueDate, priority));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void deleteTask(Long taskId) throws SQLException {
            String sql = "DELETE FROM tasks WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, taskId);
                statement.executeUpdate();
            }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
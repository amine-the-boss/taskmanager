package com.taskmanager.dao;

import com.taskmanager.models.Task;
import com.taskmanager.models.Category;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    // Requêtes SQL préparées
    private static final String INSERT_TASK = "INSERT INTO tasks (title, description, due_date, priority, completed, category_id, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";


    private static final String SELECT_ALL_TASKS =
            "SELECT t.*, c.name as category_name, c.color as category_color " +
                    "FROM tasks t LEFT JOIN categories c ON t.category_id = c.id";
    private static final String UPDATE_TASK = "UPDATE tasks SET title=?, description=?, due_date=?, priority=?, completed=?, category_id=? WHERE id=?";
    private static final String DELETE_TASK = "DELETE FROM tasks WHERE id=?";

    public void createTask(Task task) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_TASK, Statement.RETURN_GENERATED_KEYS)) {

            setTaskParameters(pstmt, task);
            pstmt.executeUpdate();

            // Récupération de l'ID généré
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    task.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<Task> getAllTasks(int userId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT t.*, c.name as category_name, c.color as category_color " +
                "FROM tasks t LEFT JOIN categories c ON t.category_id = c.id " +
                "WHERE t.user_id = ?";


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId); // Set the user ID for the query

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(extractTaskFromResultSet(rs));
                }
            }
        }
        return tasks;
    }


    public List<Category> getAllCategories(int userId) throws SQLException {
        String query = "SELECT id, name, color FROM categories WHERE user_id = ?";
        List<Category> categories = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId); // Set the user ID for the query

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                    category.setColor(rs.getString("color"));
                    categories.add(category);
                }
            }
        }
        return categories;
    }

    public void updateTask(Task task) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_TASK)) {

            setTaskParameters(pstmt, task);
            pstmt.setInt(7, task.getId());
            pstmt.executeUpdate();
        }
    }

    public void updateTaskStatus(int taskId, boolean completed) throws SQLException {
        String sql = "UPDATE tasks SET completed = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Correctly set the 'completed' value in the PreparedStatement
            pstmt.setInt(1, completed ? 1 : 0); // MySQL uses 1 for true and 0 for false
            pstmt.setInt(2, taskId);
            pstmt.executeUpdate();
        }
    }



    public void deleteTask(int taskId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_TASK)) {

            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
        }
    }

    private void setTaskParameters(PreparedStatement pstmt, Task task) throws SQLException {
        pstmt.setString(1, task.getTitle());
        pstmt.setString(2, task.getDescription());
        pstmt.setTimestamp(3, task.getDueDate() != null ? Timestamp.valueOf(task.getDueDate()) : null);
        pstmt.setString(4, task.getPriority().toString());
        pstmt.setBoolean(5, task.isCompleted());
        pstmt.setObject(6, task.getCategory() != null ? task.getCategory().getId() : null, java.sql.Types.INTEGER);
        pstmt.setInt(7, task.getUser().getId()); // Add this line
    }

    private Task extractTaskFromResultSet(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));

        Timestamp dueDate = rs.getTimestamp("due_date");
        if (dueDate != null) {
            task.setDueDate(dueDate.toLocalDateTime());
        }

        task.setPriority(Task.Priority.valueOf(rs.getString("priority")));
        task.setCompleted(rs.getBoolean("completed"));

        // Build the category object
        Category category = new Category();
        category.setId(rs.getInt("category_id")); // Ensure this matches 'categories' table
        category.setName(rs.getString("category_name"));
        category.setColor(rs.getString("category_color"));
        task.setCategory(category);

        return task;
    }

}
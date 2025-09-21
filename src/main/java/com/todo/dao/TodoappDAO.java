package com.todo.dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Connection;
import com.todo.util.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.todo.model.Todo;

public class TodoappDAO {

    private static final String SELECT_ALL_TODOS = "SELECT * FROM todos ORDER BY created_at DESC";
    private static final String INSERT_TODO = "INSERT INTO todos (title, description, completed, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_TODO_BY_ID = "SELECT * FROM todos WHERE id=?";
    private static final String UPDATE_TODO = "UPDATE todos SET title=?, description=?, completed=?, updated_at=? WHERE id=?";
    private static final String DELETE_TODO = "DELETE FROM todos WHERE id=?";
    private static final String SELECT_COMPLETED_TODOS = "SELECT * FROM todos WHERE completed=true ORDER BY created_at DESC";
    private static final String SELECT_INCOMPLETED_TODOS = "SELECT * FROM todos WHERE completed=false ORDER BY created_at DESC";

    public int createtodo(Todo todo) throws SQLException {
        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_TODO, Statement.RETURN_GENERATED_KEYS);

        ) {
            stmt.setString(1, todo.getTitle());
            stmt.setString(2, todo.getDescription());
            stmt.setBoolean(3, todo.isCompleted());
            stmt.setTimestamp(4, Timestamp.valueOf(todo.getCreated_at()));
            stmt.setTimestamp(5, Timestamp.valueOf(todo.getUpdated_at()));

            int rowAffected = stmt.executeUpdate();
            if (rowAffected == 0) {
                throw new SQLException("Creating todo failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating todo failed, no ID obtained.");
                }
            }
        }

    }

    public Boolean deleteTodo(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(DELETE_TODO);) {
            stmt.setInt(1, id);
            int row = stmt.executeUpdate();
            return row > 0;
        }

    }

    public Todo getTodoById(int id) throws SQLException {
        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_TODO_BY_ID);) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return getTodoRow(rs);
                }
            }
            return null;
        }
    }

    public boolean updateTodo(Todo todo) throws SQLException {
        try {
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_TODO);
            stmt.setString(1, todo.getTitle());
            stmt.setString(2, todo.getDescription());
            stmt.setBoolean(3, todo.isCompleted());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(5, todo.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Todo getTodoRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        boolean completed = rs.getBoolean("completed");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
        return new Todo(id, title, description, completed, createdAt, updatedAt);
    }

    public List<Todo> getAllTodos() throws SQLException {
        // Implementation to retrieve all todos from the database
        List<Todo> todos = new ArrayList<>();

        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_TODOS);
                ResultSet res = stmt.executeQuery();) {
            while (res.next()) {
                Todo todo = getTodoRow(res);
                todos.add(todo);
            } 
        }
        return todos;

    }

    public List<Todo> getCompletedTodos() throws SQLException {
        // Implementation to retrieve all todos from the database
        List<Todo> todos = new ArrayList<>();

        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_COMPLETED_TODOS);
                ResultSet res = stmt.executeQuery();) {
            while (res.next()) {
                Todo todo = getTodoRow(res);
                todos.add(todo);
            }
        }
        return todos;

    }

    public List<Todo> getIncompletedTodos() throws SQLException {
        List<Todo> todos = new ArrayList<>();
        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_INCOMPLETED_TODOS);
                ResultSet rs = stmt.executeQuery();) {
            while (rs.next()) {
                Todo todo = getTodoRow(rs);
                todos.add(todo);
            }
            return todos;
        }
    }
}
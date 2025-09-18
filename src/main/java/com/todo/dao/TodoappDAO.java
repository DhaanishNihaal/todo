package com.todo.dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.security.Timestamp;
import java.sql.Connection;
import com.todo.util.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.todo.model.Todo;
public class TodoappDAO{

    private static final String SELECT_ALL_TODOS="SELECT * FROM todos ORDER BY created_at DESC";
    private static final String INSERT_TODO="INSERT INTO todos (title, description, completed, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
    //CRAETING NEW TODO
    public int createtodo(Todo todo){

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


            try(
                Connection conn=  DatabaseConnection.getDBConnection();
                PreparedStatement stmt=conn.prepareStatement(SELECT_ALL_TODOS);
                ResultSet res=stmt.executeQuery();
            )
            {
                while(res.next()){
                    Todo todo = getTodoRow(res);
                    todos.add(todo);
                }
            }
            return todos;

        }
}
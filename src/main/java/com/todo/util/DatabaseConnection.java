package com.todo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public  class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/todo";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Keepcalm@2005";

    static{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e){
            System.out.println("Driver not found: " + e.getMessage());
        }
    }

    public static Connection getDBConnection() throws SQLException
    {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
} 
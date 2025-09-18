package com.todo;
import com.todo.gui.TodoappGUI;
import com.todo.util.DatabaseConnection;
import java.sql.SQLException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection db_Connection=new DatabaseConnection();
    try{
        Connection cn=db_Connection.getDBConnection();
        System.out.println("Connection Successful");
    }
    catch(SQLException e){
        System.out.println("Connection Failed");
    }
    try{
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
    {
        System.err.println("Look and Feel setting failed: " + e.getMessage());
    }

    SwingUtilities.invokeLater( 
        () ->{
            try{
            new TodoappGUI().setVisible(true);
            }
            catch(Exception e){
                System.err.println("Error initializing GUI: " + e.getMessage());
            }
        }
    );
}
}

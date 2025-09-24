package com.todo.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import com.todo.model.Todo;
import com.todo.dao.TodoappDAO;

public class TodoappGUI extends JFrame {
    private TodoappDAO todoappDAO;
    private JTable todoTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JCheckBox completedCheckbox;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JComboBox<String> filterComboBox;

    public TodoappGUI() {
        this.todoappDAO = new TodoappDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadTodos();
    }

    public void initializeComponents() {
        setTitle("Todo App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        String[] columns = { "ID", "Title", "Description", "Completed", "Created At", "Updated At" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        todoTable = new JTable(tableModel);
        todoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        todoTable.getSelectionModel().addListSelectionListener(
                (e) -> {
                    if (!e.getValueIsAdjusting()) {
                        // load the selected todo
                        loadSelectedTodo();
                    }
                });

        titleField = new JTextField(20);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        completedCheckbox = new JCheckBox("Completed");
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");

        String[] filterOptions = { "All", "Completed", "Incomplete" };
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.addActionListener(e -> {
            filterTodos();
        });

    }
    private void setupLayout()
    {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel ("Title"),gbc);
        gbc.gridx=1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        inputPanel.add(titleField,gbc);

        gbc.gridx=0;
        gbc.gridy=1;
        inputPanel.add(new JLabel("Description"),gbc);
        gbc.gridx=1;
        inputPanel.add(new JScrollPane(descriptionArea),gbc);
        
        gbc.gridx=1;
        gbc.gridy=2;
        inputPanel.add(completedCheckbox,gbc);

        JPanel buttonPanel=new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel filterPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterComboBox);

        JPanel northPanel=new JPanel(new BorderLayout());
        northPanel.add(inputPanel,BorderLayout.CENTER);
        northPanel.add(buttonPanel,BorderLayout.SOUTH);
        northPanel.add(filterPanel,BorderLayout.NORTH);


        add(northPanel,BorderLayout.NORTH);

        add(new JScrollPane(todoTable),BorderLayout.CENTER);

        JPanel statusPanel= new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("select a todo to edit or delete"));
        add(statusPanel,BorderLayout.SOUTH);
    }
    private void setupEventListeners()
    {
        addButton.addActionListener((e)->{addTodo();});
        updateButton.addActionListener((e)->{updateTodo();});
        deleteButton.addActionListener((e)->{deleteTodo();});
        refreshButton.addActionListener((e)->{refreshTodoList();});

    }
    private void addTodo(){
        String title =titleField.getText().trim();
        String description=descriptionArea.getText().trim();
        boolean completed=completedCheckbox.isSelected();

        if(title.isEmpty()){
            JOptionPane.showMessageDialog(this, "Title cannot be empty!","Input Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        try{
        Todo todo=new Todo(title,description);
        todo.setCompleted(completed);
        todoappDAO.createtodo(todo);
        loadTodos();
        JOptionPane.showMessageDialog(this, "Todo added successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error adding todo: " + e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
        }


    }
    private void updateTodo(){
        int row = todoTable.getSelectedRow();
        if (row==-1){
            JOptionPane.showMessageDialog(this, "Please select a todo to update!","Selection Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        String title= titleField.getText().trim();
        if(title.isEmpty()){
            JOptionPane.showMessageDialog(this, "Title cannot be empty!","Input Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        String description=descriptionArea.getText().trim();
        boolean completed=completedCheckbox.isSelected();
        int id=(Integer)todoTable.getValueAt(row,0);
        try{
            Todo todo =todoappDAO.getTodoById(id);
            if(todo!=null){
                todo.setTitle(title);
                todo.setDescription(description);
                todo.setCompleted(completed);

                if (todoappDAO.updateTodo(todo)){
                    JOptionPane.showMessageDialog(this, "Todo updated successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(this, "Failed to update todo!","Error",JOptionPane.ERROR_MESSAGE);
                }
                loadTodos();
            }

            else{
                JOptionPane.showMessageDialog(this, "Selected todo not found!","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error updating todo: " + e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteTodo(){
        int row=todoTable.getSelectedRow();
        if(row==-1){
            JOptionPane.showMessageDialog(this, "Please select a todo to delete!","Selection Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id=(Integer)todoTable.getValueAt(row,0);
        try{
            if(todoappDAO.deleteTodo(id)){
                JOptionPane.showMessageDialog(this,"Todo deleted successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
                loadTodos();
                titleField.setText("");
                descriptionArea.setText("");
                completedCheckbox.setSelected(false);
            }
            else{
                JOptionPane.showMessageDialog(this,"Failed to delete todo!","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error deleting todo: " + e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);

        }
    }
    private void refreshTodoList(){
        loadTodos();
        titleField.setText("");
        descriptionArea.setText("");
        completedCheckbox.setSelected(false);
        filterComboBox.setSelectedIndex(0);

    }
    private void loadTodos(){
        try{
        List<Todo> todos = todoappDAO.getAllTodos();
        updateTable(todos);
        filterComboBox.setSelectedIndex(0);
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error loading todos: " + e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);

        }
    }
    private void updateTable(List<Todo> todos){
        tableModel.setRowCount(0);
        for(Todo t : todos)
        {
            Object[] row = {t.getId(),t.getTitle(),t.getDescription(),t.isCompleted(),t.getCreated_at(),t.getUpdated_at()};
            tableModel.addRow(row);
        }
     
        }
    public void loadSelectedTodo(){
        int row = todoTable.getSelectedRow();
        if (row!=-1){
            String title=(String)tableModel.getValueAt(row,1);
            String description=(String)tableModel.getValueAt(row,2);
            boolean completed=(Boolean)tableModel.getValueAt(row,3);

            titleField.setText(title);
            descriptionArea.setText(description);
            completedCheckbox.setSelected(completed);
        }

    }
    private void loadCompletedTodos(){
        try{
            List<Todo> todos=todoappDAO.getCompletedTodos();
            updateTable(todos);
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error loading completed todos: " + e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);

        }
    }
    private void loadIncompleteTodos(){
        try{
            List<Todo> todos=todoappDAO.getIncompletedTodos();
            updateTable(todos);
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error loading completed todos: " + e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);

        }
    }
    private void filterTodos(){
        String filter=(String)filterComboBox.getSelectedItem();
        if(filter.equals("Completed")){
            loadCompletedTodos();
        }
        else if(filter.equals("Incomplete")){
            loadIncompleteTodos();
        }
        else{
            loadTodos();
        }
    }


}

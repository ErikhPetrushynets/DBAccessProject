package com.example.lab_07;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DbManager {
    public String url = "jdbc:postgresql://localhost:5432/postgres";
    public String user = null;
    public String password = null;
    public Connection connection ;
    public DbManager(){
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.println("Error connecting to the PostgreSQL server: " + e.getMessage());
        }
    }
}

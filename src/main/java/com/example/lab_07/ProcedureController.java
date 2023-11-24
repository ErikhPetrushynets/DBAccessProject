package com.example.lab_07;

import eu.hansolo.toolbox.properties.StringProperty;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProcedureController {
    @FXML
    public Label welcomeText;
    @FXML
    public Button getTimeButton;
    @FXML
    public TableView<Time> tableView;
    public TableColumn<Time, String> timeColumn;
    public DbManager dbmanager;
    public ProcedureController(){
        dbmanager = new DbManager();
    }
    @FXML
    public void onGetTimeButtonClick() throws SQLException {
        String storedProcedureCall = "{call get_time_for_consultation(?, ?) }";
        CallableStatement callableStatement = dbmanager.connection.prepareCall(storedProcedureCall);

// Set input parameters
        callableStatement.setInt(1, 1);  // Replace with the actual parameter values
        callableStatement.setDate(2, java.sql.Date.valueOf("2023-11-20"));

// Execute the stored procedure
        boolean hasResults = callableStatement.execute();

// Process the result set if applicable
        ArrayList<Time> availableTimes = new ArrayList<>();
        if (hasResults) {
            ResultSet rs = callableStatement.getResultSet();
            while (rs.next()) {
                if (rs.getTime("available_time") != null) {
                    availableTimes.add(new Time (rs.getTime("available_time")));
                }
            }
        }
        callableStatement.close();
        ObservableList<Time> observableList = FXCollections.observableArrayList();
        observableList.addAll(availableTimes);
        tableView.setItems(observableList);


    }
    @FXML
    public void initialize(){
        timeColumn.setCellValueFactory(new PropertyValueFactory<Time, String>("time"));
    }

}
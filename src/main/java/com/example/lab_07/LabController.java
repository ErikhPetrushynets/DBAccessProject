package com.example.lab_07;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;


public class LabController {
    @FXML
    public TableView<Time> tableView;
    @FXML
    public TextField functionDoctorIdInput;
    @FXML
    DatePicker functionDateInput;
    public TableColumn<Time, String> timeColumn;



    @FXML
    public TableView<Diagnose> diagnoseView;
    @FXML
    public TextField insertDoctorIdInput;
    @FXML
    public TextField insertPatientIdInput;
    @FXML
    public TextField insertDiagnoseInput;
    @FXML
    public TextField insertIcd10Input;

    @FXML
    public DatePicker insertDateInput;
    @FXML
    public TableColumn<Diagnose, String> insertPatientIdColumn;
    @FXML
    public TableColumn<Diagnose, String> insertDiagnoseColumn;
    @FXML
    public TableColumn<Diagnose, String> insertDateColumn;
    @FXML
    public TableColumn<Diagnose, String> insertICD10Column;
    public DbManager dbmanager;
    public LabController(){
        dbmanager = new DbManager();
    }
    @FXML
    public void onGetTimeButtonClick() throws SQLException {
        try {
            // Start a transaction
            dbmanager.connection.setAutoCommit(false);
            String storedProcedureCall = "{call get_time_for_consultation(?, ?) }";
            try (CallableStatement callableStatement = dbmanager.connection.prepareCall(storedProcedureCall)) {
                callableStatement.setInt(1, Integer.valueOf(functionDoctorIdInput.getText()));
                callableStatement.setDate(2, java.sql.Date.valueOf(functionDateInput.getValue()));

                boolean hasResults = callableStatement.execute();

                ArrayList<Time> availableTimes = new ArrayList<>();
                if (hasResults) {
                    ResultSet rs = callableStatement.getResultSet();
                    while (rs.next()) {
                        if (rs.getTime("available_time") != null) {
                            availableTimes.add(new Time(rs.getTime("available_time")));
                        }
                    }
                }
                callableStatement.close();
                ObservableList<Time> observableList = FXCollections.observableArrayList();
                observableList.addAll(availableTimes);
                tableView.setItems(observableList);
            }
        } catch (SQLException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(e.getSQLState());
        alert.setHeaderText(null);
        alert.setContentText(e.getMessage());
        alert.showAndWait();

        // Rollback the transaction in case of an error
        try {
            dbmanager.connection.rollback();
        } catch (SQLException rollbackException) {
            Alert rollbackAlert = new Alert(Alert.AlertType.ERROR);
            rollbackAlert.setTitle(rollbackException.getSQLState());
            rollbackAlert.setHeaderText(null);
            rollbackAlert.setContentText(rollbackException.getMessage());
            rollbackAlert.showAndWait();            }
    } catch (Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(e.getMessage());
        alert.setHeaderText(null);
        alert.setContentText(e.getMessage());

        alert.showAndWait();

    } finally{
        try {
            dbmanager.connection.setAutoCommit(true);
        } catch (SQLException autoCommitException) {
            autoCommitException.printStackTrace();
        }
    }
    }
    @FXML
    public void onAddDiagnoseClick() {
        try {
            // Start a transaction
            dbmanager.connection.setAutoCommit(false);

            // Insert a new diagnose
            String insertDiagnoseRow = "INSERT INTO diagnoses (doctor_id, patient_id, name, made_date, icd10_code) VALUES (?,?,?,?,?);";
            try (CallableStatement callableStatement1 = dbmanager.connection.prepareCall(insertDiagnoseRow)) {
                callableStatement1.setInt(1, Integer.parseInt(insertDoctorIdInput.getText()));
                callableStatement1.setInt(2, Integer.parseInt(insertPatientIdInput.getText()));
                callableStatement1.setString(3, insertDiagnoseInput.getText());
                callableStatement1.setDate(4, java.sql.Date.valueOf(insertDateInput.getValue()));
                callableStatement1.setString(5, insertIcd10Input.getText());
                callableStatement1.execute();
            }

            // Retrieve doctor diagnoses
            String getDoctorDiagnoses = "SELECT patient_id, name, made_date, icd10_code FROM diagnoses WHERE doctor_id = ? ORDER BY made_date LIMIT 10";
            try (CallableStatement callableStatement2 = dbmanager.connection.prepareCall(getDoctorDiagnoses)) {
                callableStatement2.setInt(1, Integer.parseInt(insertDoctorIdInput.getText()));
                boolean hasResults = callableStatement2.execute();

                if (hasResults) {
                    ResultSet rs = callableStatement2.getResultSet();

                    // Create a list to store diagnoses
                    ArrayList<Diagnose> diagnoses = new ArrayList<>();

                    while (rs.next()) {
                        int patientId = rs.getInt("patient_id");
                        String name = rs.getString("name");
                        Date madeDate = rs.getDate("made_date");
                        String icd10Code = rs.getString("icd10_code");

                        // Create a Diagnose object and add it to the list
                        Diagnose diagnose = new Diagnose(String.valueOf(patientId), name, String.valueOf(madeDate), icd10Code);
                        diagnoses.add(diagnose);
                    }

                    // Convert the list to ObservableList
                    ObservableList<Diagnose> observableList = FXCollections.observableArrayList();
                    observableList.addAll(diagnoses);

                    // Set the items in your tableView
                    diagnoseView.setItems(observableList);
                }
            }

            // Commit the transaction if everything is successful
            dbmanager.connection.commit();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(e.getSQLState());
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();

            // Rollback the transaction in case of an error
            try {
                dbmanager.connection.rollback();
            } catch (SQLException rollbackException) {
                Alert rollbackAlert = new Alert(Alert.AlertType.ERROR);
                rollbackAlert.setTitle(rollbackException.getSQLState());
                rollbackAlert.setHeaderText(null);
                rollbackAlert.setContentText(rollbackException.getMessage());
                rollbackAlert.showAndWait();            }
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(e.getMessage());
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());

            alert.showAndWait();

        } finally{
            // Set auto-commit back to true to enable auto-commit mode for subsequent operations
            try {
                dbmanager.connection.setAutoCommit(true);
            } catch (SQLException autoCommitException) {
                autoCommitException.printStackTrace();
            }
        }
    }

    @FXML
    public void initialize(){
        timeColumn.setCellValueFactory(new PropertyValueFactory<Time, String>("time"));
        insertPatientIdColumn.setCellValueFactory(new PropertyValueFactory<Diagnose, String>("patient_id"));
        insertDiagnoseColumn.setCellValueFactory(new PropertyValueFactory<Diagnose, String>("name"));
        insertDateColumn.setCellValueFactory(new PropertyValueFactory<Diagnose, String>("made_date"));
        insertICD10Column.setCellValueFactory(new PropertyValueFactory<Diagnose, String>("icd10_code"));

    }

}
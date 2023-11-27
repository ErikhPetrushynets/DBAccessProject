package com.example.lab_07;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.print.Doc;
import javax.xml.datatype.DatatypeConfigurationException;
import java.sql.*;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.DataFormatException;


public class LabController {
    //PROCEDURE FIELDS
    @FXML
    public TableView<Time> tableView;
    @FXML
    public TextField functionDoctorIdInput;
    @FXML
    DatePicker functionDateInput;
    public TableColumn<Time, String> timeColumn;


    //INSERT FIELDS
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
    public DbManager dbmanager1;
    public DbManager dbmanager2;
    public DbManager dbmanager3;
    public LabController()
    {
        dbmanager = new DbManager();
        dbmanager1 = new DbManager();
        dbmanager2 = new DbManager();
        dbmanager3 = new DbManager();
    }

    //SELECT FIELDS
    @FXML
    public TableView<Doctor> doctorView;
    @FXML
    public TextField selectSpecializationInput;
    @FXML
    public TextField selectMinimumExperienceInput;
    public TableColumn<Doctor, String> selectFullName;
    public TableColumn<Doctor, String> selectPhoneNumber;
    public TableColumn<Doctor, String> selectExperience;

    //CONFLICT FIELDS

    @FXML
    public TextField conflictFirstSelect;
    @FXML
    public TextField conflictSecondSelect;
    @FXML
    public TextField conflictTransactionSelect;
    @FXML
    public TextField conflictActualSelect;
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
            rollbackAlert.showAndWait();
        }
    } catch (NumberFormatException e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Please input NUMBER into Doctor ID!");
        alert.setHeaderText(null);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }catch (DateTimeException | NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Please input correct DATE!");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        finally{
            try {
                dbmanager.connection.setAutoCommit(true);
            } catch (SQLException autoCommitException) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(autoCommitException.getSQLState());
            alert.setHeaderText(null);
            alert.setContentText(autoCommitException.getMessage());
            alert.showAndWait();
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
                rollbackAlert.showAndWait();
            }
        } catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Please input NUMBER into ID fields!");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }catch (DateTimeException | NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Please input correct DATE!");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        finally{
            try {
                dbmanager.connection.setAutoCommit(true);
            } catch (SQLException autoCommitException) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(autoCommitException.getSQLState());
                alert.setHeaderText(null);
                alert.setContentText(autoCommitException.getMessage());
                alert.showAndWait();
            }
        }
    }
    @FXML
    public void getDoctors(){
        try {
            // Start a transaction
            dbmanager.connection.setAutoCommit(false);

            // Retrieve doctor diagnoses
            String getDoctors = "SELECT first_name || ' ' || last_name as full_name, phone_number, age(now(), start_of_work) as experience FROM doctors INNER JOIN contacts ON contacts.contact_id = doctors.contact_id WHERE specialization = ?::specialization_type and extract(YEAR from age(now(), doctors.start_of_work)) > ? ORDER BY experience LIMIT 10";
            try (CallableStatement callableStatement = dbmanager.connection.prepareCall(getDoctors)) {
                callableStatement.setString(1, selectSpecializationInput.getText());
                callableStatement.setInt(   2, Integer.parseInt(selectMinimumExperienceInput.getText()));
                boolean hasResults = callableStatement.execute();

                if (hasResults) {
                    ResultSet rs = callableStatement.getResultSet();

                    ArrayList<Doctor> doctors = new ArrayList<>();

                    while (rs.next()) {
                        String fullName = rs.getString("full_name");
                        String phoneNumber = rs.getString("phone_number");
                        String experience = rs.getString("experience");

                        Doctor doctor = new Doctor(String.valueOf(fullName), String.valueOf(phoneNumber), String.valueOf(experience));
                        doctors.add(doctor);
                    }

                    // Convert the list to ObservableList
                    ObservableList<Doctor> observableList = FXCollections.observableArrayList();
                    observableList.addAll(doctors);

                    doctorView.setItems(observableList);
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
                Alert rollbackAlert = new Alert(Alert.AlertType.ERROR);
                rollbackAlert.setTitle(autoCommitException.getSQLState());
                rollbackAlert.setHeaderText(null);
                rollbackAlert.setContentText(autoCommitException.getMessage());
                rollbackAlert.showAndWait();
            }
        }
    }
    public void setTransactionIsolationHigh() throws SQLException {
        dbmanager1.connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        dbmanager2.connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    }
    public void setTransactionIsolationLow() throws SQLException {
        dbmanager1.connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        dbmanager2.connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    }
    public void doPhantom() throws SQLException {
        dbmanager1.connection.setAutoCommit(false);
        dbmanager2.connection.setAutoCommit(false);
        try {
            String selectSumOfEquipment1_1 = "SELECT sum(cost) as cost_sum from equipments WHERE condition = 'Operational'::equipment_condition_type";
            CallableStatement cs1 = dbmanager1.connection.prepareCall(selectSumOfEquipment1_1);
            boolean hasResults1 = cs1.execute();;
            if(hasResults1){
                ResultSet rs1 = cs1.getResultSet();
                if (rs1.next()) {
                    String costSum = rs1.getString("cost_sum");
                    conflictFirstSelect.setText(costSum);
                }
                rs1.close();
            }

            String insertEquipment2_1 = "insert into equipments (room_id, name, condition, cost) values (1, 'X-ray', 'Operational'::equipment_condition_type, 24000)";
            dbmanager2.connection.prepareCall(insertEquipment2_1).execute();

            try{
                dbmanager2.connection.commit();
            }
            catch (SQLException e){
                Platform.runLater(() -> showErrorAlert(e));
                try {
                    dbmanager2.connection.rollback();
                } catch (SQLException ex) {
                    Platform.runLater(() -> showErrorAlert(ex));
                }
            }

            String selectSumOfEquipment1_2 = "SELECT sum(cost) as cost_sum from equipments WHERE condition = 'Operational'::equipment_condition_type";
            CallableStatement cs2 = dbmanager1.connection.prepareCall(selectSumOfEquipment1_2);

            boolean hasResults2 = cs2.execute();;
            if(hasResults2){
                ResultSet rs2 = cs2.getResultSet();
                if (rs2.next()) {
                    String costSum = rs2.getString("cost_sum");
                    conflictSecondSelect.setText(costSum);
                }
                rs2.close();
            }
            try{
                dbmanager1.connection.commit();
            }
            catch (SQLException e){
                Platform.runLater(() -> showErrorAlert(e));
                try {
                    dbmanager1.connection.rollback();

                } catch (SQLException ex) {
                    Platform.runLater(() -> showErrorAlert(ex));
                }
            }

        } catch (SQLException e) {
            Platform.runLater(() -> showErrorAlert(e));
        }

    }
    public void callConflict() throws SQLException {
       this.setTransactionIsolationLow();
       this.doPhantom();
    }
    public void callResolvedConflict() throws SQLException {
        this.setTransactionIsolationHigh();
        this.doPhantom();
    }
    public void doSerializationAnomaly() throws SQLException {
        dbmanager1.connection.setAutoCommit(false);
        dbmanager2.connection.setAutoCommit(false);
        dbmanager3.connection.setAutoCommit(false);

        String selectSumOfEquipment = "SELECT sum(cost) as cost_sum from equipments WHERE condition = 'Maintenance needed'::equipment_condition_type";
        CallableStatement cs3 = dbmanager3.connection.prepareCall(selectSumOfEquipment);

        try {
            String selectSumOfEquipment1_1 = "SELECT sum(cost) from equipments WHERE condition = 'Operational'::equipment_condition_type";
            dbmanager1.connection.prepareCall(selectSumOfEquipment1_1).execute();

            String selectSumOfEquipment2_1 = "SELECT sum(cost) as cost_sum from equipments WHERE condition = 'Maintenance needed'::equipment_condition_type";
            CallableStatement cs2 = dbmanager2.connection.prepareCall(selectSumOfEquipment2_1);
            boolean hasResults2 = cs2.execute();

            if(hasResults2){
                ResultSet rs2 = cs2.getResultSet();
                if (rs2.next()) {
                    String costSum = rs2.getString("cost_sum");
                    conflictTransactionSelect.setText(costSum);
                }
                rs2.close();
            }

            String insertEquipment1_2 = "insert into equipments (room_id, name, condition, cost) values (1, 'X-ray', 'Maintenance needed'::equipment_condition_type, 24000)";
            dbmanager1.connection.prepareCall(insertEquipment1_2).execute();

            String insertEquipment2_2 = "insert into equipments (room_id, name, condition, cost) values (1, 'X-ray', 'Operational'::equipment_condition_type, 24000)";
            dbmanager2.connection.prepareCall(insertEquipment2_2).execute();
            try{
                dbmanager1.connection.commit();
            }
            catch (SQLException e){
                Platform.runLater(() -> showErrorAlert(e));
                try {
                    dbmanager1.connection.rollback();
                    Thread.sleep(1000);
                    dbmanager1.connection.prepareCall(selectSumOfEquipment1_1).execute();
                    dbmanager1.connection.prepareCall(insertEquipment1_2).execute();
                    try {
                        dbmanager1.connection.commit();
                    } catch (SQLException exp) {
                        Platform.runLater(() -> showErrorAlert(exp));
                        dbmanager1.connection.rollback();

                    }
                } catch (SQLException ex) {
                    Platform.runLater(() -> showErrorAlert(ex));
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            try{
                boolean hasResults3 = cs3.execute();
                if(hasResults3){
                    ResultSet rs3 = cs3.getResultSet();
                    if (rs3.next()) {
                        String costSum = rs3.getString("cost_sum");
                        conflictActualSelect.setText(costSum);
                    }
                    rs3.close();
                }
                dbmanager2.connection.commit();
            }
            catch (SQLException e){
                Platform.runLater(() -> showErrorAlert(e));
                try {
                    dbmanager2.connection.rollback();
                    Thread.sleep(1000);
                    CallableStatement cs2_1 = dbmanager2.connection.prepareCall(selectSumOfEquipment2_1);
                    boolean hasResults2_1 = cs2_1.execute();
                    if(hasResults2_1){
                        ResultSet rs2_1 = cs2_1.getResultSet();
                        if (rs2_1.next()) {
                            String costSum = rs2_1.getString("cost_sum");
                            conflictTransactionSelect.setText(costSum);
                        }
                        rs2_1.close();
                    }
                    dbmanager2.connection.prepareCall(insertEquipment2_2).execute();
                    try {
                        boolean hasResults3 = cs3.execute();
                        if(hasResults3){
                            ResultSet rs3 = cs3.getResultSet();
                            if (rs3.next()) {
                                String costSum = rs3.getString("cost_sum");
                                conflictActualSelect.setText(costSum);
                            }
                            rs3.close();
                        }
                        dbmanager2.connection.commit();
                    } catch (SQLException exp) {
                        dbmanager2.connection.rollback();
                        Platform.runLater(() -> showErrorAlert(exp));
                    }
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                catch (SQLException exception) {
                    Platform.runLater(() -> showErrorAlert(e));
                }
            }
        } catch (SQLException e) {
            Platform.runLater(() -> showErrorAlert(e));
        }
    }
    @FXML
    public void callConflict2() throws SQLException {
        this.setTransactionIsolationLow();
        this.doSerializationAnomaly();

    }
    @FXML
    public void callResolvedConflict2() throws SQLException {
        this.setTransactionIsolationHigh();
        this.doSerializationAnomaly();
    }
    private void showErrorAlert(SQLException exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(exception.getSQLState());
        alert.setHeaderText(null);
        alert.setContentText(exception.getMessage());
        alert.showAndWait();
    }
    @FXML
    public void initialize(){
        timeColumn.setCellValueFactory(new PropertyValueFactory<Time, String>("time"));
        insertPatientIdColumn.setCellValueFactory(new PropertyValueFactory<Diagnose, String>("patient_id"));
        insertDiagnoseColumn.setCellValueFactory(new PropertyValueFactory<Diagnose, String>("name"));
        insertDateColumn.setCellValueFactory(new PropertyValueFactory<Diagnose, String>("made_date"));
        insertICD10Column.setCellValueFactory(new PropertyValueFactory<Diagnose, String>("icd10_code"));
        selectFullName.setCellValueFactory(new PropertyValueFactory<Doctor, String>("full_name"));
        selectPhoneNumber.setCellValueFactory(new PropertyValueFactory<Doctor, String>("phone_number"));
        selectExperience.setCellValueFactory(new PropertyValueFactory<Doctor, String>("experience"));
    }
}



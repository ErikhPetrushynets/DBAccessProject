package com.example.lab_07;

import java.util.Date;

public class Diagnose
{
    String patient_id;
    String name;
    String made_date;
    String icd10_code;

    public Diagnose(String patient_id, String name, String made_date, String icd10code) {
        this.patient_id = patient_id;
        this.name = name;
        this.made_date = made_date;
        this.icd10_code = icd10code;
    }


    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMade_date() {
        return made_date;
    }

    public void setMade_date(String made_date) {
        this.made_date = made_date;
    }

    public String getIcd10_code() {
        return icd10_code;
    }

    public void setIcd10_code(String icd10code) {
        this.icd10_code = icd10code;
    }
}

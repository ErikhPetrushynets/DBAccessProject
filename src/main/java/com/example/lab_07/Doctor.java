package com.example.lab_07;

public class Doctor {
    String full_name;
    String phone_number;
    String experience;

    public Doctor(String full_name, String phone_number, String experience) {
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.experience = experience;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }
}

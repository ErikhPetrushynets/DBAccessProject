package com.example.lab_07;

public class Time {
    public java.sql.Time time;

    Time(java.sql.Time _time){
        time = _time;
    }

    public java.sql.Time getTime() {
        return time;
    }

    public void setTime(java.sql.Time time) {
        this.time = time;
    }
}

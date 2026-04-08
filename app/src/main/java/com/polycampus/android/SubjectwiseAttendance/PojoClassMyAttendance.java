package com.polycampus.android.SubjectwiseAttendance;

public class PojoClassMyAttendance {
    public String date;
    public String subject_name;
    public String presenty;

    public PojoClassMyAttendance(String date, String subject_name, String presenty) {
        this.date = date;
        this.subject_name = subject_name;
        this.presenty = presenty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getPresenty() {
        return presenty;
    }

    public void setPresenty(String presenty) {
        this.presenty = presenty;
    }
}

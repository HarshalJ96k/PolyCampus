package com.polycampus.android.TeacherApp.AddAttendance;

public class POJOViewCurrentAttendance {
    String id,branch,sem,subject,date,time_from,time_to;

    public POJOViewCurrentAttendance(String id, String branch, String sem, String subject, String date, String time_from, String time_to) {
        this.id = id;
        this.branch = branch;
        this.sem = sem;
        this.subject = subject;
        this.date = date;
        this.time_from = time_from;
        this.time_to = time_to;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime_from() {
        return time_from;
    }

    public void setTime_from(String time_from) {
        this.time_from = time_from;
    }

    public String getTime_to() {
        return time_to;
    }

    public void setTime_to(String time_to) {
        this.time_to = time_to;
    }
}

package com.polycampus.android.TeacherApp.SubjectwiseAttendance;

public class PojoClassViewStudentDatewisePresenty {
    public String id,enrollmentno;
    public String student_name,subject_name;
    public String presenty;

    public PojoClassViewStudentDatewisePresenty(String id, String enrollmentno, String student_name, String subject_name, String presenty) {
        this.id = id;
        this.enrollmentno = enrollmentno;
        this.student_name = student_name;
        this.subject_name = subject_name;
        this.presenty = presenty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnrollmentno() {
        return enrollmentno;
    }

    public void setEnrollmentno(String enrollmentno) {
        this.enrollmentno = enrollmentno;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getsubject_name() {
        return subject_name;
    }

    public void setsubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getPresenty() {
        return presenty;
    }

    public void setPresenty(String presenty) {
        this.presenty = presenty;
    }
}

package com.polycampus.android;

public class POJOGetAllStudyMaterial
{
    String id,semester,subjectname,subjectmode;

    public POJOGetAllStudyMaterial(String id, String semester, String subjectname, String subjectmode) {
        this.id = id;
        this.semester = semester;
        this.subjectname = subjectname;
        this.subjectmode = subjectmode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSubjectname() {
        return subjectname;
    }

    public void setSubjectname(String subjectname) {
        this.subjectname = subjectname;
    }

    public String getSubjectmode() {
        return subjectmode;
    }

    public void setSubjectmode(String subjectmode) {
        this.subjectmode = subjectmode;
    }
}

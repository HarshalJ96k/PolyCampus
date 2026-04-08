package com.polycampus.android.AdminApp;

import com.google.gson.annotations.SerializedName;

public class POJOSubject {
    @SerializedName("id")
    private String id;
    
    @SerializedName("subject_name")
    private String subjectName;
    
    @SerializedName("subject_code")
    private String subjectCode;
    
    @SerializedName("semester")
    private String semester;
    
    @SerializedName("branch")
    private String branch;

    public String getId() { return id; }
    public String getSubjectName() { return subjectName; }
    public String getSubjectCode() { return subjectCode; }
    public String getSemester() { return semester; }
    public String getBranch() { return branch; }
}

package com.polycampus.android.TeacherApp;

public class POJOLeaveRequest {
    private String id, username, name, branch, semester, reason, from, to, status, comment;

    public POJOLeaveRequest(String id, String username, String name, String branch, String semester, String reason, String from, String to, String status, String comment) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.branch = branch;
        this.semester = semester;
        this.reason = reason;
        this.from = from;
        this.to = to;
        this.status = status;
        this.comment = comment;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getBranch() { return branch; }
    public String getSemester() { return semester; }
    public String getReason() { return reason; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getStatus() { return status; }
    public String getComment() { return comment; }
}

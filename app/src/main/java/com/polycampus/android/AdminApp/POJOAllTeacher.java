package com.polycampus.android.AdminApp;

public class POJOAllTeacher {
    String id, image, name, mobile_no, email, gender, address, department, subjects, date_of_joining, username, password;

    public POJOAllTeacher(String id, String image, String name, String mobile_no, String email, String gender, String address, String department, String subjects, String date_of_joining, String username, String password) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.mobile_no = mobile_no;
        this.email = email;
        this.gender = gender;
        this.address = address;
        this.department = department;
        this.subjects = subjects;
        this.date_of_joining = date_of_joining;
        this.username = username;
        this.password = password;
    }

    public String getId() { return id; }
    public String getImage() { return image; }
    public String getName() { return name; }
    public String getMobile_no() { return mobile_no; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getDepartment() { return department; }
    public String getSubjects() { return subjects; }
    public String getDate_of_joining() { return date_of_joining; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}

package com.polycampus.android;

public class POJONotice {
    String id,image,title,time,date,description;

    public POJONotice(String id, String image, String title, String time, String date, String description) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.time = time;
        this.date = date;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

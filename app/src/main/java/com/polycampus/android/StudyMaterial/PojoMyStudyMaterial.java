package com.polycampus.android.StudyMaterial;

public class PojoMyStudyMaterial {
    public String date,title,description,viewdoc;

    public PojoMyStudyMaterial(String title, String description, String viewdoc,String date) {
        this.date = date;
        this.title = title;
        this.description = description;
        this.viewdoc = viewdoc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getViewdoc() {
        return viewdoc;
    }

    public void setViewdoc(String viewdoc) {
        this.viewdoc = viewdoc;
    }
}

package com.polycampus.android.AdminApp;

import com.google.gson.annotations.SerializedName;

public class POJOHOD {
    @SerializedName("id")
    private String id;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("branch")
    private String branch;

    public POJOHOD(String id, String username, String branch) {
        this.id = id;
        this.username = username;
        this.branch = branch;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getBranch() { return branch; }
}

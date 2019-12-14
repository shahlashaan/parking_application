package com.example.smartpark;

public class User {

    private String Name;
    private String Mobile;
    private String id;
    private String email;

    public String getBoookedStatus() {
        return boookedStatus;
    }

    public void setBoookedStatus(String boookedStatus) {
        this.boookedStatus = boookedStatus;
    }

    private String boookedStatus;

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getMobileNo() {
        return Mobile;
    }

    public void setMobileNo(String Mobile) {
        this.Mobile = Mobile;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

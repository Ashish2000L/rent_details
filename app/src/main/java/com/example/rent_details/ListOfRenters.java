package com.example.rent_details;

public class ListOfRenters {

    private String name,username,date,password,lastupdate,category;

    public ListOfRenters(String name, String username, String date, String lastupdate, String password, String category) {
        this.name = name;
        this.username = username;
        this.date = date;
        this.password = password;
        this.lastupdate = lastupdate;
        this.category = category;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}

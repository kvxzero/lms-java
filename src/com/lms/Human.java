package com.lms;

import java.io.Serial;
import java.io.Serializable;

public class Human implements Serializable {
    enum cityList {
        TIRUNELVELI, COIMBATORE, CHENNAI, BANGALORE, HYDERABAD
    }
    @Serial
    private static final long serialVersionUID = 190190190;
    private int id;
    private String username, password, email, phNo;
    private cityList city;
    private Main.AccountType type;

    // constructors
    Human(String username, String password, cityList city, String email, String phNo) {
        this.username = username;
        this.password = password;
        this.city = city;
        this.email = email;
        this.phNo = phNo;
    }

    // setters
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setType(Main.AccountType type) {
        this.type = type;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setCity(cityList city) {
        this.city = city;
    }

    // getters
    public int getId() {
        return id;
    }
    public String getUsername() {
        if (username == null)
            return "";
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getCity() {
        return city.toString();
    }
    public Main.AccountType getType() {
        return type;
    }

    public String getPhNo() {
        return phNo;
    }
    public String getEmail() {
        return email;
    }

    // static function to log in
    public static boolean validateLogin(Human human, String password) {
        return human.getPassword().equals(password);
    }
}

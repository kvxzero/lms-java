package com.lms;

import java.io.Serial;
import java.io.Serializable;

import static com.lms.Main.sc;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 190190190;
    private int id;
    private String username, password, city;
    private Main.AccountType type;

    // constructors
    User (String username, String password, String city) {
        this.username = username;
        this.password = password;
        this.city = city;
    }

    // setters
    public void setPassword(String password) {
        this.password = password;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setType(Main.AccountType type) {
        this.type = type;
    }
    public void setId(int id) {
        this.id = id;
    }

    // getters
    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getCity() {
        return city;
    }
    public Main.AccountType getType() {
        return type;
    }

    // static function to log in
    public static boolean validateLogin(User user, String password) {
        return user.getPassword().equals(password);
    }
}

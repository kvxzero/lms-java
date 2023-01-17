package com.lms;

import java.io.Serial;
import java.io.Serializable;

public class Admin extends RegUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 191191191;
    private static int numOfAdmins = 0;
    Admin(String name, String password) {
        super(name, password);
        adminCreation();
        setId(numOfAdmins+1);
        numOfAdmins++;
    }
    @Override
    public String toString() {
        return this.getName();
    }


}
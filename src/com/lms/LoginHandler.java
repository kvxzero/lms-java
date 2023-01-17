package com.lms;

import java.util.ArrayList;
import java.util.ListIterator;

public class LoginHandler {
    // vars for login handling
    private final ArrayList<RegUser> users;
    private final ArrayList<Admin> admins;
    // var for iterating the arraylist
    private static ListIterator dbReader;

    // constructor for the object
    LoginHandler(ArrayList users, ArrayList admins) {
        this.users = users;
        this.admins = admins;
    }
    // function to handle login auth
    public int userLoginRequest(String username, String password, Main.AccountType accountType) {
        RegUser user;
        if(accountType == Main.AccountType.ADMIN)
            dbReader = admins.listIterator();
        else // accountType == Main.AccountType.USER is true
            dbReader = users.listIterator();
        while (dbReader.hasNext()) {
            user = (RegUser) dbReader.next();
            if (user.getName().equals(username)) {
                if(RegUser.validateLogin(user, password))
                    return 200;
                else {
                    System.out.println("Invalid login credentials");
                    return 401;
                }
            }
        }
        System.out.println("Provided username doesn't exist");
        return 401;
    }
    // function to get the user id after auth
    public int getLoginID(String username, Main.AccountType accountType) {
        if(accountType == Main.AccountType.ADMIN)
            dbReader = admins.listIterator();
        else // accountType == Main.AccountType.USER is true
            dbReader = users.listIterator();
        RegUser user;
        while(dbReader.hasNext()) {
            user = (RegUser) dbReader.next();
            if (user.getName().equals(username)) {
                return user.getId()-1;
            }
        }
        return 0;
    }
}
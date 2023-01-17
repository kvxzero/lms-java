package com.lms;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class RegUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 193193193;
    private int id;
    private final int proLimit = 3;
    private static int numOfUsers = 0;
    private String name, password, location;
    private Main.AccountType accountType = Main.AccountType.USER;
    private ArrayList<Integer> borrowedBook = new ArrayList<>();
    RegUser (String name, String password, String location) {
        this.name = name;
        this.password = password;
        this.id = numOfUsers+1;
        borrowedBook.add(-9999);
        this.location = location;
        numOfUsers++;
    }

    RegUser (String name, String password) {
        this.name = name;
        this.password = password;
        this.id = numOfUsers+1;
        borrowedBook.add(-9999);
        numOfUsers++;
    }
    @Override
    public String toString() {
        return "Username : " + this.name + " | Location: " + this.location;
    }

    // getters and setters
    public static void setNumOfUsers(int numOfUsers) {
        RegUser.numOfUsers = numOfUsers;
    }
    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }
    public String getLocation() {
        return location;
    }
    public String getPassword() {
        return password;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    // functions to log in and for admin account creation
    public static boolean validateLogin(RegUser user, String password) {
        return user.password.equals(password);
    }
    public static void adminCreation() {
        numOfUsers--;
    }

    // borrowing and returning functions
    public int getBorrowedBookId() {
        return borrowedBook.get(0);
    }
    public void setBorrowedBook(Book book) {
        this.borrowedBook.set(0, book.getId());
    }

    public boolean canBorrow() {
        if (borrowedBook.get(0) == -9999) {
            return true;
        }
        else {
            System.out.println(this.name + " should return the existing book before borrowing again");
            return false;
        }
    }
    public boolean returnBook() {
        if (borrowedBook.get(0) != -9999) {
            borrowedBook.set(0, -9999);
            return true;
        }
        else {
            System.out.println(this.getName() + " has no book borrowed at the moment");
            return false;
        }
    }
}
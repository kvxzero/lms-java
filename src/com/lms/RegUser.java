package com.lms;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

public class RegUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 193193193;
    private int id;
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
    public void upgradeAccount() {
        this.accountType = Main.AccountType.PRO;
        borrowedBook.add(-9999);
        borrowedBook.add(-9999);
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
    public int getBorrowedBookId(int index) {
        return borrowedBook.get(index);
    }
    public void setBorrowedBook(Book book) {
        if (this.accountType == Main.AccountType.USER)
            this.borrowedBook.set(0, book.getId());
        if (this.accountType == Main.AccountType.PRO) {
            for (int i = 0; i < 3; i++) {
                if (borrowedBook.get(i) == -9999) {
                    borrowedBook.set(i, book.getId());
                    System.out.println("I set " + book + " to " + this + "in " + i + " index");
                    i = 3;
                }
            }
        }
    }
    public void showBorrowedBooks(ArrayList<Book> books) {
        int dbIndex = 0;
        System.out.println("Books borrowed at the moment: ");
        ListIterator dbReader = books.listIterator();
        while (dbReader.hasNext()) {
            if (dbIndex > 2) {
                break;
            }
            if (borrowedBook.get(dbIndex) == -9999) {
                System.out.println(dbIndex+1 + ". None");
                dbIndex++;
                continue;
            }
            Book book = (Book) dbReader.next();
            if (book.getId() == borrowedBook.get(dbIndex)) {
                System.out.println(dbIndex+1 + ". " + book.getName());
                dbIndex++;
            }
        }
    }
    public boolean returnCheck() {
        if (this.accountType == Main.AccountType.USER) {
            if (borrowedBook.get(0) == -9999) {
                return true;
            }
        }
        else {
            if (borrowedBook.get(0) == -9999 && borrowedBook.get(1) == -9999 && borrowedBook.get(2) == -9999) {
                return true;
            }
        }
        return false;
    }
    public boolean canBorrow() {
//        System.out.println("Im here");
//        System.out.println(borrowedBook.get(0));
//        System.out.println(borrowedBook.get(1));
//        System.out.println(borrowedBook.get(2));

        if (this.accountType == Main.AccountType.USER) {
            if (borrowedBook.get(0) == -9999)
                return true;
        }
        else {
           if (borrowedBook.get(0) == -9999)
               return true;
           if (borrowedBook.get(1) == -9999)
               return true;
           if (borrowedBook.get(2) == -9999)
               return true;
        }
        return false;
    }
    public boolean returnBook(int id) {
        if (borrowedBook.get(id) != -9999) {
            borrowedBook.set(id, -9999);
            return true;
        }
        else {
            if (accountType == Main.AccountType.USER)
                System.out.println(this.getName() + " has no book borrowed at the moment");
            else
                System.out.println(this.getName() + " has no borrowed book at that ID");
            return false;
        }
    }

    public Main.AccountType getAccountType() {
        return accountType;
    }
}
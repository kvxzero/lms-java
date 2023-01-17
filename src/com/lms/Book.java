package com.lms;

import java.io.Serial;
import java.io.Serializable;

public class Book implements Serializable {
    @Serial
    private static final long serialVersionUID = 192192192;
    private int id;
    private static int numOfBooks = 0;
    private String name;
    private String genre;
    private boolean inStock;

    // Make this an array list and add all the users
    // who borrowed this book in the order of borrowing
    private int borrowedUser;

    Book (String name, String genre) {
        this.name = name;
        this.genre = genre;
        this.id = numOfBooks+1;
        this.inStock = true;
        this.borrowedUser = -9999;
        numOfBooks++;
    }
    @Override
    public String toString() {
        return "Name: " + this.name + " | Genre: " + this.genre;
    }
    public static void setNumOfBooks(int numOfBooks) {
        Book.numOfBooks = numOfBooks;
    }
    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }
    public boolean availability() {
        return this.inStock;
    }
    public int getBorrowedUser() {
        return this.borrowedUser;
    }
    public boolean setBorrowedUser(RegUser user) {
        if(this.inStock) {
            this.borrowedUser = user.getId();
            this.inStock = false;
            return true;
        }
        else {
            System.out.println("This book is already borrowed and is unavailable");
            return false;
        }
    }
    public void bookReturned() {
        this.inStock = true;
        this.borrowedUser = -9999;
    }

    public String getGenre() {
        return this.genre;
    }
}
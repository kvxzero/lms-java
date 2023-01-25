package com.lms;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {

    enum genreLists {
        FICTION, NOVEL, NARRATIVE, FANTASY, THRILLER, HORROR,
        SCIFI, ROMANCE, ADVENTURE, ACTION, COMEDY, CRIME;
    }

    @Serial
    private static final long serialVersionUID = 192192192;
    private int id;
    private static int numOfBooks = 0;
    private String name;
//    private String genre;
    private genreLists genre;
    private String author;
    private int stock;
    private ArrayList<Integer> borrowedUser = new ArrayList<>();

    Book (String name, genreLists genre, String author, int stock) {
        this.name = name;
        this.genre = genre;
        this.id = numOfBooks+1;
        this.author = author;
        this.stock = stock;
        numOfBooks++;
    }
    @Override
    public String toString() {
        return "Name: " + this.name + " | Genre: " + this.genre + " | Author: " + this.author;
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
        return borrowedUser.isEmpty();
    }
    public ArrayList<Integer> getBorrowedUser() {
        return this.borrowedUser;
    }
    public boolean setBorrowedUser(RegUser user) {
        if(this.stock > 0) {
            this.borrowedUser.add(user.getId());
            this.stock--;
            return true;
        }
        return false;
    }
    public void bookReturned(RegUser user) {
        this.stock++;
        this.borrowedUser.remove((Integer) user.getId());
    }

    public String getGenre() {
        return this.genre.toString();
    }
    public String getAuthor() {
        return this.author;
    }

    public int getStock() {
        return this.stock;
    }
}
package com.lms;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {

    enum genreLists {
        FICTION, NOVEL, NARRATIVE, FANTASY, THRILLER, HORROR,
        SCIFI, ROMANCE, ADVENTURE, ACTION, COMEDY, CRIME
    }

    @Serial
    private static final long serialVersionUID = 192192192;
    private int id;
    private static int numOfBooks = 0;
    private String name, author;
    private Library library;
//    private String genre;
    private genreLists genre;
    private int stock;
    private ArrayList<Integer> borrowedUser = new ArrayList<>();

    Book (String name, genreLists genre, String author, int stock, Library library) {
        this.name = name;
        this.genre = genre;
        this.id = numOfBooks+1;
        this.author = author;
        this.stock = stock;
        this.library = library;
        numOfBooks++;
    }
    @Override
    public String toString() {
        return "Name: " + this.name + " | Genre: " + this.genre + " | Author: " + this.author + " | ID: " + this.id;
    }
    public static void setNumOfBooks(int numOfBooks) {
        Book.numOfBooks = numOfBooks;
    }
    public static int getNumOfBooks() {
        return Book.numOfBooks;
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
    public boolean setBorrowedUser(User user) {
        if(this.stock > 0) {
            this.borrowedUser.add(user.getId());
            this.stock--;
            return true;
        }
        return false;
    }
    public void bookReturned(User user) {
        this.stock++;
        this.borrowedUser.remove((Integer) user.getId());
    }

    public String getGenre() {
        return this.genre.toString();
    }
    public String getAuthor() {
        return this.author;
    }
    public Library getLibrary() {
        return this.library;
    }
    public int getStock() {
        return this.stock;
    }
}
package com.lms;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Library implements Serializable {
    @Serial
    private static final long serialVersionUID = 194194194;
    private String Name;
    private String City;
    private ArrayList<Book> books;
    Library(String Name, String libCity) {
        this.Name = Name;
        this.City = libCity;
        this.books = new ArrayList<>();
    }
    @Override
    public String toString() {
        return this.Name + " @ " + this.City;
    }
    public String getName() {
        return Name;
    }
    public ArrayList<Book> getBooks() {
        return books;
    }
    public String getCity() {
        return City;
    }
}
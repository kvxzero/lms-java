package com.lms;

import java.io.Serial;
import java.io.Serializable;

public class Library implements Serializable {
    @Serial
    private static final long serialVersionUID = 194194194;
    private String Name;
    private String City;
    Library(String Name, String libCity) {
        this.Name = Name;
        this.City = libCity;
    }
    @Override
    public String toString() {
        return this.Name + " @ " + this.City;
    }
    public String getName() {
        return Name;
    }

    public String getCity() {
        return City;
    }
}
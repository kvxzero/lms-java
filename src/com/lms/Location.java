package com.lms;

import java.io.Serial;
import java.io.Serializable;

public class Location implements Serializable {
    @Serial
    private static final long serialVersionUID = 194194194;
    private String libName;
    private String libCity;
    Location(String libName, String libCity) {
        this.libName = libName;
        this.libCity = libCity;
    }
    @Override
    public String toString() {
        return this.libName + " @ " + this.libCity;
    }
    public String getLibName() {
        return libName;
    }

    public String getLibCity() {
        return libCity;
    }
}
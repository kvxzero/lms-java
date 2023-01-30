package com.lms;

import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public interface RegUserFunctions {

    // case 0: function to display all books
    void displayBooks(@NotNull ArrayList<Library> libraries);

    // case 1: function to search books with options
    void searchBooks(@NotNull ArrayList<Library> libraries, int option);

    // case 2: function to borrow books
    boolean borrowBook(ArrayList<Library> libraries);

    // case 3: function to return borrowed books
    boolean executeReturn();

    // case 4: function to view current status
    void getStatus(ArrayList<Library> libraries);

    // case 5: function to find out nearby libraries
    void nearbyLibraries(ArrayList<Library> libraries);

    // case 6: function to change the password
    void changePassword();

    // case 7: function to view user history
    void viewUserHistory();

    // case 8: function to upgrade account
    void upgradeAccount();
}
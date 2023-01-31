package com.lms;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface AdminFunctions {
    // case 0: view borrowing history
    void viewBorrowingHistory(ArrayList<String> borrowedHistory);

    // case 1: add a book
    Book newBook(Library selectedLibrary);

    // case 2: delete a book
    void deleteBook(ArrayList<User> users);

    // case 3: display all books
    void displayBooks(@NotNull ArrayList<Library> libraries, ArrayList<User> users);

    // case 4: search books
    void searchBooks(@NotNull ArrayList<Library> libraries, int option);

    // case 5: add a new user
    // it is in parent class

    // case 6: delete a user
    void deleteUser(ArrayList<User> users, ArrayList<Library> libraries);

    // case 8: delete an admin
    void deleteAdmin(ArrayList<Admin> admins);

    // case 9: list all the users
    void usersList(ArrayList<User> users, ArrayList<Library> libraries);

    // case 10: display all admins
    void adminsList(ArrayList<Admin> admins);

    // case 11: add a new library
    Library newLocation();

    // case 12: delete a library
    void deleteLocation(ArrayList<Library> locations, ArrayList<User> users);

    // case 13: list all libraries
    void librariesList(ArrayList<Library> locations);
}
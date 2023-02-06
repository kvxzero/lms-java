package com.lms;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface AdminFunctions {
    // case 0: view all libraries
    void librariesList(ArrayList<Library> libraries);

    // case 1: add a new library
    boolean addLibrary(ArrayList<Library> libraries);

    // case 2: delete a library
    void deleteLibrary(ArrayList<Library> libraries, ArrayList<User> users);

    // case 3: display all books
    void displayBooks(@NotNull ArrayList<Library> libraries, ArrayList<User> users);

    // case 4: search books
    void searchBooks(@NotNull ArrayList<Library> libraries, int option);

    // case 5: add a book
    boolean searchLibrary(ArrayList<Library> libraries);
    boolean checkBook();
    Book newBook();

    // case 6: delete a book
    void deleteBook(ArrayList<Library> libraries, ArrayList<User> users);

    // case 7: view borrowing history
    void viewBorrowingHistory(ArrayList<String> borrowedHistory);

    // case 8: update the stock of existing books
    void updateCopies();

    // case 9: list all the users
    void usersList(ArrayList<User> users, ArrayList<Library> libraries);

    // case 10: add a new user
    // parent class constructor

    // case 11: delete a user
    void deleteUser(ArrayList<User> users, ArrayList<Library> libraries);

    // case 12: display all admins
    void adminsList(ArrayList<Admin> admins);

    // case 13: add an admin
    // just the constructor of this class

    // case 14: delete an admin
    void deleteAdmin(ArrayList<Admin> admins);
}
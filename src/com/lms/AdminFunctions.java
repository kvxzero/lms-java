package com.lms;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface AdminFunctions {
    // case 0: view borrowing history
    void viewBorrowingHistory();

    // case 1: add a book
    Book newBook();

    // case 2: delete a book
    void deleteBook(ArrayList<RegUser> users, ArrayList<Book> books);

    // case 3: display all books
    void displayBooks(@NotNull ArrayList<Book> books, ArrayList<RegUser> users);

    // case 4: search books
    void searchBooks(@NotNull ArrayList<Book> books, ArrayList<RegUser> users, int option);

    // case 5: add a new user
    // it is in parent class

    // case 6: delete a user
    void deleteUser(ArrayList<RegUser> users, ArrayList<Book> books);

    // case 8: delete an admin
    void deleteAdmin(ArrayList<Admin> admins);

    // case 9: list all the users
    void usersList(ArrayList<RegUser> users, ArrayList<Book> books);

    // case 10: display all admins
    void adminsList(ArrayList<Admin> admins);

    // case 11: add a new library
    Library newLocation();

    // case 12: delete a library
    void deleteLocation(ArrayList<Library> locations);

    // case 13: list all libraries
    void librariesList(ArrayList<Library> locations);
}
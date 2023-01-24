package com.lms;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

import static com.lms.Main.*;
import static com.lms.Main.sc;

public class RegUser extends User implements Serializable, RegUserFunctions {
    @Serial
    private static final long serialVersionUID = 193193193;
    private static int numOfUsers = 0;
    private int bookLimit;
    private ArrayList<Integer> borrowedBook = new ArrayList<>();

    // variables for functions
    private String searchQuery;
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    // constructor
    RegUser (String username, String password, String city) {
        super(username, password, city);
        setId(numOfUsers+1);
        numOfUsers++;
        bookLimit = 1;
        this.setType(AccountType.USER);
    }

    // necessary functions
    public static void setNumOfUsers(int retrievedData) {
        numOfUsers = retrievedData;
    }
    @Override
    public String toString() {
        return "Username : " + getUsername() + " | City: " + getCity();
    }

    // borrowing and returning functions
    public int getBorrowedBookId() {
        if (borrowedBook.size() == 0)
            return -9999;
        return borrowedBook.get(0);
    }

    public ArrayList<Integer> getBorrowedBook() {
        return borrowedBook;
    }

    public void setBorrowedBook(Book book) {
        if (borrowedBook.size() < bookLimit)
            borrowedBook.add(book.getId());
    }
    public void showBorrowedBooks(ArrayList<Book> books) {
        int index = 0;
        System.out.println("Books borrowed at the moment: ");
        ListIterator dbReader = books.listIterator();
        if (borrowedBook.size() == 0) {
            System.out.println("None");
            return;
        }
        while (dbReader.hasNext()) {
            try {
                System.out.println(index + 1 + ". " + books.get(borrowedBook.get(index) - 1).getName());
                index++;
            } catch (Exception e) {
                break;
            }
        }
    }

    // implements RegUserFunctions

    // case 0: function to display all books
    public void displayBooks(@NotNull ArrayList<Book> books) {
        dbReader = books.listIterator();
        while (dbReader.hasNext()) {
            Main.searchedBook = (Book) dbReader.next();
            if (Main.searchedBook.availability()) {
                System.out.println(Main.searchedBook + " | Currently: all copies are in stock");
            } else if (searchedBook.getStock() == 0) {
                System.out.println(Main.searchedBook + " | Currently: no copies are in stock");
            }
            else {
                System.out.println(Main.searchedBook + " | Currently: some copies are in stock");
            }
        }
    }

    // case 1: search for books
    // function to search books with options
    public void searchBooks(@NotNull ArrayList<Book> books, int option) {
        switch(option) {
            case 1:
                searchByName(books);
                break;

            case 2:
                searchByGenre(books);
                break;

            case 3:
                searchByName(books);
                System.out.println();
                searchByGenre(books);
                break;

            default:
                System.out.println("!-- Enter a valid input --!");
        }
    }
    private void searchByGenre(ArrayList<Book> books) {
        Main.searchFlag = false;
        dbReader = books.listIterator();
        System.out.println("--- Searching by genre ---");
        while (dbReader.hasNext()) {
            Main.searchedBook = (Book) dbReader.next();
            if (Main.searchedBook.getGenre().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                if (Main.searchedBook.availability()) {
                    System.out.println(Main.searchedBook + " | Currently: all copies are in stock");
                } else if (searchedBook.getStock() == 0) {
                    System.out.println(Main.searchedBook + " | Currently: no copies are in stock");
                }
                else {
                    System.out.println(Main.searchedBook + " | Currently: some copies are in stock");
                }
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("No results found!");
    }
    private void searchByName(@NotNull ArrayList<Book> books) {
        Main.searchFlag = false;
        dbReader = books.listIterator();
        System.out.println("--- Searching by name ---");
        while (dbReader.hasNext()) {
            Main.searchedBook = (Book) dbReader.next();
            if (Main.searchedBook.getName().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                if (Main.searchedBook.availability()) {
                    System.out.println(Main.searchedBook + " | Currently: all copies are in stock");
                } else if (searchedBook.getStock() == 0) {
                    System.out.println(Main.searchedBook + " | Currently: no copies are in stock");
                }
                else {
                    System.out.println(Main.searchedBook + " | Currently: some copies are in stock");
                }
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("No results found!");
    }

    // case 2: borrow a book
    // function to borrow books
    public boolean borrowBook (ArrayList<Book> books) {
        if (borrowedBook.size() < bookLimit) {
            System.out.print("Enter the book to be borrowed: ");
            searchQuery = sc.next();
            dbReader = books.listIterator();
            while (dbReader.hasNext()) {
                Main.searchedBook = (Book) dbReader.next();
                if (Main.searchedBook.getName().equalsIgnoreCase(searchQuery)) {
                    if(searchedBook.setBorrowedUser(this)) {
                        userAccount.setBorrowedBook(searchedBook);
                        return true;
                    }
                    else {
                        System.out.println("This book is already borrowed and is unavailable");
                        return false;
                    }
                }
            }
            System.out.println("Requested book doesn't exist!");
        }
        else {
            System.out.println("You have reached the max limit!");
        }
        return false;
    }

    // case 3: return borrowed books
    // functions to return books
    public boolean hasNoBooks() {
        return borrowedBook.size() == 0;
    }
    public boolean executeReturn(ArrayList<Book> books) {
        if (this.getType() == AccountType.USER) {
            searchedBook = books.get(this.getBorrowedBookId()-1);
            if (this.returnBook(0)) {
                searchedBook.bookReturned(this);
                return true;
            }
        }
        if (this.getType() == AccountType.PRO) {
            this.showBorrowedBooks(books);
            System.out.print("Choose the book (number) to be returned: ");
            int dbIndex = getInput();
            if (dbIndex == -9999) {
                System.out.println("!-- Enter a valid input --!");
                return false;
            }
            else {
                System.out.print("You chose to return: ");
                searchedBook = books.get(borrowedBook.get(dbIndex - 1) - 1);
                System.out.println(searchedBook.getName() + "\n");
                if (this.returnBook(dbIndex - 1)) {
                    searchedBook.bookReturned(this);
                    return true;
                }
            }
        }
        return false;
    }
    public boolean returnBook(int id) {
        if (borrowedBook.size() == 0) {
            System.out.println(this.getUsername() + " has no book borrowed at the moment");
            return false;
        }
        try {
            borrowedBook.remove(id);
            return true;
        } catch (Exception e) {
            System.out.println(this.getUsername() + " has no borrowed book at that ID");
            return false;
        }
    }

    // case 4: get user status
    // function to get user's status
    public void getStatus(ArrayList<Book> books) {
        if(this.getBorrowedBookId() == -9999)
            System.out.println("Current book: null");
        else {
            Main.searchedBook = books.get(this.getBorrowedBookId()-1);
            System.out.println("Current book: " + Main.searchedBook);
        }
    }

    // case 5: nearby libraries
    // function to find out nearby libraries
    public void nearbyLibraries(ArrayList<Library> locations) {
        int dbIndex = 1;
        dbReader = locations.listIterator();
        while(dbReader.hasNext()) {
            Main.selectedLibrary = (Library) dbReader.next();
            if (this.getType() == AccountType.USER) {
                if (Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                    System.out.println(dbIndex + ". " + Main.selectedLibrary);
                    dbIndex++;
                }
            }
            else {
                if (Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                    System.out.println(dbIndex + ". " + Main.selectedLibrary + " (Nearby)");
                    dbIndex++;
                    continue;
                }
                System.out.println(dbIndex + ". " + Main.selectedLibrary);
                dbIndex++;
            }
        }
    }

    // case 6: change password
    // function to change the password
    public void changePassword() {
        String password;
        System.out.print("Enter your current password: ");
        password = sc.next();
        if(password.equals(this.getPassword())) {
            System.out.print("Enter your new password: ");
            password = sc.next();
            this.setPassword(password);
            System.out.println("Password change successfully!");
        }
        else {
            System.out.println("Invalid password given, Aborted");
        }
    }

    // case 7: view borrowing history
    // function to view user borrowing history
    public void viewUserHistory() {
        ArrayList<String> borrowedHistory = Main.getHistory();
        int dbIndex;
        System.out.println(this.getUsername() + "'s borrowing history:");
        dbReader = borrowedHistory.listIterator();
        dbIndex = 1;
        while (dbReader.hasNext()) {
            String history = (String) dbReader.next();
            if (history.indexOf(this.getUsername()) == 0) {
                System.out.println(dbIndex + ". " + history);
                dbIndex++;
            }
        }
    }

    // case 8: upgrade account to premium
    // function to upgrade the account
    public void upgradeAccount() {
        setType(Main.AccountType.PRO);
        bookLimit = 3;
    }
}
package com.lms;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.ListIterator;

import static com.lms.Main.*;
import static com.lms.Main.sc;

public class RegUser extends User implements Serializable, RegUserFunctions {
    @Serial
    private static final long serialVersionUID = 193193193;
    private static int numOfUsers = 0;
    private int bookLimit;
    private ArrayList<Book> borrowedBook = new ArrayList<>();
    private ArrayList<Library> borrowedFrom = new ArrayList<>();

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
        return borrowedBook.get(0).getId();
    }

    public ArrayList<Book> getBorrowedBook() {
        return borrowedBook;
    }

    public ArrayList<Library> getBorrowedFrom() {
        return borrowedFrom;
    }

    public void setBorrowedBook(Book book) {
        if (borrowedBook.size() < bookLimit) {
            borrowedBook.add(book);
            borrowedFrom.add(book.getLibrary());
        }
    }
    public void showBorrowedBooks() {
        int index = 0;
        System.out.println("Books borrowed at the moment: ");
        if (borrowedBook.size() == 0) {
            System.out.println("None");
            return;
        }
        ListIterator dbReader = borrowedFrom.listIterator();
        while (dbReader.hasNext()) {
            try {
                System.out.println(index + 1 + ". " + borrowedBook.get(index).getName());
                index++;
            } catch (Exception e) {
                break;
            }
        }
    }

    // implements RegUserFunctions

    // case 0: function to display all books
    private void displayAvailability() {
        if (Main.searchedBook.availability()) {
            System.out.println(Main.searchedBook + " | Currently: all copies are in stock");
        } else if (searchedBook.getStock() == 0) {
            System.out.println(Main.searchedBook + " | Currently: no copies are in stock");
        } else {
            System.out.println(Main.searchedBook + " | Currently: some copies are in stock");
        }
    }
    public void displayBooks(@NotNull ArrayList<Library> libraries) {
        searchFlag = false;
        dbReader = libraries.listIterator();
        while (dbReader.hasNext()) {
            selectedLibrary = (Library) dbReader.next();
            if (selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                System.out.println("\nBooks from: " + selectedLibrary.getName());
                searchFlag = true;
                if (selectedLibrary.getBooks().size() == 0) {
                    System.out.println("-- No books --");
                    continue;
                }
                ListIterator bookReader = selectedLibrary.getBooks().listIterator();
                while(bookReader.hasNext()) {
                    searchedBook = (Book) bookReader.next();
                    displayAvailability();
                }
            }
        }
        if (!searchFlag) {
            System.out.println("--- No libraries found in your location! ---");
        }
    }

    // case 1: search for books
    // function to search books with options
    public void searchBooks(@NotNull ArrayList<Library> libraries, int option) {
        switch(option) {
            case 1:
                searchByName(libraries);
                break;
            case 2:
                searchByGenre(libraries);
                break;
            case 3:
                searchByName(libraries);
                System.out.println();
                searchByGenre(libraries);
                break;
            default:
                System.out.println("!-- Enter a valid input --!");
        }
    }
    private void searchByGenre(ArrayList<Library> libraries) {
        dbReader = libraries.listIterator();
        System.out.println("--- Searching by genre ---");
        while (dbReader.hasNext()) {
            searchFlag = false;
            selectedLibrary = (Library) dbReader.next();
            if (selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                System.out.println("\nBooks from: " + selectedLibrary.getName());
                ListIterator bookReader = selectedLibrary.getBooks().listIterator();
                while (bookReader.hasNext()) {
                    searchedBook = (Book) bookReader.next();
                    if(searchedBook.getGenre().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                        displayAvailability();
                        searchFlag = true;
                    }
                }
            }
            if (!Main.searchFlag && selectedLibrary.getCity().equalsIgnoreCase(this.getCity()))
                System.out.println("No results found!");
        }
    }
    private void searchByName(@NotNull ArrayList<Library> libraries) {
        dbReader = libraries.listIterator();
        System.out.println("--- Searching by name ---");
        while (dbReader.hasNext()) {
            searchFlag = false;
            selectedLibrary = (Library) dbReader.next();
            if (selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                System.out.println("\nBooks from: " + selectedLibrary.getName());
                ListIterator bookReader = selectedLibrary.getBooks().listIterator();
                while (bookReader.hasNext()) {
                    searchedBook = (Book) bookReader.next();
                    if(searchedBook.getName().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                        displayAvailability();
                        searchFlag = true;
                    }
                }
            }
            if (!Main.searchFlag && selectedLibrary.getCity().equalsIgnoreCase(this.getCity()))
                System.out.println("No results found!");
        }
    }

    // case 2: borrow a book
    // function to borrow books
    public boolean borrowBook (ArrayList<Library> libraries) {
        if (borrowedBook.size() < bookLimit) {
            System.out.print("Enter the book to be borrowed: ");
            searchQuery = sc.next();
            ListIterator libReader = libraries.listIterator();
            while (libReader.hasNext()) {
                selectedLibrary = (Library) libReader.next();
                if (selectedLibrary.getCity().equalsIgnoreCase(this.getCity()))
                    dbReader = selectedLibrary.getBooks().listIterator();
                while (dbReader.hasNext()) {
                    searchedBook = (Book) dbReader.next();
                    if (searchedBook.getName().equalsIgnoreCase(searchQuery)) {
                        if (searchedBook.setBorrowedUser(this)) {
                            userAccount.setBorrowedBook(searchedBook);
                            return true;
                        } else {
                            System.out.println("This book is already borrowed and is unavailable");
                            return false;
                        }
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
    public void returnAll(ArrayList<Library> libraries) {
        if (this.getType() == AccountType.USER) {
            searchedBook = this.getBorrowedBook().get(0);
            if (this.returnBook(0)) {
                searchedBook.bookReturned(this);
            }
        }
        if (this.getType() == AccountType.PRO) {
            this.showBorrowedBooks();
            int dbIndex = 0;
            while (dbIndex < 3) {
                try {
                    searchedBook = getBorrowedBook().get(dbIndex);
                    if (this.returnBook(dbIndex)) {
                        searchedBook.bookReturned(this);
                    }
                    dbIndex++;
                } catch (Exception e) {
                    break;
                }
            }
        }
    }
    public boolean hasNoBooks() {
        return borrowedBook.size() == 0;
    }
    public boolean executeReturn() {
        if (this.getType() == AccountType.USER) {
            searchedBook = getBorrowedBook().get(0);
            if (this.returnBook(0)) {
                searchedBook.bookReturned(this);
                return true;
            }
        }
        if (this.getType() == AccountType.PRO) {
            this.showBorrowedBooks();
            System.out.print("Choose the book (number) to be returned: ");
            int dbIndex = getInput();
            if (dbIndex == -9999) {
                System.out.println("!-- Enter a valid input --!");
                return false;
            }
            else {
                System.out.print("You chose to return: ");
                searchedBook = getBorrowedBook().get(dbIndex - 1);
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
            borrowedFrom.remove(id);
            return true;
        } catch (Exception e) {
            System.out.println(this.getUsername() + " has no borrowed book at that ID");
            return false;
        }
    }

    // case 4: get user status
    // function to get user's status
    public void getStatus(ArrayList<Library> libraries) {
        if(this.getBorrowedBookId() == -9999)
            System.out.println("Current book: null");
        else {
            System.out.println("Current book: " + borrowedBook.get(0));
        }
    }

    // case 5: nearby libraries
    // function to find out nearby libraries
    public void nearbyLibraries(ArrayList<Library> locations) {
        searchFlag = false;
        int dbIndex = 1;
        dbReader = locations.listIterator();
        while(dbReader.hasNext()) {
            Main.selectedLibrary = (Library) dbReader.next();
            if (this.getType() == AccountType.USER) {
                if (Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                    searchFlag = true;
                    System.out.println(dbIndex + ". " + Main.selectedLibrary);
                    dbIndex++;
                }
            }
            else {
                searchFlag = true;
                if (Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                    System.out.println(dbIndex + ". " + Main.selectedLibrary + " (Nearby)");
                    dbIndex++;
                    continue;
                }
                System.out.println(dbIndex + ". " + Main.selectedLibrary);
                dbIndex++;
            }
        }
        if (!searchFlag) {
            System.out.println("--- No libraries found in your location! ---");
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
    public void viewUserHistory(ArrayList<String> history) {
        searchFlag = false;
        int dbIndex;
        System.out.println(this.getUsername() + "'s borrowing history:");
        dbReader = history.listIterator();
        dbIndex = 1;
        while (dbReader.hasNext()) {
            String line = (String) dbReader.next();
            if (line.indexOf(this.getUsername()) == 0) {
                System.out.println(dbIndex + ". " + history);
                dbIndex++;
                searchFlag = true;
            }
        }
        if (!searchFlag)
            System.out.println("-- No records to display --");
    }

    // case 8: upgrade account to premium
    // function to upgrade the account
    public void upgradeAccount() {
        setType(Main.AccountType.PRO);
        bookLimit = 3;
    }
}
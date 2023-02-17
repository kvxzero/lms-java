package com.lms;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;


public class User extends Human implements Serializable, UserFunctions {

    @Serial
    private static final long serialVersionUID = 193193193;
    private static int numOfUsers = 0;
    private int bookLimit;
    private ArrayList<Book> borrowedBook = new ArrayList<>();
    private String searchQuery;

    // constructor
    User(String username, String password, cityList city, String email, String phNo, Main.AccountType type) {
        super(username, password, city, email, phNo);
        setId(numOfUsers+1);
        numOfUsers++;
        this.setType(type);
        if (type == Main.AccountType.USER)
            this.bookLimit = 1;
        else if (type == Main.AccountType.PRO)
            this.bookLimit = 3;
    }

    // necessary functions
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
    public static void setNumOfUsers(int retrievedData) {
        numOfUsers = retrievedData;
    }
    @Override
    public String toString() {
        if (this.getUsername().equals("")) {
            return getPhNo() + " (" + getCity() + ", " + getType() + ")";
        }
        return getUsername() + " (" + getCity() + ", " + getType() + ")";
    }
    public static int getNumOfUsers() {
        return User.numOfUsers;
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

    public void setBorrowedBook(Book book) {
        if (borrowedBook.size() < bookLimit) {
            borrowedBook.add(book);
        }
    }
    public void showBorrowedBooks() {
        int index = 0;
        System.out.print("Books borrowed : ");
        if (borrowedBook.size() == 0) {
            System.out.println("None");
            return;
        }
        System.out.println();
        ListIterator<Book> dbReader = borrowedBook.listIterator();
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
        } else if (Main.searchedBook.getStock() == 0) {
            System.out.println(Main.searchedBook + " | Currently: no copies are in stock");
        } else {
            System.out.println(Main.searchedBook + " | Currently: some copies are in stock");
        }
    }
    public void displayBooks(@NotNull ArrayList<Library> libraries) {
        boolean searchFlag = false;
        for (Library library : libraries) {
            Main.selectedLibrary = library;
            if (Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                System.out.println("\nBooks from: " + Main.selectedLibrary.getName());
                searchFlag = true;
                if (Main.selectedLibrary.getBooks().size() == 0) {
                    System.out.println("-- No books --");
                    continue;
                }
                for (Book book : Main.selectedLibrary.getBooks()) {
                    Main.searchedBook = book;
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
        switch (option) {
            case 1 -> searchByName(libraries);
            case 2 -> searchByGenre(libraries);
            case 3 -> {
                searchByName(libraries);
                System.out.println();
                searchByGenre(libraries);
            }
            default -> System.out.println("!-- Enter a valid input --!");
        }
    }
    private void searchByGenre(ArrayList<Library> libraries) {
        System.out.println("--- Searching by genre ---");
        boolean searchFlag;
        for (Library library : libraries) {
            searchFlag = false;
            Main.selectedLibrary = library;
            if (Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                System.out.println("\nBooks from: " + Main.selectedLibrary.getName());
                for (Book book : Main.selectedLibrary.getBooks()) {
                    Main.searchedBook = book;
                    if (Main.searchedBook.getGenre().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                        displayAvailability();
                        searchFlag = true;
                    }
                }
            }
            if (!searchFlag && Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity()))
                System.out.println("No results found!");
        }
    }
    private void searchByName(@NotNull ArrayList<Library> libraries) {
        System.out.println("--- Searching by name ---");
        boolean searchFlag;
        for (Library library : libraries) {
            searchFlag = false;
            Main.selectedLibrary = library;
            if (Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                System.out.println("\nBooks from: " + Main.selectedLibrary.getName());
                for (Book book : Main.selectedLibrary.getBooks()) {
                    Main.searchedBook = book;
                    if (Main.searchedBook.getName().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                        displayAvailability();
                        searchFlag = true;
                    }
                }
            }
            if (!searchFlag && Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity()))
                System.out.println("No results found!");
        }
    }

    // case 2: borrow a book
    // function to borrow books
    public boolean borrowBook (ArrayList<Library> libraries) {
        if (borrowedBook.size() < bookLimit) {
            System.out.print("Enter the book to be borrowed: ");
            searchQuery = Main.sc.next();
            for (Library library : libraries) {
                Main.selectedLibrary = library;
                if (Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                    for (Book book : Main.selectedLibrary.getBooks()) {
                        Main.searchedBook = book;
                        if (Main.searchedBook.getName().equalsIgnoreCase(searchQuery)) {
                            if (Main.searchedBook.setBorrowedUser(this)) {
                                Main.userAccount.setBorrowedBook(Main.searchedBook);
                                return true;
                            } else {
                                System.out.println("This book is already borrowed and is unavailable");
                                return false;
                            }
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
    public void returnAll() {
        if (this.getType() == Main.AccountType.USER) {
            Main.searchedBook = this.getBorrowedBook().get(0);
            if (this.returnBook(0)) {
                Main.searchedBook.bookReturned(this);
            }
        }
        if (this.getType() == Main.AccountType.PRO) {
            this.showBorrowedBooks();
            int dbIndex = 0;
            do {
                Main.searchedBook = getBorrowedBook().get(dbIndex);
                if (this.returnBook(dbIndex)) {
                    Main.searchedBook.bookReturned(this);
                }
            } while (getBorrowedBook().size() != 0);
            System.out.println("\nReturned all of the borrowed books successfully!");
        }
    }
    public boolean hasNoBooks() {
        return borrowedBook.size() == 0;
    }
    public boolean executeReturn() {
        if (this.getType() == Main.AccountType.USER) {
            Main.searchedBook = getBorrowedBook().get(0);
            if (this.returnBook(0)) {
                Main.searchedBook.bookReturned(this);
                return true;
            }
        }
        if (this.getType() == Main.AccountType.PRO) {
            this.showBorrowedBooks();
            System.out.print("Choose the book (number) to be returned: ");
            int dbIndex = Main.getInput();
            if (dbIndex == -9999) {
                System.out.println("!-- Enter a valid input --!");
                return false;
            }
            else {
                System.out.print("You chose to return: ");
                Main.searchedBook = getBorrowedBook().get(dbIndex - 1);
                System.out.println(Main.searchedBook.getName() + "\n");
                if (this.returnBook(dbIndex - 1)) {
                    Main.searchedBook.bookReturned(this);
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
    public void getStatus(ArrayList<Library> libraries, ArrayList<String> requestList) {
        if(this.getBorrowedBookId() == -9999)
            System.out.println("Current book   : null");
        else {
            System.out.println("Current book   : " + borrowedBook.get(0));
        }
        ListIterator<String> dbReader = requestList.listIterator();
        String line;
        while (dbReader.hasNext()) {
            line = dbReader.next();
            int i = line.indexOf(':');
            searchQuery = line.substring(0, i);
            if (searchQuery.equalsIgnoreCase(this.getUsername())) {
                System.out.print("Premium status : ");
                System.out.println(line.substring(i + 2, line.indexOf("~")));
                break;
            }
        }
    }

    // case 5: nearby libraries
    // function to find out nearby libraries
    public void nearbyLibraries(ArrayList<Library> libraries) {
        boolean searchFlag = false;
        int dbIndex = 1;
        for (Library library : libraries) {
            Main.selectedLibrary = library;
            if (this.getType() == Main.AccountType.USER) {
                if (Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                    searchFlag = true;
                    System.out.println(dbIndex + ". " + Main.selectedLibrary);
                    dbIndex++;
                }
            } else {
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
        password = Main.sc.next();
        if(password.equals(this.getPassword())) {
            System.out.print("Enter your new password: ");
            password = Main.sc.next();
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
        boolean searchFlag = false;
        int dbIndex;
        System.out.println(this.getUsername() + "'s borrowing history:");
        dbIndex = 1;
        for (String line : history) {
            if (line.indexOf(this.getUsername()) == 0) {
                System.out.println(dbIndex + ". " + line);
                dbIndex++;
                searchFlag = true;
            }
        }
        if (!searchFlag)
            System.out.println("-- No records to display --");
    }

    // case 8: change city
    // function to change the city is in the parent class

    // case 9: upgrade account to premium
    // function to upgrade the account
    public void upgradeAccount() {
        setType(Main.AccountType.PRO);
        bookLimit = 3;
    }
}
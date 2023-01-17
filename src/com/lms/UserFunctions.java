package com.lms;

import java.util.ArrayList;
import java.util.ListIterator;
import org.jetbrains.annotations.NotNull;

import static com.lms.Main.*;

public class UserFunctions {
    // variables for the respective functions
    static String username, searchQuery;
    static ListIterator dbReader;
    static int ProDbIndex;

    // function to display all books
    public static void displayBooks(@NotNull ArrayList<Book> books) {
        dbReader = books.listIterator();
        while (dbReader.hasNext()) {
            Main.searchedBook = (Book) dbReader.next();
            if (Main.searchedBook.availability()) {
                System.out.println(Main.searchedBook + " | Currently: in Library ");
            } else {
                System.out.println(Main.searchedBook + " | Currently: Borrowed by a user");
            }
        }
    }
    // function to search books with options
    public static void searchBooks(@NotNull ArrayList<Book> books, int option) {
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
    // function to search books by genre
    private static void searchByGenre(ArrayList<Book> books) {
        Main.searchFlag = false;
        dbReader = books.listIterator();
        System.out.println("--- Searching by genre ---");
        while (dbReader.hasNext()) {
            Main.searchedBook = (Book) dbReader.next();
            if (Main.searchedBook.getGenre().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                if(Main.searchedBook.availability()) {
                    System.out.println(Main.searchedBook + " | Currently: in Library");
                }
                else {
                    System.out.println(Main.searchedBook + " | Currently: Borrowed by a user");
                }
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("No results found!");
    }
    // function to search books by name
    private static void searchByName(@NotNull ArrayList<Book> books) {
        Main.searchFlag = false;
        dbReader = books.listIterator();
        System.out.println("--- Searching by name ---");
        while (dbReader.hasNext()) {
            Main.searchedBook = (Book) dbReader.next();
            if (Main.searchedBook.getName().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                if(Main.searchedBook.availability()) {
                    System.out.println(Main.searchedBook + " | Currently: in Library ");
                }
                else {
                    System.out.println(Main.searchedBook + " | Currently: Borrowed by a user");
                }
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("No results found!");
    }
    // function to borrow books
    public static boolean initBorrow(ArrayList<Book> books) {
        System.out.print("Enter the book to be borrowed: ");
        searchQuery = sc.next();
        dbReader = books.listIterator();
        while (dbReader.hasNext()) {
            Main.searchedBook = (Book) dbReader.next();
            if (Main.searchedBook.getName().equalsIgnoreCase(searchQuery)) {
                return true;
            }
        }
        return false;
    }
    // function to return borrowed books
    public static boolean initReturn(ArrayList<Book> books) {
        username = Main.userAccount.getName();
        if (Main.userAccount.getAccountType() == Main.AccountType.USER) {
            if (Main.userAccount.getBorrowedBookId() == -9999) {
                return false;
            }
            Main.searchedBook = books.get(Main.userAccount.getBorrowedBookId()-1);
        }
        else {
            if (!Main.userAccount.returnCheck()) {
                Main.userAccount.showBorrowedBooks(books);
                System.out.print("Choose the book (number) to be returned: ");
                ProDbIndex = getInput();
                if (ProDbIndex == -9999) {
                    System.out.println("!-- Enter a valid input --!");
                    return false;
                }
                Main.searchedBook = books.get(ProDbIndex - 1);
            }
        }
        return true;
    }
    // function to find out nearby libraries
    public static void nearbyLibraries(ArrayList<Location> locations) {
        int dbIndex = 1;
        dbReader = locations.listIterator();
        while(dbReader.hasNext()) {
            Main.selectedLocation = (Location) dbReader.next();
            if (userAccount.getAccountType() == AccountType.USER) {
                if (Main.selectedLocation.getLibCity().equalsIgnoreCase(Main.userAccount.getLocation())) {
                    System.out.println(dbIndex + ". " + Main.selectedLocation);
                    dbIndex++;
                }
            }
            else {
                System.out.println(dbIndex + ". " + Main.selectedLocation);
                dbIndex++;
            }
        }
    }
    // function to change the password
    public static void changePassword() {
        String password;
        System.out.print("Enter your current password: ");
        password = sc.next();
        if(password.equals(Main.userAccount.getPassword())) {
            System.out.print("Enter your new password: ");
            password = sc.next();
            Main.userAccount.setPassword(password);
            System.out.println("Password change successfully!");
        }
        else {
            System.out.println("Invalid password given, Aborted");
        }
    }

    public static void viewUserHistory() {
        ArrayList<String> borrowedHistory = Main.getHistory();
        int dbIndex;
        System.out.println(Main.userAccount.getName() + "'s borrowing history:");
        dbReader = borrowedHistory.listIterator();
        dbIndex = 1;
        while (dbReader.hasNext()) {
            String history = (String) dbReader.next();
            if(history.indexOf(Main.userAccount.getName()) == 0) {
                System.out.println(dbIndex + ". " + history);
                dbIndex++;
            }
        }
    }
}
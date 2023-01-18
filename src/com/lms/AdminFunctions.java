package com.lms;
import org.jetbrains.annotations.NotNull;
import static com.lms.Main.sc;
import static com.lms.Main.userAccount;

import java.util.ArrayList;

public class AdminFunctions extends RegUserFunctions {
    // function to add a new book
    public static Book newBook() {
        String bookName, bookGenre;
        System.out.print("Enter the book name: ");
        bookName = sc.nextLine();
        System.out.print("Enter the book genre: ");
        bookGenre = sc.nextLine();
        return new Book(bookName, bookGenre);
    }
    // function to delete a book (also removes the user associated with the book)
    public static void deleteBook(ArrayList<RegUser> users, ArrayList<Book> books) {
        Main.searchFlag = false;
        System.out.print("Enter the book to be deleted: ");
        searchQuery = sc.next();
        dbReader = books.listIterator();
        while (dbReader.hasNext()) {
            Main.searchedBook = (Book) dbReader.next();
            if (Main.searchedBook.getName().equalsIgnoreCase(searchQuery)) {
                if(Main.searchedBook.getBorrowedUser() != -9999) {
                    RegUser user = users.get(Main.searchedBook.getBorrowedUser()-1);
                    if(user.getAccountType() == Main.AccountType.PRO) {
                        ProUserIndex = 0;
                        while(ProUserIndex < 3) {
                            if (Main.searchedBook.getId() == user.getBorrowedBookId(ProUserIndex)) {
                                break;
                            }
                            ProUserIndex++;
                        }
                    }
                    ProUserIndex++;
                    Main.execReturnBook(user, Main.searchedBook);
                }
                dbReader.remove();
                System.out.println("Deleted successfully");
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("Book doesn't exist in the records");
    }
    // function to add a new user
    public static RegUser newUser(boolean flag) {
        String userName, userPassword, userLocation;
        System.out.print("Enter your username: ");
        userName = sc.next();
        System.out.print("Enter your password: ");
        userPassword = sc.next();
        if(flag) {
            return new Admin(userName, userPassword);
        }
        else {
            System.out.print("Enter your location: ");
            userLocation = sc.next();
            return new RegUser(userName, userPassword, userLocation);
        }
    }
    // function to delete a user (also releases the book if it was borrowed by the user)
    public static void deleteUser(ArrayList<RegUser> users, ArrayList<Book> books) {
        Main.searchFlag = false;
        System.out.print("Enter the user to be deleted: ");
        searchQuery = sc.next();
        dbReader = users.listIterator();
        while (dbReader.hasNext()) {
            Main.userAccount = (RegUser) dbReader.next();
            if (Main.userAccount.getName().equalsIgnoreCase(searchQuery)) {
                if(Main.userAccount.getAccountType() == Main.AccountType.USER) {
                    if(Main.userAccount.getBorrowedBookId() != -9999)
                         books.get(Main.userAccount.getBorrowedBookId()-1).bookReturned();
                }
                else {
                    if (userAccount.getBorrowedBookId(0) != -9999)
                        books.get(userAccount.getBorrowedBookId(0)-1).bookReturned();
                    if (userAccount.getBorrowedBookId(1) != -9999)
                        books.get(userAccount.getBorrowedBookId(1)-1).bookReturned();
                    if (userAccount.getBorrowedBookId(2) != -9999)
                        books.get(userAccount.getBorrowedBookId(2)-1).bookReturned();
                }
                dbReader.remove();
                System.out.println("Deleted successfully");
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("User doesn't exist in the records");
    }
    // function to delete an admin
    public static void deleteAdmin(ArrayList<Admin> admins) {
        Main.searchFlag = false;
        System.out.print("Enter the admin to be deleted: ");
        searchQuery = sc.next();
        dbReader = admins.listIterator();
        while (dbReader.hasNext()) {
            Main.adminAccount = (Admin) dbReader.next();
            if (Main.adminAccount.getName().equalsIgnoreCase(searchQuery)) {
                dbReader.remove();
                System.out.println("Deleted successfully");
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("Admin doesn't exist in the records");
    }
    // function to list all available users
    public static void usersList(ArrayList<RegUser> users, ArrayList<Book> books) {
        int dbIndex = 1;
        String bookStat;
        dbReader = users.listIterator();
        System.out.println("List of all the available users:");
        while (dbReader.hasNext()) {
            Main.userAccount = (RegUser) dbReader.next();
            if(Main.userAccount.getBorrowedBookId() == -9999)
                bookStat = "Currently no borrowed books";
            else
                if(userAccount.getAccountType() == Main.AccountType.USER)
                    bookStat = "Currently borrowed: " + books.get(Main.userAccount.getBorrowedBookId()-1).getName();
                else {
                    bookStat = "Currently borrowed: " + books.get(Main.userAccount.getBorrowedBookId(0) - 1).getName();
                    if (userAccount.getBorrowedBookId(1) != -9999)
                        bookStat += ", " + books.get(Main.userAccount.getBorrowedBookId(1)-1).getName();
                    if (userAccount.getBorrowedBookId(2) != -9999)
                        bookStat += ", " + books.get(Main.userAccount.getBorrowedBookId(2)-1).getName();
                }
            System.out.println(dbIndex + ". " + Main.userAccount + " | " + bookStat);
            dbIndex++;
        }
    }
    // function to list all available admins
    public static void adminsList(ArrayList<Admin> admins) {
        int dbIndex = 1;
        dbReader = admins.listIterator();
        System.out.println("List of all the available admins:");
        while (dbReader.hasNext()) {
            System.out.println(dbIndex + ". " + dbReader.next());
            dbIndex++;
        }
    }
    // function to view borrowing history of the library
    public static void viewBorrowingHistory() {
        ArrayList<String> borrowedHistory = Main.getHistory();
        int dbIndex;
        System.out.println("Borrowing history:");
        dbReader = borrowedHistory.listIterator();
        dbIndex = 1;
        while (dbReader.hasNext()) {
            System.out.println(dbIndex + ". " + dbReader.next());
            dbIndex++;
        }
    }
    // function to add new library location
    public static Location newLocation() {
        String libCity, libName;
        System.out.print("Enter the Library Name: ");
        libName = sc.next();
        System.out.print("Enter the Library City: ");
        libCity = sc.next();
        return new Location(libName, libCity);
    }
    // function to delete a library location
    public static void deleteLocation(ArrayList<Location> locations) {
        Main.searchFlag = false;
        System.out.print("Enter the library to be deleted: ");
        searchQuery = sc.nextLine();
        dbReader = locations.listIterator();
        while (dbReader.hasNext()) {
            Main.selectedLocation = (Location) dbReader.next();
            if (Main.selectedLocation.getLibName().equals(searchQuery)) {
                dbReader.remove();
                System.out.println("Deleted successfully");
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("Given library doesn't exist in the records");
    }
    // function to list all libraries
    public static void locationsList(ArrayList<Location> locations) {
        int dbIndex = 1;
        dbReader = locations.listIterator();
        System.out.println("List of all the available libraries:");
        while (dbReader.hasNext()) {
            System.out.println(dbIndex + ". " + dbReader.next());
            dbIndex++;
        }
    }
    // function to search books with options along with borrowers
    public static void searchBooks(@NotNull ArrayList<Book> books, ArrayList<RegUser> users, int option) {
        switch(option) {
            case 1:
                searchByName(books, users);
                break;

            case 2:
                searchByGenre(books, users);
                break;

            case 3:
                searchByName(books, users);
                System.out.println();
                searchByGenre(books, users);
                break;

            default:
                System.out.println("!-- Enter a valid input --!");
        }
    }
    // function to search books by genre
    private static void searchByGenre(ArrayList<Book> books, ArrayList<RegUser> users) {
        Main.searchFlag = false;
        dbReader = books.listIterator();
        System.out.println("--- Searching by genre ---");
        while (dbReader.hasNext()) {
            Main.searchedBook = (Book) dbReader.next();
            if (Main.searchedBook.getGenre().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                if(Main.searchedBook.availability()) {
                    System.out.println(Main.searchedBook + " | Currently: in Library ");
                }
                else {
                    System.out.println(Main.searchedBook + " | Currently: Borrowed by " + users.get(Main.searchedBook.getBorrowedUser()-1));
                }
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("No results found!");
    }
    // function to search books by their name
    private static void searchByName(@NotNull ArrayList<Book> books, ArrayList<RegUser> users) {
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
                    System.out.println(Main.searchedBook + " | Currently: Borrowed by " + users.get(Main.searchedBook.getBorrowedUser()-1));
                }
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("No results found!");
    }
    // function to display all books along with borrowers
    public static void displayBooks(@NotNull ArrayList<Book> books, ArrayList<RegUser> users) {
        dbReader = books.listIterator();
        while (dbReader.hasNext()) {
            Main.searchedBook = (Book) dbReader.next();
            if (Main.searchedBook.availability()) {
                System.out.println(Main.searchedBook + " | Currently: in Library ");
            } else {
                System.out.println(Main.searchedBook + " | Currently: Borrowed by " + users.get(Main.searchedBook.getBorrowedUser() - 1));
            }
        }
    }
    // function to view current status of the user
    public static void getStatus(ArrayList<Book> books) {
        if(Main.userAccount.getBorrowedBookId() == -9999)
            System.out.println("Current book: null");
        else {
            Main.searchedBook = books.get(Main.userAccount.getBorrowedBookId()-1);
            System.out.println("Current book: " + Main.searchedBook);
        }
    }
}
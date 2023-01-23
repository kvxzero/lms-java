package com.lms;

import org.jetbrains.annotations.NotNull;
import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.ListIterator;
import java.util.Scanner;

public class Main {
    // enum for account types
    enum AccountType {
        ADMIN, USER, PRO
    }

    // public scanner for all the functions
    public static Scanner sc = new Scanner(System.in);

    // class objects for data inputs
    static Book searchedBook;
    static Admin adminAccount;
    static RegUser userAccount;
    static Library selectedLibrary;
    static ListIterator dbReader;

    // history arraylist
    private static ArrayList<String> borrowedHistory = new ArrayList<>();

    // id of the logged-in user
    static int loginId;

    // flags for loops
    static boolean searchFlag, loginFlag = false;

    // object serialization object for saving data
    private static ObjectOutputStream saveData;

    // variables to read thru .dat
    private static ListIterator displayReader;

    // function to handle exception handling while getting menu input
    public static int getInput() {
        int choice;
        try {
            choice = sc.nextInt();
        }
        catch (InputMismatchException e) {
            choice = -9999;
            sc.next();
        }
        return choice;
    }

    // functions for storing and reading files
    public static int retrievingData(@NotNull ArrayList loadingType) {
        displayReader = loadingType.listIterator();
        while(displayReader.hasNext())
            displayReader.next();
        return displayReader.nextIndex();
    }
    public static void storingData(File file, ArrayList inputData) throws IOException {
        saveData = new ObjectOutputStream(new FileOutputStream(file));
        saveData.writeObject(inputData);
        saveData.close();
    }

    // function to read the borrow history as ADMIN
    public static ArrayList<String> getHistory() {
        return borrowedHistory;
    }
    public static void main(String[] args) throws Exception {
        // object serialization object for loading data
        ObjectInputStream loadData;

        // variables for various functions
        int choice;
        String userName, userPassword;
        sc.useDelimiter("\n");

        // array lists for all the different data persisted
        ArrayList<Book> books = new ArrayList<>();
        ArrayList<Admin> admins = new ArrayList<>();
        ArrayList<RegUser> users = new ArrayList<>();
        ArrayList<Library> libraries = new ArrayList<>();

        // .dat file in which the data is stored
        File bookDb = new File("src/com/lms/res/BooksList.dat");
        File adminDb = new File("src/com/lms/res/AdminsList.dat");
        File userDb = new File("src/com/lms/res/UsersList.dat");
        File historyDb = new File("src/com/lms/res/BorrowedList.dat");
        File locationDb = new File("src/com/lms/res/Locations.dat");

        // loading up previously gathered data
        if (bookDb.isFile()) {
            loadData = new ObjectInputStream(new FileInputStream(bookDb));
            books = (ArrayList<Book>) loadData.readObject();
            loadData.close();
            Book.setNumOfBooks(retrievingData(books));
        }
        if (adminDb.isFile()) {
            loadData = new ObjectInputStream(new FileInputStream(adminDb));
            admins = (ArrayList<Admin>) loadData.readObject();
            loadData.close();
        }
        if (userDb.isFile()) {
            loadData = new ObjectInputStream(new FileInputStream(userDb));
            users = (ArrayList<RegUser>) loadData.readObject();
            loadData.close();
            RegUser.setNumOfUsers(retrievingData(users));
        }
        if (historyDb.isFile()) {
            loadData = new ObjectInputStream(new FileInputStream(historyDb));
            borrowedHistory = (ArrayList<String>) loadData.readObject();
            loadData.close();
        }
        if (locationDb.isFile()) {
            loadData = new ObjectInputStream(new FileInputStream(locationDb));
            libraries = (ArrayList<Library>) loadData.readObject();
            loadData.close();
        }

        // LoginHandler object and Enum variable for account type
        LoginHandler loginObject = new LoginHandler(users, admins);
        AccountType accountType = null;

        // Start of the UI
        System.out.println("--- Welcome to LMS ---");
        do {
            // looped login menu
            System.out.println("\n----- Login now -----\n");
            System.out.println("1. Admin Login");
            System.out.println("2. User Login");
            System.out.println("3. New user? Sign up now!\n");

            System.out.print("Choice: ");
            choice = getInput();

            switch (choice) {
                case 1:
                    accountType = AccountType.ADMIN;
                    System.out.print("Enter your username: ");
                    userName = sc.next();
                    System.out.print("Enter your password: ");
                    userPassword = sc.next();
                    if (loginObject.userLoginRequest(userName, userPassword, accountType) == 200) {
                        loginFlag = true;
                        loginId = loginObject.getLoginID(userName, accountType);
                    }
                    break;

                case 2:
                    accountType = AccountType.USER;
                    System.out.print("Enter your username: ");
                    userName = sc.next();
                    System.out.print("Enter your password: ");
                    userPassword = sc.next();
                    if (loginObject.userLoginRequest(userName, userPassword, accountType) == 200) {
                        loginFlag = true;
                        loginId = loginObject.getLoginID(userName, accountType);
                    }
                    break;

                case 3:
                    users.add((RegUser) User.newUser(false));
                    System.out.println("Signed up successfully! ^^");
                    storingData(userDb, users);
                    break;

//                TEST CODE TO ADD NEW ADMIN (ONLY FOR DEVS IN CASE OF .DAT FILE IS UNREADABLE OR DESTROYED)
                case 4:
                    System.out.println("!-- Warning! Test feature accessed --!");
                    System.out.println("Add a new admin (use only on data loss)");
                    admins.add((Admin) User.newUser(true));
                    storingData(adminDb, admins);
                    break;

                default:
                    System.out.println("!-- Enter a valid input --!");
            }
        } while (!loginFlag);
        if (accountType == AccountType.ADMIN) {
            // get the logged-in user account for this session via ID
            // convert the below code to a function that facilitates both admins and users
            displayReader = admins.listIterator();
            while (displayReader.hasNext()) {
                adminAccount = (Admin) displayReader.next();
                if (adminAccount.getUsername().equals(admins.get(loginId).getUsername())) {
                    System.out.println("\nWelcome " + adminAccount);
                    break;
                }
            }
            do {
                // looped admin dashboard
                System.out.println("\n--- Admin dashboard ---\n");
                System.out.println("0. Books History\t 7. Add an admin");
                System.out.println("1. Add book\t\t\t 8. Remove an admin");
                System.out.println("2. Remove a book\t 9. List all users");
                System.out.println("3. View books\t\t 10. List all admins");
                System.out.println("4. Search books\t\t 11. Add new library location");
                System.out.println("5. Add user\t\t\t 12. Remove library location");
                System.out.println("6. Remove user\t\t 13. List all libraries\n");
                System.out.println("99. Logout\n");

                // switch case ladder
                System.out.print("Choice: ");
                choice = getInput();

                switch (choice) {
                    case 0: // Borrowing history, ADMIN CASE
                        adminAccount.viewBorrowingHistory();
                        break;

                    case 1: // Add a book, ADMIN CASE
                        int multipleBooks;
                        System.out.print("Enter the number of books to be added: ");
                        multipleBooks = getInput();
                        if (multipleBooks == -9999) {
                            System.out.println("!-- Enter a valid input --!");
                            break;
                        }
                        sc.nextLine();
                        while(multipleBooks > 0) {
                            books.add(adminAccount.newBook());
                            System.out.println();
                            multipleBooks--;
                        }
                        storingData(bookDb, books);
                        break;

                    case 2: // Delete a book, ADMIN CASE
                        adminAccount.deleteBook(users, books);
                        storingData(userDb, users);
                        storingData(bookDb, books);
                        break;

                    case 3: // Display all books, ADMIN CASE
                        adminAccount.displayBooks(books, users);
                        break;

                    case 4: // Search for a book, ADMIN AND USER CASE
                        System.out.println("How do you want to search?");
                        System.out.println("1. By name");
                        System.out.println("2. By genre");
                        System.out.println("3. By both");
                        System.out.print("\nChoice: ");
                        int searchChoice = getInput();
                        System.out.print("Enter your search query: ");
                        adminAccount.setSearchQuery(sc.next());
                        adminAccount.searchBooks(books, users, searchChoice);
                        break;

                    case 5: // Add a user, ADMIN CASE
                        users.add((RegUser) User.newUser(false));
                        System.out.println("New user created successfully!");
                        storingData(userDb, users);
                        break;

                    case 6: // Delete a user, ADMIN CASE
                        adminAccount.deleteUser(users, books);
                        storingData(userDb, users);
                        storingData(bookDb, books);
                        break;

                    case 7: // Add an admin, ADMIN CASE
                        admins.add((Admin) User.newUser(true));
                        System.out.println("New admin created successfully!");
                        storingData(adminDb, admins);
                        break;

                    case 8: // Remove an admin, ADMIN CASE
                        adminAccount.deleteAdmin(admins);
                        break;

                    case 9: // Display all users, ADMIN CASE
                        adminAccount.usersList(users, books);
                        break;

                    case 10: // Display all admins, ADMIN CASE
                        adminAccount.adminsList(admins);
                        break;

                    case 11: // Add a new library, ADMIN CASE
                        libraries.add(adminAccount.newLocation());
                        storingData(locationDb, libraries);
                        break;

                    case 12: // Remove a library from the db, ADMIN CASE
                        adminAccount.deleteLocation(libraries);
                        storingData(locationDb, libraries);
                        break;

                    case 13: // List all libraries available
                        adminAccount.librariesList(libraries);
                        break;

                    case 99: // Log out
                        loginFlag = false;
                        break;

                    default:
                        System.out.println("!-- Enter a valid input --!");
                }
            } while (loginFlag);
        }

        else {
            // get the logged-in user account for this session via ID
            // convert the below code to a function that facilitates both admins and users
            displayReader = users.listIterator();
            while (displayReader.hasNext()) {
                userAccount = (RegUser) displayReader.next();
                if (Main.userAccount.getUsername().equals(users.get(loginId).getUsername())) {
                    System.out.println("\nWelcome " + userAccount);
                    break;
                }
            }

            do {
                // looped user dashboard
                System.out.println("\n--- User dashboard ---\n");
                System.out.println("0. Display all books");
                System.out.println("1. Search books");
                System.out.println("2. Borrow a book");
                System.out.println("3. Return borrowed book");
                System.out.println("4. View current status");
                System.out.println("5. Libraries in my location");
                System.out.println("6. Change password");
                System.out.println("7. View my history");
                System.out.println("8. Upgrade account\n");
                System.out.println("99. Log out\n");

                // switch case ladder
                System.out.print("Choice: ");
                choice = getInput();

                switch (choice) {
                    case 0: // display all books, USER CASE
                        userAccount.displayBooks(books);
                        break;

                    case 1: // search for a book, USER CASE
                        System.out.println("How do you want to search?");
                        System.out.println("1. By name");
                        System.out.println("2. By genre");
                        System.out.println("3. By both");
                        System.out.print("\nChoice: ");
                        int searchChoice = getInput();
                        System.out.print("Enter your search query: ");
                        userAccount.setSearchQuery(sc.next());
                        userAccount.searchBooks(books, searchChoice);
                        break;

                    case 2: // borrow a book, USER CASE
                        if (userAccount.borrowBook(books)) {
                            Main.borrowedHistory.add(userAccount.getUsername() + " has borrowed " + searchedBook.getName());
                            System.out.println(userAccount.getUsername() + " has borrowed "
                                    + searchedBook.getName() + " successfully!");
                            saveData = new ObjectOutputStream(new FileOutputStream(historyDb));
                            saveData.writeObject(borrowedHistory);
                            saveData.close();
                            storingData(userDb, users);
                            storingData(bookDb, books);
                        } else {
                            System.out.println("Borrow operation has failed...");
                        }
                        break;

                    case 3: // return a book, USER CASE
                        if(userAccount.hasNoBooks()) {
                            System.out.println(userAccount.getUsername() + " has no book borrowed at the moment");
                            break;
                        }
                        if (userAccount.executeReturn(books)) {
                            Main.borrowedHistory.add(userAccount.getUsername() + " has returned " + searchedBook.getName());
                            System.out.println(userAccount.getUsername() + " has returned "
                                    + searchedBook.getName() + " successfully!");
                            saveData = new ObjectOutputStream(new FileOutputStream(historyDb));
                            saveData.writeObject(borrowedHistory);
                            saveData.close();
                            storingData(userDb, users);
                            storingData(bookDb, books);
                        }
                        else {
                            System.out.println("Return operation has failed...");
                        }
                        break;

                    case 4: // view current status of books, USER CASE
                        System.out.println("Current user: " + Main.userAccount);
                        if (userAccount.getType() == AccountType.USER)
                            userAccount.getStatus(books);
                        else
                            userAccount.showBorrowedBooks(books);
                        break;

                    case 5: // find nearby Libraries, USER CASE
                        System.out.println("Libraries near you: ");
                        userAccount.nearbyLibraries(libraries);
                        break;

                    case 6: // change the user-password for login
                        userAccount.changePassword();
                        storingData(userDb, users);
                        break;

                    case 7: // view the user's borrowing history
                        userAccount.viewUserHistory();
                        break;

                    case 8: // upgrade account to premium
                        userAccount.upgradeAccount();
                        storingData(userDb, users);
                        System.out.println("You have successfully subscribed to premium ^^");
                        break;

                    case 99: // log out
                        loginFlag = false;
                        break;

                    default:
                        System.out.println("!-- Enter a valid input --!");
                }
            } while (loginFlag);
        }
        System.out.println("Logged out, Bye bye ~");
    }
}
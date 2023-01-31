package com.lms;

import org.jetbrains.annotations.NotNull;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    // enum for account types
    enum AccountType {
        ADMIN, USER, PRO
    }

    // public scanner for all the functions
    public static Scanner sc = new Scanner(System.in);

    // class objects for data inputs
    static User loginAccount;
    static Book searchedBook;
    static Admin adminAccount;
    static RegUser userAccount;
    static Library selectedLibrary;
    static ListIterator dbReader;

    // id of the logged-in user
    static int loginId;
    protected static String userName, userPassword, userLocation, userEmail, userPhNo;

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
    public static void storingData(File file, ArrayList[] inputData) throws IOException {
        saveData = new ObjectOutputStream(new FileOutputStream(file));
        saveData.writeObject(inputData);
        saveData.close();
    }

    // function to read the borrow history as ADMIN

    public static void main(String[] args) throws Exception {

        // object serialization object for loading data
        ObjectInputStream loadData;

        // variables for various functions
        int choice;
        String libChoice;
        sc.useDelimiter("\n");
        ListIterator libReader;

        // array lists for all the different data persisted
        ArrayList<Admin> admins;
        ArrayList<RegUser> users;
        ArrayList<Library> libraries;
        ArrayList<String> borrowedHistory;
        ArrayList[] data;

        // .dat file in which the data is stored
        File masterData = new File("src/com/lms/res/masterData.dat");

        if (masterData.isFile()) {
            loadData = new ObjectInputStream(new FileInputStream(masterData));
            data = (ArrayList[]) loadData.readObject();
            admins = data[0];
            users = data[1];
            libraries = data[2];
            borrowedHistory = data[3];
            RegUser.setNumOfUsers(retrievingData(users));
            Admin.setNumOfAdmins(retrievingData(admins));
            loadData.close();
        }
        else {
            admins = new ArrayList<>();
            users = new ArrayList<>();
            libraries = new ArrayList<>();
            borrowedHistory = new ArrayList<>();
            data = new ArrayList[4];
            data[0] = admins;
            data[1] = users;
            data[2] = libraries;
            data[3] = borrowedHistory;
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
                    loginFlag = loginObject.initiateLogin(accountType);
                    storingData(masterData, data);
                    break;

                case 2:
                    accountType = AccountType.USER;
                    loginFlag = loginObject.initiateLogin(accountType);
                    storingData(masterData, data);
                    break;

                case 3:
                    if(loginObject.validateInformation(users, true)) {
                        users.add(new RegUser(userName, userPassword, userLocation, userEmail, userPhNo));
                        System.out.println("Signed up successfully! ^^");
                        storingData(masterData, data);
                    }
                    break;

//                TEST CODE TO ADD NEW ADMIN (ONLY FOR DEVS IN CASE OF .DAT FILE IS UNREADABLE OR DESTROYED)
                case 4:
                    System.out.println("!-- WARNING --!");
                    System.out.println("!-- PROCEED WITH CAUTION --!");
                    if(loginObject.validateInformation(admins, true)) {
                        admins.add(new Admin(userName, userPassword, userLocation, userEmail, userPhNo));
                        System.out.println("New admin created successfully!");
                        storingData(masterData, data);
                    }
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
                        adminAccount.viewBorrowingHistory(borrowedHistory);
                        break;

                    case 1: // Add a book, ADMIN CASE
                        int multipleBooks;
                        searchFlag = false;
                        System.out.println("Choose the library to be managed: ");
                        libReader = libraries.listIterator();
                        while (libReader.hasNext()) {
                            Library lib = (Library) libReader.next();
                            if (lib.getCity().equalsIgnoreCase(adminAccount.getCity())) {
                                System.out.println("> " + lib.getName());
                                searchFlag = true;
                            }
                        }
                        if (!searchFlag) {
                            System.out.println("---- No libraries found! ----");
                            System.out.println("-- Add a library to manage --");
                            break;
                        }
                        searchFlag = false;
                        System.out.print("Choice: ");
                        sc.nextLine();
                        libChoice = sc.next();
                        if (libChoice.equals("")) {
                            System.out.println("!-- Enter a valid input --!");
                            break;
                        }
                        libReader = libraries.listIterator();
                        while (libReader.hasNext()) {
                            Library lib = (Library) libReader.next();
                            if (lib.getName().equalsIgnoreCase(libChoice)
                                    && lib.getCity().equalsIgnoreCase(adminAccount.getCity())) {
                                selectedLibrary = lib;
                                searchFlag = true;
                                break;
                            }
                        }
                        if (!searchFlag) {
                            System.out.println("Well, that library doesn't exist.");
                            break;
                        }
                        System.out.print("Enter the number of books to be added: ");
                        multipleBooks = getInput();
                        if (multipleBooks == -9999) {
                            System.out.println("!-- Enter a valid input --!");
                            break;
                        }
                        sc.nextLine();
                        while(multipleBooks > 0) {
                            selectedLibrary.getBooks().add(adminAccount.newBook(selectedLibrary));
                            System.out.println();
                            multipleBooks--;
                        }
                        storingData(masterData, data);
                        break;

                    case 2: // Delete a book, ADMIN CASE
                        System.out.println("Choose the library to be managed: ");
                        libReader = libraries.listIterator();
                        while (libReader.hasNext()) {
                            Library lib = (Library) libReader.next();
                            if (lib.getCity().equalsIgnoreCase(adminAccount.getCity())) {
                                System.out.println("> " + lib.getName());
                            }
                        }
                        System.out.print("Choice: ");
                        sc.nextLine();
                        libChoice = sc.next();
                        if (libChoice.equals("")) {
                            System.out.println("!-- Enter a valid input --!");
                            break;
                        }
                        libReader = libraries.listIterator();
                        while (libReader.hasNext()) {
                            Library lib = (Library) libReader.next();
                            if (lib.getName().equals(libChoice)) {
                                selectedLibrary = lib;
                                searchFlag = true;
                                break;
                            }
                        }
                        adminAccount.deleteBook(users);
                        storingData(masterData, data);
                        break;

                    case 3: // Display all books, ADMIN CASE
                        adminAccount.displayBooks(libraries, users);
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
                        adminAccount.searchBooks(libraries, searchChoice);
                        break;

                    case 5: // Add a user, ADMIN CASE
                        if (loginObject.inviteUser(users)) {
                            userLocation = adminAccount.getCity();
                            users.add(new RegUser(null, null, adminAccount.getCity(), userEmail, userPhNo));
                            storingData(masterData, data);
                            System.out.println("New user created successfully!");
                        }
                        break;

                    case 6: // Delete a user, ADMIN CASE // broken for deleting users with borrowed books
                        adminAccount.deleteUser(users, libraries);
                        storingData(masterData, data);
                        break;

                    case 7: // Add an admin, ADMIN CASE
                        if (loginObject.inviteUser(admins)) {
                            userLocation = adminAccount.getCity();
                            admins.add(new Admin(null, null, adminAccount.getCity(), userEmail, userPhNo));
                            storingData(masterData, data);
                            System.out.println("New admin created successfully!");
                        }
                        break;

                    case 8: // Remove an admin, ADMIN CASE
                        adminAccount.deleteAdmin(admins);
                        break;

                    case 9: // Display all users, ADMIN CASE
                        adminAccount.usersList(users, libraries);
                        break;

                    case 10: // Display all admins, ADMIN CASE
                        adminAccount.adminsList(admins);
                        break;

                    case 11: // Add a new library, ADMIN CASE
                        System.out.print("Enter the number of libraries to be added: ");
                        int multipleLocation = getInput();
                        if (multipleLocation == -9999) {
                            System.out.println("!-- Enter a valid input --!");
                            break;
                        }
                        sc.nextLine();
                        while(multipleLocation > 0) {
                            libraries.add(adminAccount.newLocation());
                            System.out.println();
                            multipleLocation--;
                        }
                        storingData(masterData, data);
                        break;

                    case 12: // Remove a library from the db, ADMIN CASE
                        adminAccount.deleteLocation(libraries, users);
                        storingData(masterData, data);
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
                        userAccount.displayBooks(libraries);
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
                        userAccount.searchBooks(libraries, searchChoice);
                        break;

                    case 2: // borrow a book, USER CASE
                        userAccount.displayBooks(libraries);
                        System.out.println();
                        if (userAccount.borrowBook(libraries)) {
                            borrowedHistory.add(userAccount.getUsername() + " has borrowed " + searchedBook.getName());
                            System.out.println(userAccount.getUsername() + " has borrowed "
                                    + searchedBook.getName() + " successfully!");
                            storingData(masterData, data);
                        } else {
                            System.out.println("Borrow operation has failed...");
                        }
                        break;

                    case 3: // return a book, USER CASE
                        if(userAccount.hasNoBooks()) {
                            System.out.println(userAccount.getUsername() + " has no book borrowed at the moment");
                            break;
                        }
                        if (userAccount.executeReturn()) {
                            borrowedHistory.add(userAccount.getUsername() + " has returned " + searchedBook.getName());
                            System.out.println(userAccount.getUsername() + " has returned "
                                    + searchedBook.getName() + " successfully!");
                            storingData(masterData, data);
                        }
                        else {
                            System.out.println("Return operation has failed...");
                        }
                        break;

                    case 4: // view current status of books, USER CASE
                        System.out.println("Current user: " + Main.userAccount);
                        if (userAccount.getType() == AccountType.USER)
                            userAccount.getStatus(libraries);
                        else
                            userAccount.showBorrowedBooks();
                        break;

                    case 5: // find nearby Libraries, USER CASE
                        System.out.println("Libraries near you: ");
                        userAccount.nearbyLibraries(libraries);
                        break;

                    case 6: // change the user-password for login
                        userAccount.changePassword();
                        storingData(masterData, data);
                        break;

                    case 7: // view the user's borrowing history
                        userAccount.viewUserHistory(borrowedHistory);
                        break;

                    case 8: // upgrade account to premium
                        userAccount.upgradeAccount();
                        storingData(masterData, data);
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
package com.lms;

import org.jetbrains.annotations.NotNull;
import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.ListIterator;
import java.util.Scanner;

import static com.lms.UserFunctions.*;
import static com.lms.AdminFunctions.*;

public class Main {
    // enum for account types
    enum AccountType {
        ADMIN, USER, PRO;
    }

    // public scanner for all the functions
    public static Scanner sc = new Scanner(System.in);

    // class objects for data inputs
    static Book searchedBook;
    static Admin adminAccount;
    static RegUser userAccount;
    static Location selectedLocation;

    // id of the logged-in user
    static int loginId;

    // flags for loops
    static boolean adminFlag, searchFlag, loginFlag = false;

    // object serialization object for saving data
    private static ObjectOutputStream saveData;
    // history arraylist
    private static ArrayList<String> borrowedHistory = new ArrayList<>();

    // variables to read thru .dat
    private static ListIterator dbReader;

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
        dbReader = loadingType.listIterator();
        while(dbReader.hasNext())
            dbReader.next();
        return dbReader.nextIndex();
    }
    public static void storingData(File file, ArrayList inputData) throws IOException {
        saveData = new ObjectOutputStream(new FileOutputStream(file));
        saveData.writeObject(inputData);
        saveData.close();
    }

    // functions for borrowing and returning books
    private static boolean execBorrowBook(@NotNull RegUser user, Book book) {
        if(book.setBorrowedUser(user)) {
            borrowedHistory.add(user.getName() + " has borrowed " + book.getName());
            System.out.println(user.getName() + " has borrowed " + book.getName() + " successfully!");
            user.setBorrowedBook(book);
            return true;
        }
        return false;
    }
    public static void execReturnBook(@NotNull RegUser user, Book book) {
        if (user.getAccountType() == AccountType.USER) {
            if (user.returnBook(0)) {
                book.bookReturned();
                System.out.println(user.getName() + " has returned " + book.getName() + " successfully!");
                borrowedHistory.add(user.getName() + " has returned " + book.getName());
            }
        }
        else {
            if (user.returnBook(ProDbIndex - 1)) {
                book.bookReturned();
                System.out.println(user.getName() + " has returned " + book.getName() + " successfully!");
                borrowedHistory.add(user.getName() + " has returned " + book.getName());
            }
        }
    }

    // function to read the borrow history as ADMIN
    public static ArrayList<String> getHistory() {
        return borrowedHistory;
    }
    public static void main(String[] args) throws Exception {
        // object serialization object for loading data
        ObjectInputStream loadData;

        // variables for various functions
        int choice, multipleBooks, searchChoice;
        String userName, userPassword;
        sc.useDelimiter("\n");

        // array lists for all the different data persisted
        ArrayList<Book> books = new ArrayList<>();
        ArrayList<Admin> admins = new ArrayList<>();
        ArrayList<RegUser> users = new ArrayList<>();
        ArrayList<Location> locations = new ArrayList<>();

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
            locations = (ArrayList<Location>) loadData.readObject();
            loadData.close();
        }

        // LoginHandler object and Enum variable for account type
        LoginHandler loginObject = new LoginHandler(users, admins);
        AccountType accountType;

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
                        adminFlag = true;
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
                        adminFlag = false;
                        loginId = loginObject.getLoginID(userName, accountType);
                    }
                    break;

                case 3:
                    users.add(newUser(false));
                    System.out.println("Signed up successfully! ^^");
                    storingData(userDb, users);
                    break;

//                TEST CODE TO ADD NEW ADMIN (ONLY FOR DEVS IN CASE OF .DAT FILE IS UNREADABLE OR DESTROYED)
                case 4:
                    System.out.println("!-- Warning! Test feature accessed --!");
                    System.out.println("Add a new admin (use only on data loss)");
                    admins.add((Admin) newUser(true));
                    storingData(adminDb, admins);
                    break;

                default:
                    System.out.println("!-- Enter a valid input --!");
            }
        } while (!loginFlag);
        if (adminFlag) {
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
                        viewBorrowingHistory();
                        break;

                    case 1: // Add a book, ADMIN CASE
                        System.out.print("Enter the number of books to be added: ");
                        multipleBooks = sc.nextInt();
                        sc.nextLine();
                        while(multipleBooks > 0) {
                            books.add(newBook());
                            System.out.println();
                            multipleBooks--;
                        }
                        storingData(bookDb, books);
                        break;

                    case 2: // Delete a book, ADMIN CASE
                        deleteBook(users, books);
                        storingData(userDb, users);
                        storingData(bookDb, books);
                        break;

                    case 3: // Display all books, ADMIN CASE
                        displayBooks(books, users);
                        break;

                    case 4: // Search for a book, ADMIN AND USER CASE
                        System.out.println("How do you want to search?");
                        System.out.println("1. By name");
                        System.out.println("2. By genre");
                        System.out.println("3. By both");
                        System.out.print("\nChoice: ");
                        searchChoice = getInput();
                        System.out.print("Enter your search query: ");
                        UserFunctions.searchQuery = sc.next();
                        searchBooks(books, users, searchChoice);
                        break;

                    case 5: // Add a user, ADMIN CASE
                        users.add(newUser(false));
                        System.out.println("New user created successfully!");
                        storingData(userDb, users);
                        break;

                    case 6: // Delete a user, ADMIN CASE
                        deleteUser(users, books);
                        storingData(userDb, users);
                        storingData(bookDb, books);
                        break;

                    case 7: // Add an admin, ADMIN CASE
                        admins.add((Admin) newUser(true));
                        System.out.println("New admin created successfully!");
                        storingData(adminDb, admins);
                        break;

                    case 8: // Remove an admin, ADMIN CASE
                        deleteAdmin(admins);
                        break;

                    case 9: // Display all users, ADMIN CASE
                        usersList(users, books);
                        break;

                    case 10: // Display all admins, ADMIN CASE
                        adminsList(admins);
                        break;

                    case 11: // Add a new library, ADMIN CASE
                        locations.add(newLocation());
                        storingData(locationDb, locations);
                        break;

                    case 12: // Remove a library from the db, ADMIN CASE
                        deleteLocation(locations);
                        storingData(locationDb, locations);
                        break;

                    case 13: // List all libraries available
                        locationsList(locations);
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
            dbReader = users.listIterator();
            while (dbReader.hasNext()) {
                userAccount = (RegUser) dbReader.next();
                if (Main.userAccount.getName().equals(users.get(loginId).getName())) {
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
                        displayBooks(books);
                        break;

                    case 1: // search for a book, USER CASE
                        System.out.println("How do you want to search?");
                        System.out.println("1. By name");
                        System.out.println("2. By genre");
                        System.out.println("3. By both");
                        System.out.print("\nChoice: ");
                        searchChoice = getInput();
                        System.out.print("Enter your search query: ");
                        UserFunctions.searchQuery = sc.next();
                        searchBooks(books, searchChoice);
                        break;

                    case 2: // borrow a book, USER CASE
                        if(userAccount.canBorrow()) {
                            if (initBorrow(books)) {
                                if (execBorrowBook(userAccount, searchedBook)) {
                                    saveData = new ObjectOutputStream(new FileOutputStream(historyDb));
                                    saveData.writeObject(borrowedHistory);
                                    saveData.close();
                                    storingData(userDb, users);
                                    storingData(bookDb, books);
                                }
                            }
                            else {
                                System.out.println("Requested book doesn't exist in the system");
                            }
                        }
                        break;

                    case 3: // return a book, USER CASE
                        if(userAccount.returnCheck()) {
                            System.out.println(userAccount.getName() + " has no book borrowed at the moment");
                            break;
                        }
                        if (initReturn(books)) {
                            execReturnBook(userAccount, searchedBook);
                            saveData = new ObjectOutputStream(new FileOutputStream(historyDb));
                            saveData.writeObject(borrowedHistory);
                            saveData.close();
                            storingData(userDb, users);
                            storingData(bookDb, books);
                        }
                        break;

                    case 4: // view current status of books, USER CASE
                        System.out.println("Current user: " + Main.userAccount);
                        if (userAccount.getAccountType() == AccountType.USER)
                            getStatus(books);
                        else
                            userAccount.showBorrowedBooks(books);
                        break;

                    case 5: // find nearby Libraries, USER CASE
                        System.out.println("Libraries near you: ");
                        nearbyLibraries(locations);
                        break;

                    case 6: // change the user-password for login
                        changePassword();
                        storingData(userDb, users);
                        break;

                    case 7: // view the user's borrowing history
                        viewUserHistory();
                        break;

                    case 8:
                        Main.userAccount.upgradeAccount();
                        storingData(userDb, users);
                        System.out.println("You have successfully subscribed to premium ^^");
                        System.out.println("The following are your benefits: ");
                        System.out.println("1. Can borrow unto 3 books");
                        System.out.println("2. Can view all libraries irrespective of locations");
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
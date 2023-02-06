package com.lms;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

public class Admin extends Human implements Serializable, AdminFunctions {
    @Serial
    private static final long serialVersionUID = 191191191;
    private static int numOfAdmins = 0;
    // variables for functions
    private String searchQuery;
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
    Admin(String username, String password, cityList city, String email, String phNo) {
        super(username, password, city, email, phNo);
        setId(numOfAdmins+1);
        numOfAdmins++;
        setType(Main.AccountType.ADMIN);
    }
    // necessary functions
    @Override
    public String toString() {
        if (this.getUsername().equals("")) {
            return this.getPhNo() + " (" + this.getCity() + ")";
        }
        return this.getUsername() + " (" + this.getCity() + ")";
    }
    public static void setNumOfAdmins(int retrievedData) {
        numOfAdmins = retrievedData;
    }
    public static int getNumOfAdmins() {
        return Admin.numOfAdmins;
    }

    private void displayAvailability() {
        System.out.println(Main.searchedBook + " | Currently: " + Main.searchedBook.getStock() + " copies left");
    }

    // implements AdminFunctions
    // Managing libraries //
    // case 0: list all libraries
    // function to list all libraries
    public void librariesList(ArrayList<Library> libraries) {
        int dbIndex = 1;
        Main.dbReader = libraries.listIterator();
        System.out.println("List of all the managing libraries:");
        while (Main.dbReader.hasNext()) {
            Library library = (Library) Main.dbReader.next();
            System.out.println(dbIndex + ". " + library);
                dbIndex++;
        }
    }

    // case 1: add a new library
    // function to add new library location
    public boolean addLibrary (ArrayList<Library> libraries) {
        System.out.print("Enter the library name: ");
        Main.libName = Main.sc.nextLine();
        ListIterator libReader = libraries.listIterator();
        while (libReader.hasNext()) {
            Library lib = (Library) libReader.next();
            if (lib.getName().equalsIgnoreCase(Main.libName)) {
                System.out.println("Library already exists!");
                return true;
            }
        }
        return false;
    }

    // case 2: delete a library
    // function to delete a library location
    public void deleteLibrary (ArrayList<Library> libraries, ArrayList<User> users) {
        librariesList(libraries);
        System.out.print("\nChoose index of the library to be deleted: ");
        int index = Main.getInput();
        index--;
        if (index < libraries.size()) {
            Main.selectedLibrary = libraries.get(index);
            ListIterator bookReader = Main.selectedLibrary.getBooks().listIterator();
            while (bookReader.hasNext()) {
                Main.searchedBook = (Book) bookReader.next();
                returnBook(users);
            }
            libraries.remove(Main.selectedLibrary);
            System.out.println("Deleted successfully");
        } else {
            System.out.println("!-- Enter a valid input --!");
        }
    }

    // Managing books //
    // case 3: display all books
    // function to display all books
    public void displayBooks(@NotNull ArrayList<Library> libraries, ArrayList<User> users) {
        if (searchLibrary(libraries)) {
            System.out.println("\nBooks from: " + Main.selectedLibrary.getName());
            ListIterator bookReader = Main.selectedLibrary.getBooks().listIterator();
            while (bookReader.hasNext()) {
                Main.searchedBook = (Book) bookReader.next();
                displayAvailability();
            }
        }
    }

    // case 4: search books
    // functions to search books along with borrowers name
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
        Main.dbReader = libraries.listIterator();
        System.out.println("--- Searching by genre ---");
        while (Main.dbReader.hasNext()) {
            Main.searchFlag = false;
            Main.selectedLibrary = (Library) Main.dbReader.next();
            System.out.println("\nBooks from: " + Main.selectedLibrary.getName());
            ListIterator bookReader = Main.selectedLibrary.getBooks().listIterator();
            while (bookReader.hasNext()) {
                Main.searchedBook = (Book) bookReader.next();
                if(Main.searchedBook.getGenre().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                    displayAvailability();
                    Main.searchFlag = true;
                }
            }
            if (!Main.searchFlag)
                System.out.println("No results found!");
        }
    }
    private void searchByName(@NotNull ArrayList<Library> libraries) {
        Main.dbReader = libraries.listIterator();
        System.out.println("--- Searching by name ---");
        while (Main.dbReader.hasNext()) {
            Main.searchFlag = false;
            Main.selectedLibrary = (Library) Main.dbReader.next();
            System.out.println("\nBooks from: " + Main.selectedLibrary.getName());
            ListIterator bookReader = Main.selectedLibrary.getBooks().listIterator();
            while (bookReader.hasNext()) {
                Main.searchedBook = (Book) bookReader.next();
                if(Main.searchedBook.getName().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                    displayAvailability();
                    Main.searchFlag = true;
                }
            }
            if (!Main.searchFlag)
                System.out.println("No results found!");
        }
    }

    // case 5: add a new book
    // functions to add a new book
    public boolean searchLibrary(ArrayList<Library> libraries) {
        if (libraries.size() == 0) {
            System.out.println("---- No libraries found! ----");
            System.out.println("-- Add a library to manage --");
            return false;
        }
        librariesList(libraries);
        System.out.print("\nChoose index of the library to be managed: ");
        int index = Main.getInput();
        index--;
        if (index > libraries.size()) {
            System.out.println("!-- Enter a valid input --!");
            return false;
        }
        Main.selectedLibrary = libraries.get(index);
        return true;
    }
    public boolean checkBook() {
        System.out.print("Enter the book name: ");
        Main.bookName = Main.sc.nextLine();
        ListIterator booksReader = Main.selectedLibrary.getBooks().listIterator();
        while (booksReader.hasNext()) {
            Book book = (Book) booksReader.next();
            if (book.getName().equalsIgnoreCase(Main.bookName)) {
                System.out.println("Book already exists in the library!");
                return true;
            }
        }
        return false;
    }
    public Book newBook() {
        String author;
        Book.genreLists bookGenre;
        int stock, indexOfGenre = 1;
        System.out.println("Choose the book genre: ");
        for (Book.genreLists genre: Book.genreLists.values()) {
            System.out.print(indexOfGenre + ". " + genre + "\t\t");
            if(indexOfGenre % 6 == 0) {
                System.out.println();
            }
            indexOfGenre++;
        }
        System.out.print("\nChoice: ");
        indexOfGenre = Main.sc.nextInt();
        bookGenre = Book.genreLists.values()[indexOfGenre - 1];
        Main.sc.nextLine();
        System.out.print("Enter the author name: ");
        author = Main.sc.nextLine();
        System.out.print("Enter the number of copies: ");
        stock = Main.sc.nextInt();
        Main.sc.nextLine();
        return new Book(Main.bookName, bookGenre, author, stock, Main.selectedLibrary);
    }

    // case 6: delete a book
    // function to delete a book (also removes the user associated with the book)
    private void returnBook(ArrayList<User> users) {
        if (!Main.searchedBook.getBorrowedUser().isEmpty()) {
            ArrayList<Integer> borrowedUsers = Main.searchedBook.getBorrowedUser();
            ListIterator borrowers = borrowedUsers.listIterator();
            while (true) {
                try {
                    Main.userAccount = users.get(((Integer) borrowers.next()) - 1);
                    int index = Main.userAccount.getBorrowedBook().indexOf(Main.searchedBook);
                    Main.userAccount.getBorrowedBook().remove(index);
                } catch (Exception e) {
                    break;
                }
            }
        }
    }
    public void deleteBook(ArrayList<Library> libraries, ArrayList<User> users) {
        displayBooks(libraries, users);
        System.out.print("\nEnter the book to be deleted: ");
        searchQuery = Main.sc.next();
        Main.dbReader = Main.selectedLibrary.getBooks().listIterator();
        while (Main.dbReader.hasNext()) {
            Main.searchedBook = (Book) Main.dbReader.next();
            if (Main.searchedBook.getName().equalsIgnoreCase(searchQuery)) {
                if (!Main.searchedBook.getBorrowedUser().isEmpty()) {
                    returnBook(users);
                }
                Main.dbReader.remove();
                System.out.println("Deleted from " + Main.selectedLibrary + " successfully");
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("Requested book doesn't exist in the records");
    }

    // case 7: view borrowing history
    // function to view borrowing history of the library
    public void viewBorrowingHistory(ArrayList<String> history) {
        int dbIndex;
        System.out.println("Borrowing history:");
        Main.dbReader = history.listIterator();
        dbIndex = 1;
        while (Main.dbReader.hasNext()) {
            System.out.println(dbIndex + ". " + Main.dbReader.next());
            dbIndex++;
        }
        if (history.isEmpty()) {
            System.out.println("-- No records to display --");
        }
    }

    // case 8: update number of copies
    // function to change the number of copies left in the library
    public void updateCopies() {
        System.out.println("\nBooks from: " + Main.selectedLibrary.getName());
        ListIterator bookReader = Main.selectedLibrary.getBooks().listIterator();
        while (bookReader.hasNext()) {
            Main.searchedBook = (Book) bookReader.next();
            displayAvailability();
        }
        Main.searchFlag = false;
        System.out.print("\nEnter the book to be updated: ");
        searchQuery = Main.sc.next();
        Main.dbReader = Main.selectedLibrary.getBooks().listIterator();
        while (Main.dbReader.hasNext()) {
            Main.searchedBook = (Book) Main.dbReader.next();
            if (Main.searchedBook.getName().equalsIgnoreCase(searchQuery)) {
                System.out.println("\nUpdating: " + Main.searchedBook.getName());
                System.out.println("Current stock left: " + Main.searchedBook.getStock());
                System.out.print("\nEnter the updated stock: ");
                Main.searchedBook.setStock(Main.sc.nextInt());
                System.out.println("Updated successfully");
                Main.searchFlag = true;
                break;
            }
        }
        if (!Main.searchFlag)
            System.out.println("Book doesn't exist in the records");
    }

    // case 9: list all the users
    // function to list all available users
    public void usersList(ArrayList<User> users, ArrayList<Library> libraries) {
        int dbIndex = 1;
        String bookStat;
        Main.dbReader = users.listIterator();
        System.out.println("List of all the available users:");
        while (Main.dbReader.hasNext()) {
            Main.userAccount = (User) Main.dbReader.next();
            if(Main.userAccount.getBorrowedBookId() == -9999)
                bookStat = "Currently no borrowed books";
            else
            if(Main.userAccount.getType() == Main.AccountType.USER)
                bookStat = "Currently borrowed: " +
                        Main.userAccount.getBorrowedBook().get(0).getName();
            else {
                bookStat = "Currently borrowed: ";
                ListIterator bookReader = Main.userAccount.getBorrowedBook().listIterator();
                while (bookReader.hasNext()) {
                    Book book = (Book) bookReader.next();
                    bookStat = bookStat + book.getName() + " | ";
                }
            }
            System.out.println(dbIndex + ". " + Main.userAccount + " | " + bookStat);
            dbIndex++;
        }
    }

    // case 10: add a new user
    // parent class constructor

    // case 11: remove a user
    // function to delete a user (also releases the book if it was borrowed by the user)
    public void deleteUser(ArrayList<User> users, ArrayList<Library> libraries) {
        Main.searchFlag = false;
        usersList(users, libraries);
        System.out.print("\nEnter the user to be deleted: ");
        searchQuery = Main.sc.next();
        Main.dbReader = users.listIterator();
        while (Main.dbReader.hasNext()) {
            Main.userAccount = (User) Main.dbReader.next();
            if (Main.userAccount.getUsername().equalsIgnoreCase(searchQuery)
                    || Main.userAccount.getPhNo().equals(searchQuery)) {
                if (!Main.userAccount.hasNoBooks())
                    Main.userAccount.returnAll(libraries);
                Main.dbReader.remove();
                System.out.println("Deleted successfully");
                Main.searchFlag = true;
                break;
            }
        }
        if (!Main.searchFlag)
            System.out.println("User doesn't exist in the records");
    }

    // case 12: approve requests
    // function to read the requests AL and handle the requests
    public void approveRequests(ArrayList<User> users, ArrayList<String> requestList) {
        Main.dbReader = requestList.listIterator();
        while (Main.dbReader.hasNext()) {
            String line = (String) Main.dbReader.next();
            int i = line.indexOf('~');
            searchQuery = line.substring(i + 1, line.length());
            System.out.println("> " + line.substring(0, i));
        }
        if (!requestList.isEmpty()) {
            System.out.print("Choose the request to be managed: ");
            searchQuery = Main.sc.next();
            Main.searchFlag = false;
            Main.dbReader = requestList.listIterator();
            while (Main.dbReader.hasNext()) {
                String line = (String) Main.dbReader.next();
                int i = line.indexOf(':');
                String requestUser = line.substring(0, i);
                if (requestUser.equalsIgnoreCase(searchQuery)) {
                    int j = line.indexOf('~');
                    if (line.substring(j - 7, j - 1).equalsIgnoreCase("Denied")) {
                        Main.searchFlag = false;
                        System.out.println("Already denied the request!");
                        System.out.println("Remove request from the list? (Y/N)");
                        System.out.print("Choice: ");
                        String input = Main.sc.next();
                        if (input.equalsIgnoreCase("y")) {
                            requestList.remove(line);
                            System.out.println("Removed the request");
                        } else {
                            System.out.println("No actions performed");
                        }
                        break;
                    } else
                        Main.searchFlag = true;
                }
            }
            Main.dbReader = users.listIterator();
            while (Main.dbReader.hasNext()) {
                if (!Main.searchFlag)
                    break;
                Main.userAccount = (User) Main.dbReader.next();
                if (Main.userAccount.getUsername().equalsIgnoreCase(searchQuery)) {
                    Main.searchFlag = true;
                    String request = Main.userAccount.getUsername() + ": Premium account request opened ~" + Main.userAccount.getCity();
                    System.out.println("Chosen User Request: " + Main.userAccount);
                    System.out.println("1. Approve");
                    System.out.println("2. Deny");
                    System.out.print("Choice: ");
                    int choice = Main.sc.nextInt();
                    if (choice == 1) {
                        requestList.remove(request);
                        Main.userAccount.upgradeAccount();
                        System.out.println("Approved successfully");
                    } else if (choice == 2) {
                        requestList.remove(request);
                        requestList.add(Main.userAccount.getUsername() + ": Premium account request denied ~" + Main.userAccount.getCity());
                        System.out.println("Request has been denied");
                    }
                }
            }
            if (Main.searchFlag = false) {
                System.out.println("-- Invalid request selected! --");
            }
        }
        else {
            System.out.println("-- No requests at the moment ^^ --");
        }
    }

    // case 13: display all admins
    // function to list all available admins
    public void adminsList(ArrayList<Admin> admins) {
        int dbIndex = 1;
        Main.dbReader = admins.listIterator();
        System.out.println("List of all the available admins:");
        while (Main.dbReader.hasNext()) {
            System.out.println(dbIndex + ". " + Main.dbReader.next());
            dbIndex++;
        }
    }

    // case 14: add an admin
    // just the constructor of this class

    // case 15: delete an admin
    // function to delete an admin
    public void deleteAdmin(ArrayList<Admin> admins) {
        Main.searchFlag = false;
        System.out.print("Enter the admin to be deleted: ");
        searchQuery = Main.sc.next();
        Main.dbReader = admins.listIterator();
        while (Main.dbReader.hasNext()) {
            Admin deleteAdminAcc = (Admin) Main.dbReader.next();
            if (deleteAdminAcc.getUsername().equalsIgnoreCase(searchQuery)
                    || deleteAdminAcc.getPhNo().equals(searchQuery)) {
                Main.searchFlag = true;
                if (deleteAdminAcc.getUsername().equals(Main.adminAccount.getUsername())) {
                    System.out.println("You cannot delete your own account.");
                    break;
                }
                Main.dbReader.remove();
                System.out.println("Deleted successfully");
            }
        }
        if (!Main.searchFlag)
            System.out.println("Admin doesn't exist in the records");
    }
}
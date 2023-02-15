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
    Admin(String username, String password, cityList city, String email, String phNo) {
        super(username, password, city, email, phNo);
        setId(numOfAdmins+1);
        numOfAdmins++;
        setType(Main.AccountType.ADMIN);
    }
    // necessary functions
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
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
        System.out.println("List of all the managing libraries:");
        for (Library library : libraries) {
            System.out.println(dbIndex + ". " + library);
            dbIndex++;
        }
    }

    // case 1: add a new library
    // function to add new library location
    public boolean addLibrary (ArrayList<Library> libraries) {
        System.out.print("Enter the library name: ");
        Main.libName = Main.sc.nextLine();
        for (Library lib : libraries) {
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
        if (index <= libraries.size()) {
            Main.selectedLibrary = libraries.get(index - 1);
            for (Book book : Main.selectedLibrary.getBooks()) {
                Main.searchedBook = book;
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
            int index = 1;
            System.out.println("\nBooks from: " + Main.selectedLibrary.getName());
            for (Book book : Main.selectedLibrary.getBooks()) {
                Main.searchedBook = book;
                System.out.print(index + ". ");
                displayAvailability();
                index++;
            }
        }
    }

    // case 4: search books
    // functions to search books along with borrowers name
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
        for (Library library : libraries) {
            Main.searchFlag = false;
            Main.selectedLibrary = library;
            System.out.println("\nBooks from: " + Main.selectedLibrary.getName());
            for (Book book : Main.selectedLibrary.getBooks()) {
                Main.searchedBook = book;
                if (Main.searchedBook.getGenre().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                    displayAvailability();
                    Main.searchFlag = true;
                }
            }
            if (!Main.searchFlag)
                System.out.println("No results found!");
        }
    }
    private void searchByName(@NotNull ArrayList<Library> libraries) {
        System.out.println("--- Searching by name ---");
        for (Library library : libraries) {
            Main.searchFlag = false;
            Main.selectedLibrary = library;
            System.out.println("\nBooks from: " + Main.selectedLibrary.getName());
            for (Book book : Main.selectedLibrary.getBooks()) {
                Main.searchedBook = book;
                if (Main.searchedBook.getName().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
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
        if (index > libraries.size()) {
            System.out.println("!-- Enter a valid input --!");
            return false;
        }
        Main.selectedLibrary = libraries.get(index - 1);
        return true;
    }
    public boolean checkBook() {
        System.out.print("Enter the book name: ");
        Main.bookName = Main.sc.nextLine();
        for (Book book : Main.selectedLibrary.getBooks()) {
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
            ListIterator<Integer> borrowers = borrowedUsers.listIterator();
            while (true) {
                try {
                    Main.userAccount = users.get(borrowers.next() - 1);
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
        System.out.print("\nChoose the index of the book to be deleted: ");
        int index = Main.getInput();
        if (index != -9999 && index < Main.selectedLibrary.getBooks().size()) {
            Main.searchedBook = Main.selectedLibrary.getBooks().get(index - 1);
            System.out.println("Successfully deleted " + Main.searchedBook.getName() + " from " + Main.selectedLibrary.getName() + "!");
            Main.selectedLibrary.getBooks().remove(Main.searchedBook);
        }
        else
            System.out.println("!-- Enter a valid input --!");
    }

    // case 7: view borrowing history
    // function to view borrowing history of the library
    public void viewBorrowingHistory(ArrayList<String> history) {
        int dbIndex;
        System.out.println("Borrowing history:");
        dbIndex = 1;
        for (String line : history) {
            System.out.println(dbIndex + ". " + line);
            dbIndex++;
        }
        if (history.isEmpty()) {
            System.out.println("-- No records to display --");
        }
    }

    // case 8: update number of copies
    // function to change the number of copies left in the library
    public void updateCopies(ArrayList<Library> libraries, ArrayList<User> users) {
        displayBooks(libraries, users);
        System.out.print("\nChoose the index of the book to be updated: ");
        int index = Main.getInput();
        if (index != -9999 && index <= Main.selectedLibrary.getBooks().size()) {
            Main.searchedBook = Main.selectedLibrary.getBooks().get(index - 1);
            System.out.println("\nUpdating: " + Main.searchedBook.getName());
            System.out.println("Current stock left: " + Main.searchedBook.getStock());
            System.out.print("\nEnter the updated stock: ");
            Main.searchedBook.setStock(Main.sc.nextInt());
            System.out.println("Updated successfully");
        }
        else
            System.out.println("!-- Enter a valid input --!");
    }

    // case 9: list all the users
    // function to list all available users
    public void usersList(ArrayList<User> users, ArrayList<Library> libraries) {
        int dbIndex = 1;
        String bookStat;
        System.out.println("List of all the available users:");
        for (User user : users) {
            Main.userAccount = user;
            if (Main.userAccount.getBorrowedBookId() == -9999)
                bookStat = "Currently no borrowed books";
            else if (Main.userAccount.getType() == Main.AccountType.USER)
                bookStat = "Currently borrowed: " +
                        Main.userAccount.getBorrowedBook().get(0).getName();
            else {
                bookStat = "Currently borrowed: ";
                for (Book book : Main.userAccount.getBorrowedBook()) {
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
        System.out.print("\nChoose the index of the user to be deleted: ");
        int index = Main.getInput();
        if (index != -9999 && index <= users.size()) {
            Main.userAccount = users.get(index - 1);
            if (!Main.userAccount.hasNoBooks())
                Main.userAccount.returnAll();
            users.remove(Main.userAccount);
            System.out.println("Deleted successfully");
        }
        else
            System.out.println("!-- Enter a valid input --!");
    }

    // case 12: approve requests
    // function to read the requests AL and handle the requests
    public void approveRequests(ArrayList<User> users, ArrayList<String> requestList) {
        if (!requestList.isEmpty()) {
            int index = 1;
            for (String line : requestList) {
                System.out.println(index + ". " + line);
            }
            System.out.print("Choose the subscription request to be managed: ");
            index = Main.getInput();
            if (index != -9999 && index <= requestList.size()) {
                Main.searchFlag = false;
                String line = requestList.get(index - 1);
                int i = line.indexOf(':');
                searchQuery = line.substring(0, i);
                int j = line.indexOf('(');
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
                } else
                    Main.searchFlag = true;

                for (User user : users) {
                    if (!Main.searchFlag)
                        break;
                    Main.userAccount = user;
                    if (Main.userAccount.getUsername().equalsIgnoreCase(searchQuery)) {
                        Main.searchFlag = true;
                        System.out.println("Chosen User Request: " + Main.userAccount);
                        System.out.println("1. Approve");
                        System.out.println("2. Deny");
                        System.out.print("Choice: ");
                        int choice = Main.sc.nextInt();
                        if (choice == 1) {
                            requestList.remove(index - 1);
                            Main.userAccount.upgradeAccount();
                            System.out.println("Approved successfully");
                        } else if (choice == 2) {
                            requestList.remove(index - 1);
                            requestList.add(Main.userAccount.getUsername() + ": Premium account request denied ("
                                    + Main.userAccount.getCity() + ")");
                            System.out.println("Request has been denied");
                        }
                    }
                }
            }
            else {
                System.out.println("!-- Enter a valid input --!");
            }
        }
        else {
            System.out.println("-- No subscription requests at the moment ^^ --");
        }
    }

    // case 13: display all admins
    // function to list all available admins
    public void adminsList(ArrayList<Admin> admins) {
        int dbIndex = 1;
        System.out.println("List of all the available admins:");
        for (Admin admin : admins) {
            System.out.println(dbIndex + ". " + admin);
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
        ListIterator<Admin> dbReader = admins.listIterator();
        while (dbReader.hasNext()) {
            Admin deleteAdminAcc = dbReader.next();
            if (deleteAdminAcc.getUsername().equalsIgnoreCase(searchQuery)
                    || deleteAdminAcc.getPhNo().equals(searchQuery)) {
                Main.searchFlag = true;
                if (deleteAdminAcc.getUsername().equals(Main.adminAccount.getUsername())) {
                    System.out.println("You cannot delete your own account.");
                    break;
                }
                dbReader.remove();
                System.out.println("Deleted successfully");
            }
        }
        if (!Main.searchFlag)
            System.out.println("Admin doesn't exist in the records");
    }
}
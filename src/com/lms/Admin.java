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
        if (Main.searchedBook.availability()) {
            System.out.println(Main.searchedBook + " | Currently: all copies are in stock");
        } else {
            System.out.println(Main.searchedBook + " | Currently: " + Main.searchedBook.getStock() + " copies left");
        }
    }

    // implements AdminFunctions

    // case 0: view borrowing history
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

    // case 1: add a new book
    // function to add a new book
    public boolean searchLibrary(ArrayList<Library> libraries) {
        System.out.println("Choose the library to be managed: ");
        ListIterator libReader = libraries.listIterator();
        while (libReader.hasNext()) {
            Library lib = (Library) libReader.next();
            if (lib.getCity().equalsIgnoreCase(this.getCity())) {
                System.out.println("> " + lib.getName());
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag) {
            System.out.println("---- No libraries found! ----");
            System.out.println("-- Add a library to manage --");
            return false;
        }
        Main.searchFlag = false;
        System.out.print("Choice: ");
        Main.sc.nextLine();
        String libChoice = Main.sc.next();
        if (libChoice.equals("")) {
            System.out.println("!-- Enter a valid input --!");
            return false;
        }
        libReader = libraries.listIterator();
        while (libReader.hasNext()) {
            Library lib = (Library) libReader.next();
            if (lib.getName().equalsIgnoreCase(libChoice)
                    && lib.getCity().equalsIgnoreCase(Main.adminAccount.getCity())) {
                Main.selectedLibrary = lib;
                Main.searchFlag = true;
                break;
            }
        }
        if (!Main.searchFlag) {
            System.out.println("Well, that library doesn't exist.");
            return false;
        }
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

    // case 2: delete a book
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
                    Main.userAccount.getBorrowedFrom().remove(index);
                } catch (Exception e) {
                    break;
                }
            }
        }
    }
    public void deleteBook(ArrayList<User> users) {
        Main.searchFlag = false;
        System.out.print("Enter the book to be deleted: ");
        searchQuery = Main.sc.next();
        Main.dbReader = Main.selectedLibrary.getBooks().listIterator();
        while (Main.dbReader.hasNext()) {
            Main.searchedBook = (Book) Main.dbReader.next();
            if (Main.searchedBook.getName().equalsIgnoreCase(searchQuery)) {
                if (!Main.searchedBook.getBorrowedUser().isEmpty()) {
                    returnBook(users);
                }
                Main.dbReader.remove();
                System.out.println("Deleted from successfully");
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("Book doesn't exist in the records");
    }

    // case 3: display all books
    // function to display all books
    public void displayBooks(@NotNull ArrayList<Library> libraries, ArrayList<User> users) {
        Main.searchFlag = false;
        Main.dbReader = libraries.listIterator();
        while (Main.dbReader.hasNext()) {
            Main.selectedLibrary = (Library) Main.dbReader.next();
            if (Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                System.out.println("\nBooks from: " + Main.selectedLibrary.getName());
                Main.searchFlag = true;
                if (Main.selectedLibrary.getBooks().size() == 0) {
                    System.out.println("-- No books --");
                    continue;
                }
                ListIterator bookReader = Main.selectedLibrary.getBooks().listIterator();
                while(bookReader.hasNext()) {
                    Main.searchedBook = (Book) bookReader.next();
                    displayAvailability();
                }
            }
        }
        if (!Main.searchFlag) {
            System.out.println("---- No libraries found! ----");
            System.out.println("-- Add a library to manage --");
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
            if (Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                System.out.println("\nBooks from: " + Main.selectedLibrary.getName());
                ListIterator bookReader = Main.selectedLibrary.getBooks().listIterator();
                while (bookReader.hasNext()) {
                    Main.searchedBook = (Book) bookReader.next();
                    if(Main.searchedBook.getGenre().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                        displayAvailability();
                        Main.searchFlag = true;
                    }
                }
            }
            if (!Main.searchFlag && Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity()))
                System.out.println("No results found!");
        }
    }
    private void searchByName(@NotNull ArrayList<Library> libraries) {
        Main.dbReader = libraries.listIterator();
        System.out.println("--- Searching by name ---");
        while (Main.dbReader.hasNext()) {
            Main.searchFlag = false;
            Main.selectedLibrary = (Library) Main.dbReader.next();
            if (Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity())) {
                System.out.println("\nBooks from: " + Main.selectedLibrary.getName());
                ListIterator bookReader = Main.selectedLibrary.getBooks().listIterator();
                while (bookReader.hasNext()) {
                    Main.searchedBook = (Book) bookReader.next();
                    if(Main.searchedBook.getName().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                        displayAvailability();
                        Main.searchFlag = true;
                    }
                }
            }
            if (!Main.searchFlag && Main.selectedLibrary.getCity().equalsIgnoreCase(this.getCity()))
                System.out.println("No results found!");
        }
    }

    // case 5: add a new user
    // parent class has the function

    // case 6: delete a user
    // function to delete a user (also releases the book if it was borrowed by the user)
    public void deleteUser(ArrayList<User> users, ArrayList<Library> libraries) {
        Main.searchFlag = false;
        System.out.print("Enter the user to be deleted: ");
        searchQuery = Main.sc.next();
        Main.dbReader = users.listIterator();
        while (Main.dbReader.hasNext()) {
            Main.userAccount = (User) Main.dbReader.next();
            if (Main.userAccount.getUsername().equalsIgnoreCase(searchQuery)
                    && Main.userAccount.getCity().equalsIgnoreCase(Main.adminAccount.getCity())) {
                if (!Main.userAccount.hasNoBooks())
                    Main.userAccount.returnAll(libraries);
                Main.dbReader.remove();
                System.out.println("Deleted successfully");
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("User doesn't exist in the records");
    }

    // case 8: delete an admin
    // function to delete an admin
    public void deleteAdmin(ArrayList<Admin> admins) {
        Main.searchFlag = false;
        System.out.print("Enter the admin to be deleted: ");
        searchQuery = Main.sc.next();
        Main.dbReader = admins.listIterator();
        while (Main.dbReader.hasNext()) {
            Admin deleteAdminAcc = (Admin) Main.dbReader.next();
            if (deleteAdminAcc.getUsername().equalsIgnoreCase(searchQuery)) {
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

    // case 10: display all admins
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

    // case 11: add a new library
    // function to add new library location
    public Library newLocation() {
        String name;
        System.out.print("Enter the Library Name: ");
        name = Main.sc.next();
        return new Library(name, this.getCity());
    }

    // case 12: delete a library
    // function to delete a library location
    public void deleteLocation(ArrayList<Library> locations, ArrayList<User> users) {
        Main.searchFlag = false;
        System.out.print("Enter the library to be deleted: ");
        Main.sc.nextLine();
        searchQuery = Main.sc.nextLine();
        Main.dbReader = locations.listIterator();
        while (Main.dbReader.hasNext()) {
            Main.selectedLibrary = (Library) Main.dbReader.next();
            if (Main.selectedLibrary.getName().equalsIgnoreCase(searchQuery)) {
                ListIterator bookReader = Main.selectedLibrary.getBooks().listIterator();
                while (bookReader.hasNext()) {
                    Main.searchedBook = (Book) bookReader.next();
                    returnBook(users);
                }
                Main.dbReader.remove();
                System.out.println("Deleted successfully");
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("Given library doesn't exist in the records");
    }

    // case 13: list all libraries
    // function to list all libraries
    public void librariesList(ArrayList<Library> libraries) {
        int dbIndex = 1;
        Main.dbReader = libraries.listIterator();
        System.out.println("List of all the available libraries:");
        while (Main.dbReader.hasNext()) {
            Library library = (Library) Main.dbReader.next();
            if (library.getCity().equalsIgnoreCase(this.getCity())) {
                System.out.println(dbIndex + ". " + Main.dbReader.next());
                dbIndex++;
            }
        }
    }
}
package com.lms;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

import static com.lms.Main.*;

public class Admin extends User implements Serializable, AdminFunctions {
    @Serial
    private static final long serialVersionUID = 191191191;
    private static int numOfAdmins = 0;
    // variables for functions
    private String searchQuery;
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
    Admin(String username, String password, String city) {
        super(username, password, city);
        setId(numOfAdmins+1);
        numOfAdmins++;
        setType(AccountType.ADMIN);
    }
    // necessary functions
    @Override
    public String toString() {
        return this.getUsername();
    }
    public static void setNumOfAdmins(int retrievedData) {
        numOfAdmins = retrievedData;
    }

    private void displayAvailability() {
        if (Main.searchedBook.availability()) {
            System.out.println(Main.searchedBook + " | Currently: all copies are in stock");
        } else {
            System.out.println(Main.searchedBook + " | Currently: " + searchedBook.getStock() + " copies left");
        }
    }

    // implements AdminFunctions

    // case 0: view borrowing history
    // function to view borrowing history of the library
    public void viewBorrowingHistory(ArrayList<String> history) {
        int dbIndex;
        System.out.println("Borrowing history:");
        dbReader = history.listIterator();
        dbIndex = 1;
        while (dbReader.hasNext()) {
            System.out.println(dbIndex + ". " + dbReader.next());
            dbIndex++;
        }
        if (history.isEmpty()) {
            System.out.println("-- No records to display --");
        }
    }

    // case 1: add a new book
    // function to add a new book
    public Book newBook(Library selectedLibrary) {
        String bookName, author;
        Book.genreLists bookGenre;
        int stock, indexOfGenre = 1;
        System.out.print("Enter the book name: ");
        bookName = sc.nextLine();
        System.out.println("Choose the book genre: ");
        for (Book.genreLists genre: Book.genreLists.values()) {
            System.out.print(indexOfGenre + ". " + genre + "\t\t");
            if(indexOfGenre % 6 == 0) {
                System.out.println();
            }
            indexOfGenre++;
        }
        System.out.print("\nChoice: ");
        indexOfGenre = sc.nextInt();
        bookGenre = Book.genreLists.values()[indexOfGenre - 1];
        sc.nextLine();
        System.out.print("Enter the author name: ");
        author = sc.nextLine();
        System.out.print("Enter the number of copies: ");
        stock = sc.nextInt();
        sc.nextLine();
        return new Book(bookName, bookGenre, author, stock, selectedLibrary);
    }

    // case 2: delete a book
    // function to delete a book (also removes the user associated with the book)
    private void returnBook(ArrayList<RegUser> users) {
        if (!searchedBook.getBorrowedUser().isEmpty()) {
            ArrayList<Integer> borrowedUsers = searchedBook.getBorrowedUser();
            ListIterator borrowers = borrowedUsers.listIterator();
            while (true) {
                try {
                    userAccount = users.get(((Integer) borrowers.next()) - 1);
                    int index = userAccount.getBorrowedBook().indexOf(searchedBook);
                    userAccount.getBorrowedBook().remove(index);
                    userAccount.getBorrowedFrom().remove(index);
                } catch (Exception e) {
                    break;
                }
            }
        }
    }
    public void deleteBook(ArrayList<RegUser> users) {
        searchFlag = false;
        System.out.print("Enter the book to be deleted: ");
        searchQuery = sc.next();
        dbReader = selectedLibrary.getBooks().listIterator();
        while (dbReader.hasNext()) {
            searchedBook = (Book) dbReader.next();
            if (searchedBook.getName().equalsIgnoreCase(searchQuery)) {
                if (!searchedBook.getBorrowedUser().isEmpty()) {
                    returnBook(users);
                }
                dbReader.remove();
                System.out.println("Deleted successfully");
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("Book doesn't exist in the records");
    }

    // case 3: display all books
    // function to display all books
    public void displayBooks(@NotNull ArrayList<Library> libraries, ArrayList<RegUser> users) {
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

    // case 5: add a new user
    // parent class has the function

    // case 6: delete a user
    // function to delete a user (also releases the book if it was borrowed by the user)
    public void deleteUser(ArrayList<RegUser> users, ArrayList<Library> libraries) {
        Main.searchFlag = false;
        System.out.print("Enter the user to be deleted: ");
        searchQuery = sc.next();
        dbReader = users.listIterator();
        while (dbReader.hasNext()) {
            userAccount = (RegUser) dbReader.next();
            if (userAccount.getUsername().equalsIgnoreCase(searchQuery)
                    && userAccount.getCity().equalsIgnoreCase(adminAccount.getCity())) {
                if (!userAccount.hasNoBooks())
                    userAccount.returnAll(libraries);
                dbReader.remove();
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
        searchQuery = sc.next();
        dbReader = admins.listIterator();
        while (dbReader.hasNext()) {
            Admin deleteAdminAcc = (Admin) dbReader.next();
            if (deleteAdminAcc.getUsername().equalsIgnoreCase(searchQuery)) {
                Main.searchFlag = true;
                if (deleteAdminAcc.getUsername().equals(adminAccount.getUsername())) {
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

    // case 9: list all the users
    // function to list all available users
    public void usersList(ArrayList<RegUser> users, ArrayList<Library> libraries) {
        int dbIndex = 1;
        String bookStat;
        dbReader = users.listIterator();
        System.out.println("List of all the available users:");
        while (dbReader.hasNext()) {
            Main.userAccount = (RegUser) dbReader.next();
            if(Main.userAccount.getBorrowedBookId() == -9999)
                bookStat = "Currently no borrowed books";
            else
            if(userAccount.getType() == Main.AccountType.USER)
                bookStat = "Currently borrowed: " +
                        userAccount.getBorrowedBook().get(0).getName();
            else {
                bookStat = "Currently borrowed: ";
                ListIterator bookReader = userAccount.getBorrowedBook().listIterator();
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
        dbReader = admins.listIterator();
        System.out.println("List of all the available admins:");
        while (dbReader.hasNext()) {
            System.out.println(dbIndex + ". " + dbReader.next());
            dbIndex++;
        }
    }

    // case 11: add a new library
    // function to add new library location
    public Library newLocation() {
        String name;
        System.out.print("Enter the Library Name: ");
        name = sc.next();
        return new Library(name, this.getCity());
    }

    // case 12: delete a library
    // function to delete a library location
    public void deleteLocation(ArrayList<Library> locations, ArrayList<RegUser> users) {
        searchFlag = false;
        System.out.print("Enter the library to be deleted: ");
        sc.nextLine();
        searchQuery = sc.nextLine();
        dbReader = locations.listIterator();
        while (dbReader.hasNext()) {
            selectedLibrary = (Library) dbReader.next();
            if (selectedLibrary.getName().equalsIgnoreCase(searchQuery)) {
                ListIterator bookReader = selectedLibrary.getBooks().listIterator();
                while (bookReader.hasNext()) {
                    searchedBook = (Book) bookReader.next();
                    returnBook(users);
                }
                dbReader.remove();
                System.out.println("Deleted successfully");
                searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("Given library doesn't exist in the records");
    }

    // case 13: list all libraries
    // function to list all libraries
    public void librariesList(ArrayList<Library> locations) {
        int dbIndex = 1;
        dbReader = locations.listIterator();
        System.out.println("List of all the available libraries:");
        while (dbReader.hasNext()) {
            System.out.println(dbIndex + ". " + dbReader.next());
            dbIndex++;
        }
    }
}
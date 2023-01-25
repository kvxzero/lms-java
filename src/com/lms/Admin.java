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

    // static function to log in
    public static boolean validateLogin(RegUser user, String password) {
        return user.getPassword().equals(password);
    }

    // implements AdminFunctions

    // case 0: view borrowing history
    // function to view borrowing history of the library
    public void viewBorrowingHistory() {
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

    // case 1: add a new book
    // function to add a new book
    public Book newBook() {
        String bookName, author;
        Book.genreLists bookGenre;
        int stock, indexOfGenre = 1;
        System.out.print("Enter the book name: ");
        bookName = sc.nextLine();
        System.out.println("Choose the book genre: ");
        for (Book.genreLists genre: Book.genreLists.values()) {
            System.out.println(indexOfGenre + ". " + genre);
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
        return new Book(bookName, bookGenre, author, stock);
    }

    // case 2: delete a book
    // function to delete a book (also removes the user associated with the book)
    public void deleteBook(ArrayList<RegUser> users, ArrayList<Book> books) {
        Main.searchFlag = false;
        System.out.print("Enter the book to be deleted: ");
        searchQuery = sc.next();
        dbReader = books.listIterator();
        while (dbReader.hasNext()) {
            Main.searchedBook = (Book) dbReader.next();
            if (Main.searchedBook.getName().equalsIgnoreCase(searchQuery)) {
                if(!Main.searchedBook.getBorrowedUser().isEmpty()) {
                    ArrayList<Integer> borrowedUsers = searchedBook.getBorrowedUser();
                    while(borrowedUsers.listIterator().hasNext()) {
                        RegUser user = users.get(borrowedUsers.listIterator().next());
                        user.getBorrowedBook().remove((Object) searchedBook.getId());
                    }
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
    public void displayBooks(@NotNull ArrayList<Book> books, ArrayList<RegUser> users) {
        dbReader = books.listIterator();
        while (dbReader.hasNext()) {
            Main.searchedBook = (Book) dbReader.next();
            if (Main.searchedBook.availability()) {
                System.out.println(Main.searchedBook + " | Currently: all copies are in stock");
            } else {
                System.out.println(Main.searchedBook + " | Currently: " + searchedBook.getStock() + " copies left");
            }
        }
    }

    // case 4: search books
    // functions to search books along with borrowers name
    public void searchBooks(@NotNull ArrayList<Book> books, ArrayList<RegUser> users, int option) {
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
    private void searchByGenre(ArrayList<Book> books, ArrayList<RegUser> users) {
        Main.searchFlag = false;
        dbReader = books.listIterator();
        System.out.println("--- Searching by genre ---");
        while (dbReader.hasNext()) {
            Main.searchedBook = (Book) dbReader.next();
            if (Main.searchedBook.getGenre().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                if (Main.searchedBook.availability()) {
                    System.out.println(Main.searchedBook + " | Currently: all copies are in stock");
                } else {
                    System.out.println(Main.searchedBook + " | Currently: " + searchedBook.getStock() + "copies left");
                }
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("No results found!");
    }
    private void searchByName(@NotNull ArrayList<Book> books, ArrayList<RegUser> users) {
        Main.searchFlag = false;
        dbReader = books.listIterator();
        System.out.println("--- Searching by name ---");
        while (dbReader.hasNext()) {
            Main.searchedBook = (Book) dbReader.next();
            if (Main.searchedBook.getName().toLowerCase().indexOf(searchQuery.toLowerCase()) == 0) {
                if (Main.searchedBook.availability()) {
                    System.out.println(Main.searchedBook + " | Currently: all copies are in stock");
                } else {
                    System.out.println(Main.searchedBook + " | Currently: " + searchedBook.getStock() + "copies left");
                }
                Main.searchFlag = true;
            }
        }
        if (!Main.searchFlag)
            System.out.println("No results found!");
    }

    // case 5: add a new user
    // it is in parent

    // case 6: delete a user
    // function to delete a user (also releases the book if it was borrowed by the user)
    public void deleteUser(ArrayList<RegUser> users, ArrayList<Book> books) {
        Main.searchFlag = false;
        System.out.print("Enter the user to be deleted: ");
        searchQuery = sc.next();
        dbReader = users.listIterator();
        while (dbReader.hasNext()) {
            userAccount = (RegUser) dbReader.next();
            if (userAccount.getUsername().equalsIgnoreCase(searchQuery)) {
                ListIterator booksReader = userAccount.getBorrowedBook().listIterator();
                while(booksReader.hasNext()) {
                    Book book = books.get((Integer) booksReader.next() - 1);
                    book.bookReturned(userAccount);
                }
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
    public void usersList(ArrayList<RegUser> users, ArrayList<Book> books) {
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
                bookStat = "Currently borrowed: " + books.get(Main.userAccount.getBorrowedBookId()-1).getName();
            else {
                bookStat = "Currently borrowed: ";
                while (userAccount.getBorrowedBook().listIterator().hasNext()) {
                    Book book = books.get(userAccount.getBorrowedBook().listIterator().next());
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
        String name, city;
        System.out.print("Enter the Library Name: ");
        name = sc.next();
        System.out.print("Enter the Library City: ");
        city = sc.next();
        return new Library(name, city);
    }

    // case 12: delete a library
    // function to delete a library location
    public void deleteLocation(ArrayList<Library> locations) {
        searchFlag = false;
        System.out.print("Enter the library to be deleted: ");
        sc.nextLine();
        searchQuery = sc.nextLine();
        dbReader = locations.listIterator();
        while (dbReader.hasNext()) {
            selectedLibrary = (Library) dbReader.next();
            if (selectedLibrary.getName().equals(searchQuery)) {
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
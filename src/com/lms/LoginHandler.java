package com.lms;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginHandler {
    // vars for login handling
    private final ArrayList<RegUser> users;
    private final ArrayList<Admin> admins;
    // var for iterating the arraylist
    private static ListIterator dbReader;
    // constructor for the object
    LoginHandler(ArrayList users, ArrayList admins) {
        this.users = users;
        this.admins = admins;
    }

    // validation functions
    public boolean validatePhoneNumber(ArrayList users) {
        System.out.print("Enter your phone number: ");
        Main.sc.nextLine();
        Main.userPhNo = Main.sc.next();
        Pattern numberPattern = Pattern.compile("^\\d{10}$");
        Matcher matcher = numberPattern.matcher(String.valueOf(Main.userPhNo));
        if (!matcher.matches()) {
            System.out.println("Invalid phone number!");
            return false;
        }
        dbReader = users.listIterator();
        while(dbReader.hasNext()) {
            if (((User) dbReader.next()).getPhNo().equals(Main.userPhNo)) {
                System.out.println("Phone number already exists!");
                return false;
            }
        }
        return true;
    }
    public boolean validateEmail(ArrayList users) {
        System.out.print("Enter your email address: ");
        Main.userEmail = Main.sc.next();
        if (Main.userEmail.equals("") | Main.userEmail.equals("\n")) {
            System.out.println("Invalid email!");
            return false;
        }
        dbReader = users.listIterator();
        while(dbReader.hasNext()) {
            if (((User) dbReader.next()).getEmail().equals(Main.userEmail)) {
                System.out.println("Email already exists!");
                return false;
            }
        }
        return true;
    }
    private static boolean validateUsername(ArrayList users) {
        System.out.print("Enter your username: ");
        Main.userName = Main.sc.next();
        if (Main.userName.equals("") | Main.userName.equals("\n")) {
            System.out.println("Invalid username!");
            return false;
        }
        dbReader = users.listIterator();
        while(dbReader.hasNext()) {
            String username = ((User) dbReader.next()).getUsername();
            if (username.equals(Main.userName)) {
                System.out.println("Username already exists!");
                return false;
            }
        }
        return true;
    }
    private static boolean validatePassword() {
        System.out.print("Enter your password: ");
        Main.userPassword = Main.sc.next();
        if(Main.userPassword.length() < 5) {
            System.out.println("Password must be at least 5 characters long!");
            return false;
        }
        return true;
    }
    private static boolean validateCity() {
        System.out.print("Enter your location: ");
        Main.userLocation = Main.sc.next();
        if(Main.userLocation.equals("") | Main.userLocation.equals("\n")) {
            System.out.println("Location can not be blank!");
            return false;
        }
        return true;
    }
    public boolean validateInformation(ArrayList users, boolean signUp) {
        if (!validatePhoneNumber(users)) {
            return false;
        }
        if (!validateEmail(users)) {
            return false;
        }
        if (!validateUsername(users)) {
            return false;
        }
        if (!validatePassword()) {
            return false;
        }
        if (signUp) {
            if (!validateCity()) {
                return false;
            }
        }
        return true;
    }
    public boolean inviteUser(ArrayList users) {
        if (!validatePhoneNumber(users)) {
            return false;
        }
        if (!validateEmail(users)) {
            return false;
        }
        return true;
    }

    // function to handle login auth
    public int userLoginRequest(String username, String password, Main.AccountType accountType) {
        User user;
        if(accountType == Main.AccountType.ADMIN)
            dbReader = admins.listIterator();
        else // accountType == Main.AccountType.USER or Main.AccountType.PRO is true
            dbReader = users.listIterator();
        while (dbReader.hasNext()) {
            user = (User) dbReader.next();
            if (user.getUsername().equals(username)) {
                if(User.validateLogin(user, password))
                    return 200;
                else {
                    System.out.println("Invalid login credentials");
                    return 401;
                }
            }
        }
        System.out.println("Provided username doesn't exist");
        return 401;
    }

    // function to log in with phone number
    public boolean phoneLoginRequest(String phNo, String password, Main.AccountType accountType) {
        User user;
        if(accountType == Main.AccountType.ADMIN)
            dbReader = admins.listIterator();
        else // accountType == Main.AccountType.USER or Main.AccountType.PRO is true
            dbReader = users.listIterator();
        while (dbReader.hasNext()) {
            user = (User) dbReader.next();
            if (user.getPhNo().equals(phNo)) {
                if(User.validateLogin(user, password))
                    return true;
                else {
                    System.out.println("Invalid login credentials");
                    return false;
                }
            }
        }
        System.out.println("Provided phone number doesn't exist");
        return false;
    }

    // function to initiate login
    public boolean initiateLogin(Main.AccountType accountType) {
        System.out.println("-- Login via --");
        System.out.println("1. Username");
        System.out.println("2. Phone Number");
        System.out.print("\nChoice: ");
        int loginChoice = Main.getInput();
        switch (loginChoice) {
            case 1:
                System.out.print("Enter your username: ");
                Main.userName = Main.sc.next();
                System.out.print("Enter your password: ");
                Main.userPassword = Main.sc.next();
                if (this.userLoginRequest(Main.userName, Main.userPassword, accountType) == 200) {
                    Main.loginId = this.getLoginID(Main.userName, accountType);
                    return true;
                }
                break;

            case 2:
                boolean flag;
                System.out.print("Enter your phone number: ");
                Main.userPhNo = Main.sc.next();
                if(this.initialLoginCheck(Main.userPhNo, accountType)) {
                    System.out.println("Welcome! You are logging in for the first time.");
                    System.out.println("-- Fill in the following --");
                    if (accountType == Main.AccountType.ADMIN) {
                        flag = validateUsername(admins);
                    } else {
                        flag = validateUsername(users);
                    }
                    if (flag && validatePassword()) {
                        if(accountType == Main.AccountType.ADMIN)
                            dbReader = admins.listIterator();
                        else // accountType == Main.AccountType.USER or Main.AccountType.PRO is true
                            dbReader = users.listIterator();
                        while (dbReader.hasNext()){
                            Main.loginAccount = (User) dbReader.next();
                            if (Main.loginAccount.getPhNo().equals(Main.userPhNo)) {
                                Main.loginAccount.setUsername(Main.userName);
                                Main.loginAccount.setPassword(Main.userPassword);
                                System.out.println(Main.loginAccount);
                                System.out.println("Initial login was successful!");
                                if (userLoginRequest(Main.userName, Main.userPassword, accountType) == 200) {
                                    Main.loginId = this.getLoginID(Main.userName, accountType);
                                    return true;
                                }
                            }
                        }
                    }
                }
                else {
                    System.out.print("Enter your password: ");
                    Main.userPassword = Main.sc.next();
                    if (this.phoneLoginRequest(Main.userPhNo, Main.userPassword, accountType)) {
                        Main.loginId = this.getLoginID(Main.userName, accountType);
                        return true;
                    }
                }
                break;

            default:
                System.out.println("!-- Enter a valid input --!");
        }
        return false;
    }

    public boolean initialLoginCheck(String phNo, Main.AccountType accountType) {
        User user;
        if(accountType == Main.AccountType.ADMIN)
            dbReader = admins.listIterator();
        else // accountType == Main.AccountType.USER or Main.AccountType.PRO is true
            dbReader = users.listIterator();
        while (dbReader.hasNext()) {
            user = (User) dbReader.next();
            if (user.getPhNo().equals(phNo)) {
                if (user.getPassword() == null && user.getUsername() == "") {
                    return true;
                }
            }
        }
        return false;
    }
    // function to get the user id after auth
    public int getLoginID(String username, Main.AccountType accountType) {
        if(accountType == Main.AccountType.ADMIN)
            dbReader = admins.listIterator();
        else // accountType == Main.AccountType.USER is true
            dbReader = users.listIterator();
        User user;
        while(dbReader.hasNext()) {
            user = (User) dbReader.next();
            if (user.getUsername().equals(username)) {
                return user.getId()-1;
            }
        }
        return 0;
    }

}
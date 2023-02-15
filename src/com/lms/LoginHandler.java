package com.lms;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginHandler {
    // vars for login handling
    private final ArrayList<User> users;
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
            System.out.println("Invalid phone number! (10 digits)");
            return true;
        }
        dbReader = users.listIterator();
        while(dbReader.hasNext()) {
            if (((Human) dbReader.next()).getPhNo().equals(Main.userPhNo)) {
                System.out.println("Phone number already exists!");
                return true;
            }
        }
        return false;
    }
    public boolean validateEmail(ArrayList users) {
        System.out.print("Enter your email address: ");
        Main.userEmail = Main.sc.next();
        if (Main.userEmail.equals("") | Main.userEmail.equals("\n")) {
            System.out.println("Invalid email address!");
            return true;
        }
        Pattern emailPattern = Pattern.compile("^[\\w+&&[^_]](\\w+)@([a-z]+)(\\.([a-z]+)){1,2}$");
        Matcher matcher = emailPattern.matcher(String.valueOf(Main.userEmail));
        if (!matcher.matches()) {
            System.out.println("Invalid email address!");
            return true;
        }
        dbReader = users.listIterator();
        while(dbReader.hasNext()) {
            if (((Human) dbReader.next()).getEmail().equals(Main.userEmail)) {
                System.out.println("Email already exists!");
                return true;
            }
        }
        return false;
    }
    private boolean validateUsername(ArrayList users) {
        System.out.print("Enter your username: ");
        Main.userName = Main.sc.next();
        if (Main.userName.equals("") | Main.userName.equals("\n")) {
            System.out.println("Invalid username!");
            return false;
        }
        dbReader = users.listIterator();
        while(dbReader.hasNext()) {
            String username = ((Human) dbReader.next()).getUsername();
            if (username.equals(Main.userName)) {
                System.out.println("Username already exists!");
                return false;
            }
        }
        return true;
    }
    private boolean validatePassword() {
        System.out.print("Enter your password: ");
        Main.userPassword = Main.sc.next();
        if(Main.userPassword.length() < 5) {
            System.out.println("Password must be at least 5 characters long!");
            return false;
        }
        return true;
    }
    public boolean validateCity() {
        int indexOfCity = 1;
        System.out.println("Select your location: ");
        for (Human.cityList city: Human.cityList.values()) {
            System.out.println(indexOfCity + ". " + city);
            indexOfCity++;
        }
        System.out.print("\nChoice: ");
        indexOfCity = Main.getInput();
        if (indexOfCity == -9999) {
            return false;
        }
        Main.userLocation = Human.cityList.values() [indexOfCity - 1];
        return true;
    }
    private boolean validateType() {
        System.out.println("Select your account type: ");
        System.out.println("1. Regular User");
        System.out.println("2. Premium User");
        System.out.print("\nChoice: ");
        int index = Main.getInput();
        if (index == -9999) {
            System.out.println("Enter a valid input!");
            return true;
        } else if (index == 1)
            Main.userType = Main.AccountType.USER;
        else if (index == 2)
            Main.userType = Main.AccountType.PRO;
        return false;
     }
    public boolean validateInformation(ArrayList users) {
        if (validatePhoneNumber(users)) {
            return false;
        }
        if (validateEmail(users)) {
            return false;
        }
        if (!validateUsername(users)) {
            return false;
        }
        if (!validatePassword()) {
            return false;
        }
        if (validateType()) {
            return false;
        }
        if (!validateCity()) {
            return false;
        }
        return true;
    }
    public boolean inviteUser(ArrayList users, boolean userFlag) {
        if (validatePhoneNumber(users)) {
            return false;
        }
        if (validateEmail(users)) {
            return false;
        }
        if (!validateCity()) {
            return false;
        }
        if (userFlag) {
            if (validateType()) {
                return false;
            }
        }
        return true;
    }

    // function to handle login auth
    public boolean userLoginRequest(String username, String password, Main.AccountType accountType) {
        Human human;
        if(accountType == Main.AccountType.ADMIN)
            dbReader = admins.listIterator();
        else // accountType == Main.AccountType.USER or Main.AccountType.PRO is true
            dbReader = users.listIterator();
        while (dbReader.hasNext()) {
            human = (Human) dbReader.next();
            if (human.getUsername().equals(username)) {
                if(Human.validateLogin(human, password))
                    return true;
                else {
                    System.out.println("Invalid login credentials");
                    return false;
                }
            }
        }
        System.out.println("Provided username doesn't exist");
        return false;
    }

    // function to log in with phone number
    public boolean phoneLoginRequest(String phNo, String password, Main.AccountType accountType) {
        Human human;
        if(accountType == Main.AccountType.ADMIN)
            dbReader = admins.listIterator();
        else // accountType == Main.AccountType.USER or Main.AccountType.PRO is true
            dbReader = users.listIterator();
        while (dbReader.hasNext()) {
            human = (Human) dbReader.next();
            if (human.getPhNo().equals(phNo)) {
                if(Human.validateLogin(human, password))
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
                if (this.userLoginRequest(Main.userName, Main.userPassword, accountType)) {
                    Main.loginId = this.getLoginID(Main.userName, accountType);
                    return true;
                }
                break;

            case 2:
                boolean flag;
                System.out.print("Enter your phone number: ");
                Main.userPhNo = Main.sc.next();
                if(this.initialLoginCheck(Main.userPhNo, accountType)) {
                    System.out.println("\nWelcome! You are logging in for the first time.");
                    System.out.println("-- Fill in the following --\n");
                    if (accountType == Main.AccountType.ADMIN) {
                        flag = validateUsername(admins);
                    } else {
                        flag = validateUsername(users);
                    }
                    if (flag && validatePassword()) {
                        Human loginAccount;
                        if(accountType == Main.AccountType.ADMIN)
                            dbReader = admins.listIterator();
                        else // accountType == Main.AccountType.USER or Main.AccountType.PRO is true
                            dbReader = users.listIterator();
                        while (dbReader.hasNext()){
                            loginAccount = (Human) dbReader.next();
                            if (loginAccount.getPhNo().equals(Main.userPhNo)) {
                                loginAccount.setUsername(Main.userName);
                                loginAccount.setPassword(Main.userPassword);
                                System.out.println();
                                System.out.println(loginAccount);
                                System.out.println("\nInitial login was successful!");
                                if (userLoginRequest(Main.userName, Main.userPassword, accountType)) {
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
        Human human;
        if(accountType == Main.AccountType.ADMIN)
            dbReader = admins.listIterator();
        else // accountType == Main.AccountType.USER or Main.AccountType.PRO is true
            dbReader = users.listIterator();
        while (dbReader.hasNext()) {
            human = (Human) dbReader.next();
            if (human.getPhNo().equals(phNo)) {
                if (human.getPassword() == null && human.getUsername().equals("")) {
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
        Human human;
        while(dbReader.hasNext()) {
            human = (Human) dbReader.next();
            if (human.getUsername().equals(username)) {
                return dbReader.nextIndex() - 1;
            }
        }
        return 0;
    }

}
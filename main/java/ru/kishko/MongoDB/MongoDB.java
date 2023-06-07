package ru.kishko.MongoDB;

public class MongoDB {

    public static String collectionString = "testCollection";
    public static String usersCollectionString = "users";
    public static String database = "test";

    public static void main(String[] args) {

        new LoginWindow(collectionString);

    }

}

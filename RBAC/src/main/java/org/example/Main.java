package org.example;

public class Main {
    public static void main(String[] args) {
        Permission p1 = new Permission("read", "users", "Can read user data");
        Permission p2 = new Permission("WRITE", "reports", "Can modify reports");
        Permission p3 = new Permission("Delete ", "Settings", "Can remove settings");

        System.out.println(p1.format());
        System.out.println(p2.format());
        System.out.println(p3.format());

        System.out.println(p1.matches("read", null));
        System.out.println(p1.matches("WRITE", "users"));
        System.out.println(p2.matches(".*ITE.*", "rep.*")); 

        try {
            Permission p4 = new Permission("TEST", "test", "");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
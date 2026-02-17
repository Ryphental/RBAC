package org.example;

public class Main {
    public static void main(String[] args) {
        User u1 = User.create("john_doe", "John Doe", "john@mail.com");
        System.out.println(u1.format());

//         try {
//            User u2 = User.create("jo", "J D", "j@m.c");
//        } catch (IllegalArgumentException e) {
//            System.out.println("Error: " + e.getMessage());
//        }
    }
}
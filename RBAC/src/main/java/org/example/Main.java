package org.example;

public class Main {
    public static void main(String[] args) {
        AssignmentMetadata meta1 = AssignmentMetadata.now("john_doe", "Project requirement");
        System.out.println(meta1.format());

        AssignmentMetadata meta2 = AssignmentMetadata.now("admin");
        System.out.println(meta2.format());
    }
}
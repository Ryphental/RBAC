package org.example.command;

import org.example.command.RBACSystem;
import java.util.Scanner;

@FunctionalInterface
public interface Command {
    void execute(Scanner scanner, RBACSystem system);
}

package org.example.command;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandParser {

    private final Map<String, Command> commands;
    private final Map<String, String> commandDescriptions;

    public CommandParser() {
        this.commands = new LinkedHashMap<>();
        this.commandDescriptions = new LinkedHashMap<>();
    }

    public void registerCommand(String name, String description, Command command) {
        commands.put(name.toLowerCase(), command);
        commandDescriptions.put(name.toLowerCase(), description);
    }

    public void executeCommand(String commandName, Scanner scanner, RBACSystem system) {
        Command cmd = commands.get(commandName.toLowerCase());
        if (cmd == null) {
            System.out.println("Unknown command: " + commandName);
            System.out.println("Type 'help' to see available commands");
            return;
        }

        try {
            cmd.execute(scanner, system);
        } catch (Exception e) {
            System.out.println("Error executing command: " + e.getMessage());
        }
    }

    public void printHelp() {
        System.out.println("\n=== Available Commands ===");
        commandDescriptions.forEach((cmd, desc) ->
                System.out.printf("  %-15s - %s\n", cmd, desc)
        );
        System.out.println("==========================\n");
    }

    public void parseAndExecute(String input, Scanner scanner, RBACSystem system) {
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String[] parts = input.trim().split("\\s+", 2);
        String commandName = parts[0];

        executeCommand(commandName, scanner, system);
    }

    public boolean hasCommand(String commandName) {
        return commands.containsKey(commandName.toLowerCase());
    }

    public Map<String, String> getCommandDescriptions() {
        return Map.copyOf(commandDescriptions);
    }
}
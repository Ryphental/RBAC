import org.example.command.RBACSystem;
import org.example.command.CommandRegistry;
import org.example.command.CommandParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class CommandRegistryTest {

    private RBACSystem system;
    private CommandRegistry registry;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        system = new RBACSystem();
        system.initialize();
        registry = new CommandRegistry(system);

        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testRegistryInitialization() {
        CommandParser parser = registry.getParser();
        assertNotNull(parser);
    }

    @Test
    void testUserListCommand() {
        registry.getParser().parseAndExecute("user-list", new Scanner(System.in), system);
        String output = outputStream.toString();

        assertTrue(output.contains("admin"));
        assertTrue(output.contains("System Administrator"));
    }

    @Test
    void testRoleListCommand() {
        registry.getParser().parseAndExecute("role-list", new Scanner(System.in), system);
        String output = outputStream.toString();

        assertTrue(output.contains("Admin"));
        assertTrue(output.contains("Manager"));
        assertTrue(output.contains("Viewer"));
    }

    @Test
    void testHelpCommand() {
        registry.getParser().parseAndExecute("help", new Scanner(System.in), system);
        String output = outputStream.toString();

        assertTrue(output.contains("Available Commands"));
        assertTrue(output.contains("user-list"));
        assertTrue(output.contains("role-list"));
        assertTrue(output.contains("assign-role"));
    }

    @Test
    void testStatsCommand() {
        registry.getParser().parseAndExecute("stats", new Scanner(System.in), system);
        String output = outputStream.toString();

        assertTrue(output.contains("Statistics"));
        assertTrue(output.contains("Total users: 1"));
    }

    @Test
    void testClearCommand() {
        registry.getParser().parseAndExecute("clear", new Scanner(System.in), system);
    }

    @Test
    void testUnknownCommand() {
        registry.getParser().parseAndExecute("blabla", new Scanner(System.in), system);
        String output = outputStream.toString();

        assertTrue(output.contains("Unknown command"));
    }

    @Test
    void testUserViewCommand() {
        String input = "admin\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        registry.getParser().parseAndExecute("user-view", scanner, system);
        String output = outputStream.toString();

        assertTrue(output.contains("admin"));
        assertTrue(output.contains("System Administrator"));
    }

    @Test
    void testRoleViewCommand() {
        String input = "Admin\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        registry.getParser().parseAndExecute("role-view", scanner, system);
        String output = outputStream.toString();

        assertTrue(output.contains("Admin"));
        assertTrue(output.contains("Full system access"));
    }
}

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

public class RBACSystemTest {

    private RBACSystem system;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        system = new RBACSystem();
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testSystemInitialization() {
        system.initialize();

        assertNotNull(system.getUserManager());
        assertNotNull(system.getRoleManager());
        assertNotNull(system.getAssignmentManager());
        assertEquals("admin", system.getCurrentUser());

        assertEquals(1, system.getUserManager().count());
        assertEquals(3, system.getRoleManager().count());
        assertEquals(1, system.getAssignmentManager().count());
    }

    @Test
    void testSetCurrentUser() {
        system.initialize();

        assertThrows(IllegalArgumentException.class, () ->
                system.setCurrentUser("nonexistent")
        );
    }

    @Test
    void testGenerateStatistics() {
        system.initialize();
        String stats = system.generateStatistics();

        assertTrue(stats.contains("Total users: 1"));
        assertTrue(stats.contains("Total roles: 3"));
        assertTrue(stats.contains("Total assignments: 1"));
        assertTrue(stats.contains("Current user: admin"));
    }

    @Test
    void testToString() {
        system.initialize();
        String str = system.toString();

        assertTrue(str.contains("users=1"));
        assertTrue(str.contains("roles=3"));
        assertTrue(str.contains("assignments=1"));
    }
}

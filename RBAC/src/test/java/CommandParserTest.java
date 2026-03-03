
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

public class CommandParserTest {

    private CommandParser parser;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        parser = new CommandParser();
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testRegisterCommand() {
        parser.registerCommand("test", "Test command", (sc, sys) ->
                System.out.println("executed"));

        assertTrue(parser.hasCommand("test"));
    }

    @Test
    void testExecuteCommand() {
        parser.registerCommand("test", "Test", (sc, sys) ->
                System.out.println("executed"));

        parser.executeCommand("test", new Scanner(System.in), null);
        assertEquals("executed\n", outputStream.toString());
    }

    @Test
    void testUnknownCommand() {
        parser.executeCommand("unknown", new Scanner(System.in), null);
        assertTrue(outputStream.toString().contains("Unknown command"));
    }

    @Test
    void testPrintHelp() {
        parser.registerCommand("cmd1", "Description 1", (sc, sys) -> {});
        parser.registerCommand("cmd2", "Description 2", (sc, sys) -> {});

        parser.printHelp();
        String output = outputStream.toString();

        assertTrue(output.contains("cmd1"));
        assertTrue(output.contains("Description 1"));
        assertTrue(output.contains("cmd2"));
        assertTrue(output.contains("Description 2"));
    }

    @Test
    void testParseAndExecute() {
        parser.registerCommand("hello", "Say hello", (sc, sys) ->
                System.out.println("Hello!"));

        parser.parseAndExecute("hello", new Scanner(System.in), null);
        assertEquals("Hello!\n", outputStream.toString());
    }

    @Test
    void testEmptyInput() {
        parser.parseAndExecute("", new Scanner(System.in), null);
        parser.parseAndExecute("   ", new Scanner(System.in), null);
        assertEquals("", outputStream.toString());
    }
}
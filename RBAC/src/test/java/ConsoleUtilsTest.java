
import org.example.util.ConsoleUtils;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleUtilsTest {

    @Test
    void testPromptString() {
        String input = "test\n";
        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes()));

        String result = ConsoleUtils.promptString(sc, "Enter test", true);
        assertEquals("test", result);
    }

    @Test
    void testPromptStringEmptyNotRequired() {
        String input = "\n";
        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes()));

        String result = ConsoleUtils.promptString(sc, "Enter test", false);
        assertEquals("", result);
    }

    @Test
    void testPromptInt() {
        String input = "5\n";
        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes()));

        int result = ConsoleUtils.promptInt(sc, "Enter number", 1, 10);
        assertEquals(5, result);
    }

    @Test
    void testPromptYesNo() {
        String input = "yes\n";
        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes()));

        boolean result = ConsoleUtils.promptYesNo(sc, "Confirm?");
        assertTrue(result);
    }

    @Test
    void testPromptChoice() {
        String input = "2\n";
        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes()));

        List<String> options = List.of("Option 1", "Option 2", "Option 3");
        String result = ConsoleUtils.promptChoice(sc, "Choose", options);

        assertEquals("Option 2", result);
    }

    @Test
    void testPromptChoiceWithNumbers() {
        String input = "3\n";
        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes()));

        List<Integer> options = List.of(10, 20, 30, 40);
        Integer result = ConsoleUtils.promptChoice(sc, "Choose number", options);

        assertEquals(30, result);
    }
}
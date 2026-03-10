
import org.example.util.FormatUtils;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FormatUtilsTest {

    @Test
    void testTruncate() {
        assertEquals("Hello", FormatUtils.truncate("Hello", 5));
        assertEquals("He...", FormatUtils.truncate("Hello World", 5));
        assertEquals("", FormatUtils.truncate(null, 5));
    }

    @Test
    void testPadRight() {
        assertEquals("Hello     ", FormatUtils.padRight("Hello", 10));
        assertEquals("Hello", FormatUtils.padRight("Hello", 3));
    }

    @Test
    void testPadLeft() {
        assertEquals("     Hello", FormatUtils.padLeft("Hello", 10));
        assertEquals("Hello", FormatUtils.padLeft("Hello", 3));
    }

    @Test
    void testPadCenter() {
        assertEquals("  Hello   ", FormatUtils.padCenter("Hello", 10));
        assertEquals("Hello", FormatUtils.padCenter("Hello", 3));
    }

    @Test
    void testRepeat() {
        assertEquals("----", FormatUtils.repeat("-", 4));
        assertEquals("", FormatUtils.repeat("-", 0));
    }

    @Test
    void testFormatTable() {
        String[] headers = {"Name", "Age"};
        List<String[]> rows = List.of(
                new String[]{"Alice", "25"},
                new String[]{"Bob", "30"}
        );

        String table = FormatUtils.formatTable(headers, rows);
        assertTrue(table.contains("Alice"));
        assertTrue(table.contains("Bob"));
        assertTrue(table.contains("Name"));
        assertTrue(table.contains("Age"));
        assertTrue(table.contains("┌"));
        assertTrue(table.contains("┐"));
        assertTrue(table.contains("└"));
        assertTrue(table.contains("┘"));
        assertTrue(table.contains("│"));
    }

    @Test
    void testFormatBox() {
        String box = FormatUtils.formatBox("Hello\nWorld");
        assertTrue(box.contains("Hello"));
        assertTrue(box.contains("World"));
        assertTrue(box.contains("┌"));
        assertTrue(box.contains("┐"));
        assertTrue(box.contains("└"));
        assertTrue(box.contains("┘"));
        assertTrue(box.contains("│"));
    }

    @Test
    void testFormatHeader() {
        String header = FormatUtils.formatHeader("Test Header");
        assertTrue(header.contains("Test Header"));
        assertTrue(header.contains("╔"));
        assertTrue(header.contains("╗"));
        assertTrue(header.contains("╚"));
        assertTrue(header.contains("╝"));
    }
}

import org.example.util.DateUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DateUtilsTest {

    @Test
    void testGetCurrentDate() {
        String date = DateUtils.getCurrentDate();
        assertTrue(date.matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    void testIsBefore() {
        assertTrue(DateUtils.isBefore("2024-01-01", "2024-01-02"));
        assertFalse(DateUtils.isBefore("2024-01-02", "2024-01-01"));
    }

    @Test
    void testIsAfter() {
        assertTrue(DateUtils.isAfter("2024-01-02", "2024-01-01"));
        assertFalse(DateUtils.isAfter("2024-01-01", "2024-01-02"));
    }

    @Test
    void testAddDays() {
        String date = "2024-01-01";
        String result = DateUtils.addDaysSimple(date, 5);
        assertEquals("2024-01-06", result);
    }

    @Test
    void testFormatRelativeTimePast() {
        String pastDate = DateUtils.addDaysSimple(DateUtils.getCurrentDate(), -2);
        String result = DateUtils.formatRelativeTime(pastDate);
        assertTrue(result.contains("days ago") || result.contains("day ago"));
    }

    @Test
    void testFormatRelativeTimeFuture() {
        String futureDate = DateUtils.addDaysSimple(DateUtils.getCurrentDate(), 3);
        String result = DateUtils.formatRelativeTime(futureDate);
        assertTrue(result.contains("in"));
    }

    @Test
    void testIsValidDate() {
        assertTrue(DateUtils.isValidDate("2024-01-01"));
        assertTrue(DateUtils.isValidDate("2024-01-01 10:30"));
        assertTrue(DateUtils.isValidDate("2024-01-01 10:30:00"));
        assertFalse(DateUtils.isValidDate("invalid"));
        assertFalse(DateUtils.isValidDate("2024/01/01"));
    }

    @Test
    void testDaysBetween() {
        assertEquals(5, DateUtils.daysBetween("2024-01-01", "2024-01-06"));
        assertEquals(-5, DateUtils.daysBetween("2024-01-06", "2024-01-01"));
    }
}
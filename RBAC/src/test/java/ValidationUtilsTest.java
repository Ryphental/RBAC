
import org.example.util.ValidationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilsTest {

    @Test
    void testValidUsername() {
        assertTrue(ValidationUtils.isValidUsername("john_doe"));
        assertTrue(ValidationUtils.isValidUsername("user123"));
        assertTrue(ValidationUtils.isValidUsername("a1_b2_c3"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"jo", "very_long_username_21", "user name", "user@name", ""})
    void testInvalidUsername(String username) {
        assertFalse(ValidationUtils.isValidUsername(username));
    }

    @Test
    void testValidEmail() {
        assertTrue(ValidationUtils.isValidEmail("user@example.com"));
        assertTrue(ValidationUtils.isValidEmail("user.name@domain.co.uk"));
        assertTrue(ValidationUtils.isValidEmail("user+tag@gmail.com"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"user@", "@domain.com", "user@domain", "user.domain.com", ""})
    void testInvalidEmail(String email) {
        assertFalse(ValidationUtils.isValidEmail(email));
    }

    @Test
    void testValidDate() {
        assertTrue(ValidationUtils.isValidDate("2026-03-10 15:30"));
        assertTrue(ValidationUtils.isValidDate("2024-12-31 23:59"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2026/03/10 15:30", "10-03-2026", "2026-03-10", "invalid", ""})
    void testInvalidDate(String date) {
        assertFalse(ValidationUtils.isValidDate(date));
    }

    @Test
    void testNormalizeString() {
        assertEquals("John Doe", ValidationUtils.normalizeString("  John   Doe  "));
        assertEquals("Hello World", ValidationUtils.normalizeString("Hello    World"));
        assertEquals("", ValidationUtils.normalizeString(null));
        assertEquals("", ValidationUtils.normalizeString("   "));
    }

    @Test
    void testRequireNonEmpty() {
        assertDoesNotThrow(() -> ValidationUtils.requireNonEmpty("test", "field"));
        assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNonEmpty("", "field"));
        assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNonEmpty("   ", "field"));
        assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNonEmpty(null, "field"));
    }
}
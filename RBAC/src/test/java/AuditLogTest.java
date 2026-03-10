
import org.example.audit.AuditLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuditLogTest {

    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditLog = new AuditLog();
    }

    @Test
    void testLogEntry() {
        auditLog.log("TEST_ACTION", "admin", "user1", "Test details");

        List<AuditLog.AuditEntry> entries = auditLog.getAll();
        assertEquals(1, entries.size());

        AuditLog.AuditEntry entry = entries.get(0);
        assertEquals("TEST_ACTION", entry.action());
        assertEquals("admin", entry.performer());
        assertEquals("user1", entry.target());
        assertEquals("Test details", entry.details());
        assertNotNull(entry.timestamp());
    }

    @Test
    void testGetByPerformer() {
        auditLog.log("ACTION1", "admin", "target1", "details1");
        auditLog.log("ACTION2", "admin", "target2", "details2");
        auditLog.log("ACTION3", "user", "target3", "details3");

        List<AuditLog.AuditEntry> adminEntries = auditLog.getByPerformer("admin");
        assertEquals(2, adminEntries.size());

        List<AuditLog.AuditEntry> userEntries = auditLog.getByPerformer("user");
        assertEquals(1, userEntries.size());
    }

    @Test
    void testGetByAction() {
        auditLog.log("CREATE", "admin", "user1", "details1");
        auditLog.log("CREATE", "admin", "user2", "details2");
        auditLog.log("DELETE", "admin", "user3", "details3");

        List<AuditLog.AuditEntry> createEntries = auditLog.getByAction("CREATE");
        assertEquals(2, createEntries.size());

        List<AuditLog.AuditEntry> deleteEntries = auditLog.getByAction("DELETE");
        assertEquals(1, deleteEntries.size());
    }

    @Test
    void testSaveToFile(@TempDir Path tempDir) throws Exception {
        auditLog.log("TEST", "admin", "target", "details");

        Path filePath = tempDir.resolve("audit.csv");
        auditLog.saveToFile(filePath.toString());

        assertTrue(Files.exists(filePath));
        String content = Files.readString(filePath);
        assertTrue(content.contains("TEST,admin,target,details"));
    }

    @Test
    void testPrintLog() {
        auditLog.log("TEST1", "admin", "target1", "details1");
        auditLog.log("TEST2", "user", "target2", "details2");

        // Just verify no exception
        assertDoesNotThrow(() -> auditLog.printLog());
    }
}
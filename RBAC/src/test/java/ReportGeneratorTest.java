
import org.example.*;
import org.example.Manager.UserManager;
import org.example.Manager.RoleManager;
import org.example.Manager.AssignmentManager;
import org.example.report.ReportGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class ReportGeneratorTest {

    private UserManager userManager;
    private RoleManager roleManager;
    private AssignmentManager assignmentManager;
    private ReportGenerator generator;

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        roleManager = new RoleManager();
        assignmentManager = new AssignmentManager(userManager, roleManager);
        generator = new ReportGenerator();

        // Create test data
        Permission readUsers = new Permission("READ", "users", "Read users");
        Permission writeUsers = new Permission("WRITE", "users", "Write users");

        Role adminRole = new Role("Admin", "Admin role");
        adminRole.addPermission(readUsers);
        adminRole.addPermission(writeUsers);

        Role viewerRole = new Role("Viewer", "Viewer role");
        viewerRole.addPermission(readUsers);

        roleManager.add(adminRole);
        roleManager.add(viewerRole);

        User user1 = User.create("alice", "Alice Smith", "alice@test.com");
        User user2 = User.create("bob", "Bob Jones", "bob@test.com");

        userManager.add(user1);
        userManager.add(user2);

        AssignmentMetadata meta1 = AssignmentMetadata.now("admin", "Test");
        AssignmentMetadata meta2 = AssignmentMetadata.now("admin", "Test");

        assignmentManager.add(new PermanentAssignment(user1, adminRole, meta1));
        assignmentManager.add(new PermanentAssignment(user2, viewerRole, meta2));
    }

    @Test
    void testGenerateUserReport() {
        String report = generator.generateUserReport(userManager, assignmentManager);

        assertTrue(report.contains("USER REPORT"));
        assertTrue(report.contains("alice"));
        assertTrue(report.contains("bob"));
        assertTrue(report.contains("Admin"));
        assertTrue(report.contains("Viewer"));
        assertTrue(report.contains("Total users: 2"));
    }

    @Test
    void testGenerateRoleReport() {
        String report = generator.generateRoleReport(roleManager, assignmentManager);

        assertTrue(report.contains("ROLE REPORT"));
        assertTrue(report.contains("Admin"));
        assertTrue(report.contains("Viewer"));
        assertTrue(report.contains("READ on users"));
        assertTrue(report.contains("WRITE on users"));
        assertTrue(report.contains("Total roles: 2"));
    }

    @Test
    void testGeneratePermissionMatrix() {
        String report = generator.generatePermissionMatrix(userManager, assignmentManager);

        assertTrue(report.contains("PERMISSION MATRIX"));
        assertTrue(report.contains("alice"));
        assertTrue(report.contains("bob"));
        assertTrue(report.contains("users"));
        assertTrue(report.contains("READ"));
        assertTrue(report.contains("WRITE"));
    }

    @Test
    void testExportToFile(@TempDir Path tempDir) throws Exception {
        String report = "Test report content";
        Path filePath = tempDir.resolve("report.txt");

        generator.exportToFile(report, filePath.toString());

        assertTrue(Files.exists(filePath));
        String content = Files.readString(filePath);
        assertEquals(report, content);
    }
}
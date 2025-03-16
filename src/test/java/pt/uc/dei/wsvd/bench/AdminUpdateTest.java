package pt.uc.dei.wsvd.bench;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import pt.uc.dei.wsvd.bench.tpcw.versions.AdminUpdate_VxA;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mockStatic;

public class AdminUpdateTest {

    @InjectMocks
    private AdminUpdate_VxA adminService;
    Connection sqliteConnection = null;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        sqliteConnection = DriverManager.getConnection("jdbc:sqlite::memory:");
        sqliteConnection.createStatement().execute("CREATE TABLE tpcw_item (id INT PRIMARY KEY, i_cost INT, i_image VARCHAR(255), i_thumbnail VARCHAR(255), i_pub_date DATE)");
        sqliteConnection.createStatement().execute("INSERT INTO tpcw_item VALUES (1, 123, 'image', 'thumbnail', '2021-01-01')");
    }

    @Test
    void testAdminUpdateSQLInjection() throws Exception {
        MockedStatic<Database> mockedStatic = mockStatic(Database.class);
        mockedStatic.when(Database::pickConnection).thenReturn(sqliteConnection);
        mockedStatic.when(() -> Database.createStatement(sqliteConnection)).thenCallRealMethod();
        adminService.adminUpdate(1, 123, "'; DROP TABLE tpcw_item; --", "123");
        // Verifica se a tabela ainda existe apos DROP TABLE
        assertDoesNotThrow(() -> sqliteConnection.createStatement().executeQuery("SELECT * FROM tpcw_item").next());
    }
}
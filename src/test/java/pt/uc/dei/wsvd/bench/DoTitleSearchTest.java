package pt.uc.dei.wsvd.bench;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import pt.uc.dei.wsvd.bench.tpcw.object.Book;
import pt.uc.dei.wsvd.bench.tpcw.versions.DoTitleSearch_VxA;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.mockito.Mockito.mockStatic;

public class DoTitleSearchTest {

    @InjectMocks
    private DoTitleSearch_VxA titleSearchService;
    Connection sqliteConnection = null;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        sqliteConnection = DriverManager.getConnection("jdbc:sqlite::memory:");
        sqliteConnection.createStatement().execute("CREATE TABLE tpcw_item (id INT PRIMARY KEY, i_cost INT, i_image VARCHAR(255), i_thumbnail VARCHAR(255), i_pub_date DATE, i_title VARCHAR(255), i_a_id INT)");
        sqliteConnection.createStatement().execute("CREATE TABLE tpcw_author (a_id INT PRIMARY KEY, a_lname VARCHAR(255), a_fname VARCHAR(255), a_bio VARCHAR(255), a_dob DATE)");
        sqliteConnection.createStatement().execute("INSERT INTO tpcw_item VALUES (1, 123, 'image', 'thumbnail', '2021-01-01', 'title', 1)");
        sqliteConnection.createStatement().execute("INSERT INTO tpcw_author VALUES (1, 'lname', 'fname', 'bio', '2021-01-01')");
    }

    @Test
    void testTitleSearchSQLInjection() throws Exception {
        MockedStatic<Database> mockedStatic = mockStatic(Database.class);
        mockedStatic.when(Database::pickConnection).thenReturn(sqliteConnection);
        mockedStatic.when(() -> Database.createStatement(sqliteConnection)).thenCallRealMethod();
        List<Book> books = titleSearchService.doTitleSearch("' OR 1=1 -- ");
        // Precisa ser 0 porque não há nenhum livro com esse título
        assert (books.size() == 0);
    }
}
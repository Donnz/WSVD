package pt.uc.dei.wsvd.bench.tpcw.versions;

import pt.uc.dei.wsvd.bench.Database;
import pt.uc.dei.wsvd.bench.tpcw.object.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * WS - Vulnerability Detection Tools Benchmark
 * TPC - C Benchmark Services
 * #WebServiceOperation
 *
 *
 * @author nmsa@dei.uc.pt
 */
public class DoTitleSearch_VxA {

    public List<Book> doTitleSearch(
            String search_key) {
        List<Book> vec = new ArrayList<Book>();
        Connection con = Database.pickConnection();
        try {
            PreparedStatement stmt = Database.preparedStatement(con,
                    "SELECT * FROM tpcw_item, tpcw_author "
                    + "WHERE tpcw_item.i_a_id = tpcw_author.a_id AND tpcw_item.i_title LIKE '?%'"
                    + "AND ROWNUM <= 50 ORDER BY tpcw_item.i_title ");
            stmt.setString(1, search_key);
            ResultSet rs = stmt.executeQuery();
            // Results
            while (rs.next()) {
                vec.add(new Book(rs));
            }
            rs.close();
            con.commit();
        } catch (java.lang.Exception ex) {
            //ex.printStackTrace();
        } finally {
            Database.relaseConnection(con);
        }
        return vec;
    }
}

package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class DB {

    public static Connection c;

    public static void SetUpConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        c = DriverManager.getConnection(
                AppConfig.get("db.url", "jdbc:mysql://localhost:3306/masters"),
                AppConfig.get("db.user", "root"),
                AppConfig.get("db.password", ""));
    }

    public static void IUD(String sql) throws Exception {
        if (c == null) {
            SetUpConnection();
        }
        c.createStatement().executeUpdate(sql);
    }

    public static ResultSet search(String sql) throws Exception {
        if (c == null) {
            SetUpConnection();
        }
        return c.createStatement().executeQuery(sql);
    }
}

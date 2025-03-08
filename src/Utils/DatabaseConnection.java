package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
  private static final String URL = Config.get("DB_URL");
  private static final String USER = Config.get("DB_USER");
  private static final String PASSWORD = Config.get("DB_PASSWORD");

  //Get database connection
  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
}

package systems.server;

import systems.common.SqlCommands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class SqliteHandler {
    private static final String DB = "auth";
    private static final String sqliteUrl = "jdbc:sqlite:server\\" + DB + ".db";
    private static Connection connection = null;
    private static Statement statement = null;


    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(sqliteUrl);
        statement = connection.createStatement();
    }

    public static String sqlTask(SqlCommands command, String... arg) throws SQLException {
        switch (command) {
            case SELECT_USERNAME_AUTH:
                return (statement.executeQuery(String.format("Select * from " + DB + " where login = '%s' and password = '%s'", arg[0], arg[1])).getString("username"));
            case SELECT_LOGIN:
                return (statement.executeQuery(String.format("Select * from " + DB + " where username = '%s'", arg[0])).getString("login"));
            case SELECT_USERNAME:
                return (statement.executeQuery(String.format("Select * from " + DB + " where login = '%s'", arg[0])).getString("username"));
            case UPDATE:
                statement.executeUpdate(String.format("update " + DB + " set username = '%s' where login = '%s'", arg[1], arg[0]));
                return "update done";
            case INSERT:
                statement.executeUpdate(String.format("insert into " + DB + " (login, password, username) values ('%s','%s','%s')", arg[0], arg[1], arg[0]));
                return "done";
            default:
                return null;
        }
    }

    public static void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}

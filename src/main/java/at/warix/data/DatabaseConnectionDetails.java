package at.warix.data;

public class DatabaseConnectionDetails {
    private static String host;
    private static String port;
    private static String database;
    private static String username;
    private static String password;

    public static void writeConnectionDetails(String host, String port, String database, String username, String password) {
        setHost(host);
        setPort(port);
        setDatabase(database);
        setUsername(username);
        setPassword(password);
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        DatabaseConnectionDetails.host = host;
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        DatabaseConnectionDetails.port = port;
    }

    public static String getDatabase() {
        return database;
    }

    public static void setDatabase(String database) {
        DatabaseConnectionDetails.database = database;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        DatabaseConnectionDetails.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        DatabaseConnectionDetails.password = password;
    }
}

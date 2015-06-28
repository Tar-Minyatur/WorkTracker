package de.tshw.worktracker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DerbyDatabaseProvider implements DatabaseProvider {

    public static final String DB_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String DEFAULT_DB_URL = "jdbc:derby:WorkTrackerDB;create=true";

    private Connection openConnection;
    private final String dbURL;

    public DerbyDatabaseProvider() {
        this.dbURL = DEFAULT_DB_URL;
    }

    DerbyDatabaseProvider(String databaseName) {
        this.dbURL = "jdbc:derby:" + databaseName + ";create=true";
    }

    @Override
    public Connection getDatabaseConnection() throws ClassNotFoundException, SQLException {
        if (this.openConnection == null) {
            Class.forName(DB_DRIVER);
            this.openConnection = DriverManager.getConnection(this.dbURL);
            this.openConnection.setAutoCommit(true);
        }
        return this.openConnection;
    }
}

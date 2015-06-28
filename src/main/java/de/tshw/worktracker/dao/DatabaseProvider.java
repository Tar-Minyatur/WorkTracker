package de.tshw.worktracker.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseProvider {

    Connection getDatabaseConnection() throws ClassNotFoundException, SQLException;

}

/******************************************************************************
 * This file is part of WorkTracker, Copyright (c) 2015 Till Helge Helwig.    *
 *                                                                            *
 * WorkTracker is distributed under the MIT License (MIT), so feel free       *
 * to do whatever you want with this code. You may notify the author about    *
 * bugs via http://github.com/Tar-Minyatur/WorkTracker/issues, but be aware   *
 * that he is not (legally) obligated to provide support. You are using       *
 * this software at your own risk.                                            *
 ******************************************************************************/

package de.tshw.worktracker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DerbyDatabaseProvider implements DatabaseProvider {

	public static final String DB_DRIVER      = "org.apache.derby.jdbc.EmbeddedDriver";
	public static final String DEFAULT_DB_URL = "jdbc:derby:WorkTrackerDB;create=true";
	private final String     dbURL;
	private       Connection openConnection;

	public DerbyDatabaseProvider() {
		this.dbURL = DEFAULT_DB_URL;
	}

	public DerbyDatabaseProvider( String databaseName ) {
		this.dbURL = "jdbc:derby:" + databaseName + ";create=true";
	}

	@Override
	public Connection getDatabaseConnection() throws ClassNotFoundException, SQLException {
		if ( this.openConnection == null ) {
			Class.forName(DB_DRIVER);
			this.openConnection = DriverManager.getConnection(this.dbURL);
			this.openConnection.setAutoCommit(true);
		}
		return this.openConnection;
	}
}

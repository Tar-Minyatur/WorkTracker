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

import de.tshw.worktracker.model.Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMaintainer {

	public void ensureDatabaseIsCorrect( DatabaseProvider dbProvider ) throws SQLException, ClassNotFoundException {
		Connection connection = dbProvider.getDatabaseConnection();

		Statement statement = connection.createStatement();
		createProjectTable(statement);
		createWorkLogEntryTable(statement);
		createDefaultEntries(connection);
	}

	private void createProjectTable( Statement statement ) {
		try {
			this.log("Creating table 'Project'...");
			statement.execute("CREATE TABLE Project (" +
							  "id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
							  "name VARCHAR(100) NOT NULL UNIQUE)");
		}
		catch (SQLException ex) {
			this.log("Table 'Project' already exists. (Error: " + ex.getMessage() + ")");
		}
	}

	private void createWorkLogEntryTable( Statement statement ) {
		try {
			this.log("Creating table 'WorkLogEntry'...");
			statement.execute("CREATE TABLE WorkLogEntry (" +
							  "id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
							  "project_id BIGINT NOT NULL," +
							  "start_time TIMESTAMP NOT NULL," +
							  "end_time TIMESTAMP," +
							  "comment VARCHAR(255)," +
							  "CONSTRAINT project_id_ref FOREIGN KEY (project_id) REFERENCES Project(id))");
		}
		catch (SQLException ex) {
			this.log("Table 'WorkLogEntry' already exists. (Error: " + ex.getMessage() + ")");
		}
	}

	private void createDefaultEntries( Connection connection ) {
		try {
			this.log("Creating default entry in table 'Project'...");
			String sql = "INSERT INTO Project (name) VALUES (?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, Project.PAUSE_PROJECT_NAME);
			statement.execute();
		}
		catch (SQLException ex) {
			this.log("Default entry in table 'Project' already exists. (Error: " + ex.getMessage() + ")");
		}
	}

	private void log( String message ) {
		System.out.println("[DatabaseMaintainer] " + message);
	}

	public void dropTables( DatabaseProvider dbProvider ) throws SQLException, ClassNotFoundException {
		Connection connection = dbProvider.getDatabaseConnection();

		Statement statement = connection.createStatement();
		try {
			statement.execute("DROP TABLE WorkLogEntry");
			statement.execute("DROP TABLE Project");
		}
		catch (Exception ex) {
			// This should rarely fail. If it does, we don't much care for it.
		}
	}

	public void clearTables( DatabaseProvider dbProvider ) throws SQLException, ClassNotFoundException {
		Connection connection = dbProvider.getDatabaseConnection();

		Statement statement = connection.createStatement();
		try {
			statement.executeUpdate("DELETE FROM WorkLogEntry");
			statement.executeUpdate("DELETE FROM Project");
		}
		catch (Exception ex) {
			// This should rarely fail. If it does, we don't much care for it.
		}
	}

}

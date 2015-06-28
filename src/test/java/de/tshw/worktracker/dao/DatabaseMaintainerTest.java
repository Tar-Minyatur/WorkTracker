/******************************************************************************
 * This file is part of WorkTracker, Copyright (c) 2015 Till Helge Helwig.    *
 *                                                                            *
 * WorkTracker is distributed under the MIT License, so feel free to do       *
 * whatever you want with application or code. You may notify the author      *
 * about bugs via http://github.com/Tar-Minyatur/WorkTracker/issues, but      *
 * be aware that he is not (legally) obligated to provide support. You are    *
 * using this software at your own risk.                                      *
 ******************************************************************************/

package de.tshw.worktracker.dao;

import org.junit.Assert;
import org.junit.Test;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class DatabaseMaintainerTest {

	@Test
	public void createdNecessaryTables() {
		DatabaseProvider dbProvider = new DerbyDatabaseProvider("dbMaintainerTest");

		DatabaseMaintainer dbMaintainer = new DatabaseMaintainer();
		try {
			dbMaintainer.dropTables(dbProvider);
			dbMaintainer.ensureDatabaseIsCorrect(dbProvider);

			DatabaseMetaData metaData = dbProvider.getDatabaseConnection().getMetaData();
			assertTrue("Table 'Project' not found", checkIfTableExists(metaData, "Project"));
			assertTrue("Table 'WorkLogEntry' not found", checkIfTableExists(metaData, "WorkLogEntry"));

		}
		catch (Exception ex) {
			Assert.fail(ex.getClass().getName() + " thrown: " + ex.getMessage());
		}
	}

	private boolean checkIfTableExists( DatabaseMetaData metaData, String tableName ) throws SQLException {
		// We can't really rely on the name being spelled exactly as we want it, so let's mess about
		String tablePattern = ( tableName.charAt(0) + "%" ).toUpperCase();
		ResultSet result = metaData.getTables(null, null, tablePattern, new String[]{ "TABLE" });
		boolean tableFound = false;
		while ( ( result != null ) && result.next() ) {
			tableFound = tableFound || result.getString("TABLE_NAME").equalsIgnoreCase(tableName);
		}
		return tableFound;
	}

}

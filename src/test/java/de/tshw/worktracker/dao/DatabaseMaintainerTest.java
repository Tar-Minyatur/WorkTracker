package de.tshw.worktracker.dao;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        } catch (Exception ex) {
            Assert.fail(ex.getClass().getName() + " thrown: " + ex.getMessage());
        }
    }

    private boolean checkIfTableExists(DatabaseMetaData metaData, String tableName) throws SQLException {
        // We can't really rely on the name being spelled exactly as we want it, so let's mess about
        String tablePattern = (tableName.charAt(0) + "%").toUpperCase();
        ResultSet result = metaData.getTables(null, null, tablePattern, new String[]{"TABLE"});
        boolean tableFound = false;
        while ((result != null) && result.next()) {
            tableFound = tableFound || result.getString("TABLE_NAME").equalsIgnoreCase(tableName);
        }
        return tableFound;
    }

}

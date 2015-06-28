package de.tshw.worktracker.dao;

import de.tshw.worktracker.model.Project;
import de.tshw.worktracker.model.WorkLogEntry;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class WorkLogEntryDAOTest {

    private static DatabaseProvider dbProvider;
    private static DatabaseMaintainer dbMaintainer;
    private static ProjectDAO projectDAO;
    private static WorkLogEntryDAO workLogEntryDAO;

    @BeforeClass
    public static void setUp() throws SQLException, ClassNotFoundException {
        dbProvider = new DerbyDatabaseProvider("WorkTrackerTest");
        dbMaintainer = new DatabaseMaintainer();
        dbMaintainer.ensureDatabaseIsCorrect(dbProvider);
        dbMaintainer.clearTables(dbProvider);
        projectDAO = new ProjectDAO(dbProvider);
        workLogEntryDAO = new WorkLogEntryDAO(dbProvider, projectDAO);
    }

    @Before
    public void init() throws SQLException, ClassNotFoundException {
        dbMaintainer.clearTables(dbProvider);
    }

    @Test
    public void storesEntitiesInTheDatabase() {
        Project project = new Project("Test 1");
        WorkLogEntry entry = new WorkLogEntry(project);
        workLogEntryDAO.save(entry);
        Assert.assertNotNull("No ID was assigned", entry.getId());
        Assert.assertNotNull("Related entity got no ID", project.getId());
        WorkLogEntry entry2 = new WorkLogEntry(project);
        entry2.stop();
        workLogEntryDAO.save(entry2);
        Assert.assertNotNull("No ID was assigned", entry2.getId());

        List<WorkLogEntry> entries = workLogEntryDAO.getAll();
        boolean entry1Found = false;
        boolean entry2Found = false;
        for (WorkLogEntry e: entries) {
            entry1Found |= (e.getProject() == project) && e.isRunning() && (e == entry);
            entry2Found |= (e.getProject() == project) && !e.isRunning() && (e == entry2);
        }
        Assert.assertTrue("First entry is not in the database", entry1Found);
        Assert.assertTrue("Second entry is not in the database", entry2Found);
    }

    @Test
    public void deletesEntityFromTheDatabase() {
        Project project = new Project("Test 1");
        WorkLogEntry entry = new WorkLogEntry(project);
        entry.setComment("Delete me");
        workLogEntryDAO.save(entry);
        List<WorkLogEntry> entries = workLogEntryDAO.getAll();
        Assert.assertThat(entries.size(), is(1));
        workLogEntryDAO.delete(entry);
        entries = workLogEntryDAO.getAll();
        Assert.assertThat(entries.size(), is(0));
        Assert.assertNull("Entry's ID has not been cleared", entry.getId());
    }

    @Test
    public void updatesEntityInTheDatabase() {
        WorkLogEntry entry = createStoredWorkLogEntry();
        entry.stop();
        entry.setComment("Foo");
        workLogEntryDAO.save(entry);
        WorkLogEntry entry2 = workLogEntryDAO.findById(entry.getId());
        Assert.assertNotNull("End date has not been stored", entry2.getEndTime());
        Assert.assertThat(entry2.getComment(), is("Foo"));
    }

    @Test
    public void preventsCreationOfDuplicates() {
        WorkLogEntry entry = createStoredWorkLogEntry();
        WorkLogEntry entry2 = workLogEntryDAO.findById(entry.getId());
        Assert.assertTrue("There are two separate entity objects", entry == entry2);
    }

    @Test
    public void retrievesUnknownEntity() {
        WorkLogEntry entry = createStoredWorkLogEntry();
        WorkLogEntryDAO workLogEntryDAO2 = new WorkLogEntryDAO(dbProvider, projectDAO);
        List<WorkLogEntry> entries = workLogEntryDAO2.findWithoutEndDate();
        Assert.assertThat(entries.size(), is(1));
        WorkLogEntry entry2 = entries.get(0);
        Assert.assertTrue(entry != entry2);
        Assert.assertThat(entry.getId(), is(entry2.getId()));
    }

    @Test
    public void findsEntriesByDay() {
        Project project = new Project("Test 1");
        WorkLogEntry entry = new WorkLogEntry(project);
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        entry.setStartTime(yesterday);
        entry.setComment("Yesterday");
        workLogEntryDAO.save(entry);
        entry = new WorkLogEntry(project);
        entry.setComment("Today");
        workLogEntryDAO.save(entry);
        List<WorkLogEntry> entries = workLogEntryDAO.findByDay(LocalDate.now());
        Assert.assertThat(entries.size(), is(1));
        entry = entries.get(0);
        Assert.assertThat(entry.getComment(), is("Today"));
        entries = workLogEntryDAO.findByDay(yesterday.toLocalDate());
        Assert.assertThat(entries.size(), is(1));
        entry = entries.get(0);
        Assert.assertThat(entry.getComment(), is("Yesterday"));
    }

    private WorkLogEntry createStoredWorkLogEntry() {
        Project project = new Project("Test 1");
        WorkLogEntry entry = new WorkLogEntry(project);
        workLogEntryDAO.save(entry);
        return entry;
    }

}

package de.tshw.worktracker.dao;

import de.tshw.worktracker.model.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class ProjectDAOTest {

    private static DatabaseProvider dbProvider;
    private static DatabaseMaintainer dbMaintainer;
    private static ProjectDAO projectDAO;

    @BeforeClass
    public static void setUp() throws SQLException, ClassNotFoundException {
        dbProvider = new DerbyDatabaseProvider("WorkTrackerTest");
        dbMaintainer = new DatabaseMaintainer();
        dbMaintainer.ensureDatabaseIsCorrect(dbProvider);
        dbMaintainer.clearTables(dbProvider);
        projectDAO = new ProjectDAO(dbProvider);
    }

    @Before
    public void init() throws SQLException, ClassNotFoundException {
        dbMaintainer.clearTables(dbProvider);
    }

    @Test
    public void storesEntitiesInTheDatabase() {
        Project project = new Project("Test 1");
        projectDAO.save(project);
        Assert.assertNotNull("No ID was assigned", project.getId());
        project = new Project("Test 2");
        projectDAO.save(project);
        Assert.assertNotNull("No ID was assigned", project.getId());

        List<Project> projects = projectDAO.getAll();
        boolean project1Found = false;
        boolean project2Found = false;
        for (Project p: projects) {
            project1Found |= p.getName().equals("Test 1");
            project2Found |= p.getName().equals("Test 2");
        }
        Assert.assertTrue("Test 1 is not in the database", project1Found);
        Assert.assertTrue("Test 2 is not in the database", project2Found);
    }

    @Test
    public void deletesEntityFromTheDatabase() {
        Project project = new Project("Delete me");
        projectDAO.save(project);
        List<Project> projects = projectDAO.getAll();
        Assert.assertThat(projects.size(), is(1));
        projectDAO.delete(project);
        projects = projectDAO.getAll();
        Assert.assertThat(projects.size(), is(0));
        Assert.assertNull("Project's ID has not been cleared", project.getId());
    }

    @Test
    public void updatesEntityInTheDatabase() {
        Project project = new Project("Test 1");
        projectDAO.save(project);
        project.setName("Test 2");
        projectDAO.save(project);
        Project project2 = projectDAO.findById(project.getId());
        Assert.assertThat(project2.getName(), is("Test 2"));
    }

    @Test
    public void preventsCreationOfDuplicates() {
        Project project = new Project("Test 1");
        projectDAO.save(project);
        Project project2 = projectDAO.findById(project.getId());
        Assert.assertTrue("There are two separate entity objects", project == project2);
    }

    @Test
    public void retrievesUnknownEntity() {
        Project project = new Project("Test 1");
        projectDAO.save(project);
        ProjectDAO projectDAO2 = new ProjectDAO(dbProvider);
        Project project2 = projectDAO2.findByName("Test 1");
        Assert.assertThat(project2.getName(), is("Test 1"));
        Assert.assertTrue(project != project2);
        Assert.assertThat(project.getId(), is(project2.getId()));
        projectDAO2 = new ProjectDAO(dbProvider);
        project2 = projectDAO2.findById(project.getId());
        Assert.assertThat(project2.getName(), is("Test 1"));
        Assert.assertTrue(project != project2);
    }

}

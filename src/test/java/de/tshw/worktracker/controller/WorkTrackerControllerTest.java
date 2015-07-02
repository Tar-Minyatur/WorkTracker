/******************************************************************************
 * This file is part of WorkTracker, Copyright (c) 2015 Till Helge Helwig.    *
 *                                                                            *
 * WorkTracker is distributed under the MIT License, so feel free to do       *
 * whatever you want with application or code. You may notify the author      *
 * about bugs via http://github.com/Tar-Minyatur/WorkTracker/issues, but      *
 * be aware that he is not (legally) obligated to provide support. You are    *
 * using this software at your own risk.                                      *
 ******************************************************************************/

package de.tshw.worktracker.controller;

import de.tshw.worktracker.dao.*;
import de.tshw.worktracker.model.Project;
import de.tshw.worktracker.model.WorkLogEntry;
import de.tshw.worktracker.model.WorkTracker;
import de.tshw.worktracker.view.WorkTrackerView;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;

public class WorkTrackerControllerTest {

	private static DatabaseProvider      dbProvider;
	private static DatabaseMaintainer    dbMaintainer;
	private static ProjectDAO            projectDAO;
	private static WorkLogEntryDAO       workLogEntryDAO;
	private static WorkTracker           workTracker;
	private static WorkTrackerController controller;

	@BeforeClass
	public static void setup() throws SQLException, ClassNotFoundException {
		dbProvider = new DerbyDatabaseProvider("WorkTrackerTest");
		dbMaintainer = new DatabaseMaintainer();
		dbMaintainer.clearTables(dbProvider);
		dbMaintainer.ensureDatabaseIsCorrect(dbProvider);
		projectDAO = new ProjectDAO(dbProvider);
		workLogEntryDAO = new WorkLogEntryDAO(dbProvider, projectDAO);
		Project pauseProject = projectDAO.findByName(Project.PAUSE_PROJECT_NAME);
		workTracker = new WorkTracker(pauseProject);
		controller = new WorkTrackerController(workTracker, projectDAO, workLogEntryDAO);
	}

	@Test
	public void canAddProject() {
		Project project = controller.addProject("Test 1");
		Assert.assertTrue("Project was not added correctly", workTracker.getProjects().contains(project));
		project = projectDAO.findById(project.getId());
		Assert.assertNotNull("Project was not stored in the database", project);
		Assert.assertThat(project.getName(), is("Test 1"));
	}

	@Test
	public void canSwitchProject() throws InterruptedException {
		WorkLogEntry previousEntry = workTracker.getCurrentLogEntry();
		Project oldProject = previousEntry.getProject();
		Project newProject = new Project("New Project");
		Thread.sleep(1000);
		controller.switchProject(newProject);
		Assert.assertThat(previousEntry.isRunning(), is(false));
		Assert.assertThat(workTracker.getCurrentLogEntry().getProject(), is(newProject));
		long id = previousEntry.getId();
		WorkLogEntry entry = workLogEntryDAO.findById(id);
		Assert.assertThat(entry.isRunning(), is(false));
		Assert.assertThat(entry.getProject(), is(oldProject));
	}

	@Test
	public void canSetComment() {
		controller.changeComment("Test");
		Assert.assertThat(workTracker.getCurrentLogEntry().getComment(), is("Test"));
		controller.changeComment("Test 2");
		Assert.assertThat(workTracker.getCurrentLogEntry().getComment(), is("Test 2"));
		WorkLogEntry entry = workLogEntryDAO.findById(workTracker.getCurrentLogEntry().getId());
		Assert.assertThat(entry.getComment(), is("Test 2"));
	}

	@Test
	public void notfiesAllViews() {
		MockView view1 = new MockView();
		controller.registerView(view1);
		MockView view2 = new MockView();
		controller.registerView(view2);
		controller.tick();
		Assert.assertThat(view1.hasBeenUpdated, is(true));
		Assert.assertThat(view2.hasBeenUpdated, is(true));
	}

	private class MockView implements WorkTrackerView {

		private boolean hasBeenUpdated = false;

		@Override
		public void update( WorkTracker workTracker ) {
			hasBeenUpdated = true;
		}
	}

}

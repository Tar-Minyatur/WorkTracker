/******************************************************************************
 * This file is part of WorkTracker, Copyright (c) 2015 Till Helge Helwig.    *
 *                                                                            *
 * WorkTracker is distributed under the MIT License (MIT), so feel free       *
 * to do whatever you want with this code. You may notify the author about    *
 * bugs via http://github.com/Tar-Minyatur/WorkTracker/issues, but be aware   *
 * that he is not (legally) obligated to provide support. You are using       *
 * this software at your own risk.                                            *
 ******************************************************************************/

package de.tshw.worktracker;

import de.tshw.worktracker.controller.WorkTrackerController;
import de.tshw.worktracker.dao.*;
import de.tshw.worktracker.model.Project;
import de.tshw.worktracker.model.WorkTracker;
import de.tshw.worktracker.view.SimpleConsoleView;

import java.sql.SQLException;

public class Main {

	public static void main( String[] args ) throws SQLException, ClassNotFoundException {

		DatabaseProvider dbProvider = new DerbyDatabaseProvider();

		DatabaseMaintainer dbMaintainer = new DatabaseMaintainer();
		dbMaintainer.ensureDatabaseIsCorrect(dbProvider);

		ProjectDAO projectDAO = new ProjectDAO(dbProvider);
		WorkLogEntryDAO workLogEntryDAO = new WorkLogEntryDAO(dbProvider, projectDAO);

		Project pauseProject = projectDAO.findByName(Project.PAUSE_PROJECT_NAME);

		WorkTracker workTracker = new WorkTracker(pauseProject);
		WorkTrackerController controller = new WorkTrackerController(workTracker, projectDAO, workLogEntryDAO);

		controller.registerView(new SimpleConsoleView());

		Project project = controller.addProject("Test 1");
		controller.switchProject(project);
	}

}

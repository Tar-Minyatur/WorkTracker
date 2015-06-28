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

import de.tshw.worktracker.dao.*;
import de.tshw.worktracker.model.Project;
import de.tshw.worktracker.model.WorkLogEntry;
import org.joda.time.LocalDate;

import java.sql.SQLException;
import java.util.List;

public class Main {

	public static void main( String[] args ) throws SQLException, ClassNotFoundException {

		DatabaseProvider dbProvider = new DerbyDatabaseProvider();

		DatabaseMaintainer dbMaintainer = new DatabaseMaintainer();
		dbMaintainer.ensureDatabaseIsCorrect(dbProvider);

		ProjectDAO projectDAO = new ProjectDAO(dbProvider);
		WorkLogEntryDAO workLogEntryDAO = new WorkLogEntryDAO(dbProvider, projectDAO);

		Project project = projectDAO.findByName("Testel 3");
		if ( project == null ) {
			project = new Project("Testel 3");
		}
		WorkLogEntry entry = new WorkLogEntry(project);
		workLogEntryDAO.save(entry);

		List<Project> projects = projectDAO.getAll();

		for ( Project p : projects ) {
			System.out.println("Project: " + p.getName());
		}


		List<WorkLogEntry> entries = workLogEntryDAO.findByDay(LocalDate.now());

		System.out.println("ALL ENTRIES:");
		for ( WorkLogEntry e : entries ) {
			System.out.println(
					"Entry: " + e.getProject().getName() + " von " + e.getStartTime() + " bis " + e.getEndTime() +
					" (" + e.getComment() + ")");
		}

		entries = workLogEntryDAO.findWithoutEndDate();

		System.out.println("INCOMPLETE ENTRIES:");
		for ( WorkLogEntry e : entries ) {
			System.out.println(
					"Entry: " + e.getProject().getName() + " von " + e.getStartTime() + " bis " + e.getEndTime() +
					" (" + e.getComment() + ")");
		}
	}

}

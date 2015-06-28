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

import de.tshw.worktracker.dao.ProjectDAO;
import de.tshw.worktracker.dao.WorkLogEntryDAO;
import de.tshw.worktracker.model.Project;
import de.tshw.worktracker.model.WorkLogEntry;
import de.tshw.worktracker.model.WorkTracker;
import de.tshw.worktracker.view.WorkTrackerView;
import org.joda.time.LocalDate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkTrackerController {

	private WorkTracker          workTracker;
	private ProjectDAO           projectDAO;
	private WorkLogEntryDAO      workLogEntryDAO;
	private boolean              stopTimer;
	private Set<WorkTrackerView> views;

	public WorkTrackerController( WorkTracker workTracker, ProjectDAO projectDAO,
								  WorkLogEntryDAO workLogEntryDAO ) {
		this.workTracker = workTracker;
		this.projectDAO = projectDAO;
		this.workLogEntryDAO = workLogEntryDAO;
		this.views = new HashSet<>();
		findProjects();
		findTodaysEntries();
		findUnfinishedEntries();
		startTimer();
	}

	private void findProjects() {
		projectDAO.getAll().forEach(workTracker::addProject);
	}

	private void findTodaysEntries() {
		List<WorkLogEntry> entries = workLogEntryDAO.findByDay(LocalDate.now());
		entries.forEach(workTracker::addWorkLogEntry);

	}

	private void findUnfinishedEntries() {
		List<WorkLogEntry> entries = workLogEntryDAO.findWithoutEndDate();
		entries.forEach(workTracker::addUnfinishedLogEntries);
	}

	private void startTimer() {
		this.stopTimer = false;
		new Thread(() -> {
			try {
				while ( !stopTimer ) {
					Thread.sleep(1000);
					this.tick();
				}
			}
			catch (InterruptedException ex) {
				System.err.println("Timer thread was interrupted while sleeping. (Error: " + ex.getMessage() + ")");
			}
		}).start();
	}

	void tick() {
		for ( WorkTrackerView view : views ) {
			view.update(workTracker);
		}
	}

	public Project addProject( String name ) {
		Project project = projectDAO.findByName(name);
		if ( project == null ) {
			project = new Project(name);
			projectDAO.save(project);
		}
		workTracker.addProject(project);
		tick();
		return project;
	}

	public void switchProject( Project project ) {
		WorkLogEntry entry = workTracker.getCurrentLogEntry();
		if ( project != entry.getProject() ) {
			entry.stop();
			if ( entry.getTimeElapsed().getSeconds() < 1 ) {
				workLogEntryDAO.delete(entry);
				workTracker.removeWorkLogEntry(entry);
			} else {
				workLogEntryDAO.save(entry);
			}
			if ( !workTracker.getProjects().contains(project) ) {
				workTracker.addProject(project);
			}
			entry = new WorkLogEntry(project);
			workLogEntryDAO.save(entry);
			workTracker.setCurrentLogEntry(entry);
		}
		tick();
	}

	public void changeComment( String comment ) {
		workTracker.getCurrentLogEntry().setComment(comment);
		workLogEntryDAO.save(workTracker.getCurrentLogEntry());
		tick();
	}

	public void registerView( WorkTrackerView view ) {
		views.add(view);
	}

	public void quit() {
		stopTimer = true;
		WorkLogEntry currentLogEntry = workTracker.getCurrentLogEntry();
		currentLogEntry.stop();
		workLogEntryDAO.save(currentLogEntry);
	}
}
/******************************************************************************
 * This file is part of WorkTracker, Copyright (c) 2015 Till Helge Helwig.    *
 *                                                                            *
 * WorkTracker is distributed under the MIT License (MIT), so feel free       *
 * to do whatever you want with this code. You may notify the author about    *
 * bugs via http://github.com/Tar-Minyatur/WorkTracker/issues, but be aware   *
 * that he is not (legally) obligated to provide support. You are using       *
 * this software at your own risk.                                            *
 ******************************************************************************/

package de.tshw.worktracker.controller;

import de.tshw.worktracker.dao.ProjectDAO;
import de.tshw.worktracker.dao.WorkLogEntryDAO;
import de.tshw.worktracker.model.Project;
import de.tshw.worktracker.model.WorkLogEntry;
import de.tshw.worktracker.model.WorkTracker;
import de.tshw.worktracker.view.WorkTrackerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkTrackerController implements Runnable {

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
		findUnfinishedEntries();
		startTimer();
	}

	private void findUnfinishedEntries() {
		List<WorkLogEntry> entries = workLogEntryDAO.findWithoutEndDate();
		for ( WorkLogEntry entry : entries ) {
			workTracker.addUnfininishedLogEntries(entry);
		}
	}

	private void startTimer() {
		this.stopTimer = false;
		Thread timerThread = new Thread(this);
		timerThread.start();
	}

	public Project addProject( String name ) {
		Project project = projectDAO.findByName(name);
		if ( project == null ) {
			project = new Project(name);
			projectDAO.save(project);
		}
		workTracker.getProjects().add(project);
		return project;
	}

	public void switchProject( Project project ) {
		WorkLogEntry entry = workTracker.getCurrentLogEntry();
		entry.stop();
		workLogEntryDAO.save(entry);
		if ( !workTracker.getProjects().contains(project) ) {
			workTracker.getProjects().add(project);
		}
		entry = new WorkLogEntry(project);
		workLogEntryDAO.save(entry);
		workTracker.setCurrentLogEntry(entry);
	}

	public void changeComment( String comment ) {
		workTracker.getCurrentLogEntry().setComment(comment);
		workLogEntryDAO.save(workTracker.getCurrentLogEntry());
	}

	@Override
	public void run() {
		try {
			while ( !stopTimer ) {
				Thread.sleep(1000);
				this.tick();
			}
		}
		catch (InterruptedException ex) {
			System.err.println("Timer thread was interrupted while sleeping. (Error: " + ex.getMessage() + ")");
		}
	}

	void tick() {
		for ( WorkTrackerView view : views ) {
			view.update(workTracker);
		}
	}

	public void registerView( WorkTrackerView view ) {
		views.add(view);
	}
}
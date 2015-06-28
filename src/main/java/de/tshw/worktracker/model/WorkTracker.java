/******************************************************************************
 * This file is part of WorkTracker, Copyright (c) 2015 Till Helge Helwig.    *
 *                                                                            *
 * WorkTracker is distributed under the MIT License (MIT), so feel free       *
 * to do whatever you want with this code. You may notify the author about    *
 * bugs via http://github.com/Tar-Minyatur/WorkTracker/issues, but be aware   *
 * that he is not (legally) obligated to provide support. You are using       *
 * this software at your own risk.                                            *
 ******************************************************************************/

package de.tshw.worktracker.model;

import java.util.HashSet;
import java.util.Set;

public class WorkTracker {

	private final Project           pauseProject;
	private       Set<Project>      projects;
	private       WorkLogEntry      currentLogEntry;
	private       Set<WorkLogEntry> unfininishedLogEntries;

	public WorkTracker( Project pauseProject ) {
		this.projects = new HashSet<>();
		this.pauseProject = pauseProject;
		this.currentLogEntry = new WorkLogEntry(pauseProject);
		this.unfininishedLogEntries = new HashSet<>();
	}

	public WorkLogEntry getCurrentLogEntry() {
		return currentLogEntry;
	}

	public void setCurrentLogEntry( WorkLogEntry currentLogEntry ) {
		this.currentLogEntry = currentLogEntry;
	}

	public void addProject( Project project ) {
		this.projects.add(project);
	}

	public Set<Project> getProjects() {
		return projects;
	}

	public Set<WorkLogEntry> getUnfininishedLogEntries() {
		return unfininishedLogEntries;
	}

	public void addUnfininishedLogEntries( WorkLogEntry entry ) {
		unfininishedLogEntries.add(entry);
	}

	public Project getPauseProject() {
		return pauseProject;
	}
}

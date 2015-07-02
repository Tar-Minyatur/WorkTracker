/******************************************************************************
 * This file is part of WorkTracker, Copyright (c) 2015 Till Helge Helwig.    *
 *                                                                            *
 * WorkTracker is distributed under the MIT License, so feel free to do       *
 * whatever you want with application or code. You may notify the author      *
 * about bugs via http://github.com/Tar-Minyatur/WorkTracker/issues, but      *
 * be aware that he is not (legally) obligated to provide support. You are    *
 * using this software at your own risk.                                      *
 ******************************************************************************/

package de.tshw.worktracker.model;

import java.util.*;

public class WorkTracker {

	public static final String VERSION = "1.0-preview2";

	private final Project                 pauseProject;
	private       SortedSet<Project>      projects;
	private       WorkLogEntry            currentLogEntry;
	private       Set<WorkLogEntry>       unfinishedLogEntries;
	private       SortedSet<WorkLogEntry> todaysWorkLogEntries;

	public WorkTracker( Project pauseProject ) {
		this.projects = new TreeSet<>();
		this.pauseProject = pauseProject;
		this.currentLogEntry = new WorkLogEntry(pauseProject);
		this.unfinishedLogEntries = new HashSet<>();
		this.todaysWorkLogEntries = new TreeSet<>();
		this.todaysWorkLogEntries.add(this.currentLogEntry);
	}

	public WorkLogEntry getCurrentLogEntry() {
		return currentLogEntry;
	}

	public void setCurrentLogEntry( WorkLogEntry entry ) {
		this.currentLogEntry = entry;
		todaysWorkLogEntries.add(entry);
	}

	public void addProject( Project project ) {
		this.projects.add(project);
	}

	public Set<Project> getProjects() {
		return Collections.unmodifiableSortedSet(projects);
	}

	public Set<WorkLogEntry> getUnfinishedLogEntries() {
		return unfinishedLogEntries;
	}

	public void addUnfinishedLogEntries( WorkLogEntry entry ) {
		unfinishedLogEntries.add(entry);
	}

	public Project getPauseProject() {
		return pauseProject;
	}

	public void addWorkLogEntry( WorkLogEntry entry ) {
		todaysWorkLogEntries.add(entry);
	}

	public SortedSet<WorkLogEntry> getTodaysWorkLogEntries() {
		return Collections.unmodifiableSortedSet(todaysWorkLogEntries);
	}

	public void removeWorkLogEntry( WorkLogEntry entry ) {
		todaysWorkLogEntries.remove(entry);
	}
}

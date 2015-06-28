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

	private Set<Project> projects;
	private WorkLogEntry currentLogEntry;

	public WorkTracker() {
		this.projects = new HashSet<Project>();
		this.currentLogEntry = null;
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

	public WorkLogEntry switchProject( Project project ) {
		if ( this.currentLogEntry != null ) {
			this.currentLogEntry.stop();
		}

		WorkLogEntry newLogEntry = new WorkLogEntry(project);
		this.currentLogEntry = newLogEntry;

		return newLogEntry;
	}
}

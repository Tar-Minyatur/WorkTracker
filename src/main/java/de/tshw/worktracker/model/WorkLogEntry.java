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

import org.joda.time.LocalDateTime;

public class WorkLogEntry extends Entity {

	private Project       project;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String        comment;

	public WorkLogEntry() {
		super();
		this.project = null;
		this.startTime = LocalDateTime.now();
		this.endTime = null;
		this.comment = null;
	}

	public WorkLogEntry( Project project ) {
		super();
		this.project = project;
		this.startTime = LocalDateTime.now();
		this.endTime = null;
		this.comment = null;
	}

	public WorkLogEntry( Project project, LocalDateTime startTime, LocalDateTime endTime, String comment ) {
		super();
		this.project = project;
		this.startTime = startTime;
		this.endTime = endTime;
		this.comment = comment;
	}

	public Project getProject() {
		return project;
	}

	public void setProject( Project project ) {
		this.project = project;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime( LocalDateTime startTime ) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime( LocalDateTime endTime ) {
		this.endTime = endTime;
	}

	public boolean isRunning() {
		return ( this.endTime == null );
	}

	public String getComment() {
		return comment;
	}

	public void setComment( String comment ) {
		this.comment = comment;
	}

	public void stop() {
		this.endTime = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return this.project.getName() + " (" + startTime + " - " + ( ( endTime == null ) ? "?" : endTime ) + ": " +
			   comment;
	}
}

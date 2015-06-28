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

import org.joda.time.LocalDateTime;
import org.joda.time.Period;

public class WorkLogEntry extends Entity implements Comparable<WorkLogEntry> {

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

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime( LocalDateTime endTime ) {
		this.endTime = endTime;
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

	public Period getTimeElapsed() {
		Period period = new Period();
		if ( !isRunning() ) {
			period = new Period(startTime, endTime);
		}
		return period;
	}

	public boolean isRunning() {
		return ( this.endTime == null );
	}

	@Override
	public String toString() {
		return this.project.getName() + " (" + startTime + " - " + ( ( endTime == null ) ? "?" : endTime ) + ": " +
			   comment;
	}

	@Override
	public int compareTo( WorkLogEntry o ) {
		return startTime.compareTo(o.getStartTime());
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime( LocalDateTime startTime ) {
		this.startTime = startTime;
	}
}

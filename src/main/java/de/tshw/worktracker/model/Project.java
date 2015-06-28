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

public class Project extends Entity implements Comparable<Project> {

	public static final String PAUSE_PROJECT_NAME = "Idling / Break";

	private String name;

	public Project() {
		super();
	}

	public Project( String name ) {
		super();
		this.name = name;
	}

	@Override
	public int compareTo( Project project ) {
		if ( project == null ) {
			return 1;
		} else if ( project.equals(this) ) {
			return 0;
		} else if ( name.equals(PAUSE_PROJECT_NAME) ) {
			return -1;
		} else if ( project.getName().equals(PAUSE_PROJECT_NAME) ) {
			return 1;
		} else {
			return name.compareTo(project.getName());
		}
	}

	@Override
	public boolean equals( Object obj ) {
		if ( obj instanceof Project ) {
			Project project = (Project) obj;
			if ( ( project.getId() != null ) && ( getId() != null ) ) {
				return project.getId().equals(getId());
			} else {
				return ( project == this ) || ( project.getName().equals(getName()) );
			}
		} else {
			return false;
		}
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}

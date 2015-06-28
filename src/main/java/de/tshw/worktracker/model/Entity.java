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

/**
 * Simple abstraction for an entity stored in the database.
 * <p>
 * Inherit from this class and create a DAO inheriting from {@link de.tshw.worktracker.dao.DataAccessObject} to setup
 * a simple and straight-forward database read and write access for your object.
 * <p>
 * The system will automatically take care of IDs, so just add the fields you need. All of the mapping will
 * be done directly in the DAO.
 */
public abstract class Entity {

	private Long id;

	public Entity() {
		this.id = null;
	}

	public Long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
	}

	public void clearId() {
		this.id = null;
	}
}

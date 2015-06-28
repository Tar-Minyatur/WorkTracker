/******************************************************************************
 * This file is part of WorkTracker, Copyright (c) 2015 Till Helge Helwig.    *
 *                                                                            *
 * WorkTracker is distributed under the MIT License, so feel free to do       *
 * whatever you want with application or code. You may notify the author      *
 * about bugs via http://github.com/Tar-Minyatur/WorkTracker/issues, but      *
 * be aware that he is not (legally) obligated to provide support. You are    *
 * using this software at your own risk.                                      *
 ******************************************************************************/

package de.tshw.worktracker.dao;

import de.tshw.worktracker.model.WorkLogEntry;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkLogEntryDAO extends DataAccessObject<WorkLogEntry> {

	private final ProjectDAO projectDAO;

	public WorkLogEntryDAO( DatabaseProvider dbProvider, ProjectDAO projectDAO ) {
		super(dbProvider);
		this.projectDAO = projectDAO;
	}

	public List<WorkLogEntry> findByDay( LocalDate day ) {
		List<WorkLogEntry> entries = new ArrayList<>();
		try {
			Connection connection = getDbProvider().getDatabaseConnection();
			PreparedStatement statement = connection.prepareStatement(getSelectByDayStatement());
			statement.setDate(1, new Date(day.toDate().getTime()));
			statement.setDate(2, new Date(day.toDate().getTime()));
			ResultSet result = statement.executeQuery();
			while ( result.next() ) {
				WorkLogEntry entry = getEntityFromResultSet(result);
				entries.add(entry);
			}
		}
		catch (Exception ex) {
			System.err.println("Failed to retrieve WorkLogEntries by day (Error: " + ex.getMessage() + ")");
		}
		return entries;
	}

	private String getSelectByDayStatement() {
		return "SELECT " + this.getIdColumnName() + ", " + String.join(", ", this.getFieldList()) +
			   " FROM " + this.getTableName() +
			   " WHERE DATE(start_time) = ? OR DATE(end_time) = ?";
	}

	@Override
	protected String[] getFieldList() {
		return new String[]{ "project_id", "start_time", "end_time", "comment" };
	}

	@Override
	protected String getDefaultOrderBy() {
		return "start_time ASC";
	}

	@Override
	protected WorkLogEntry mapFieldsToEntity( ResultSet result ) {
		WorkLogEntry entry = null;
		try {
			entry = new WorkLogEntry(projectDAO.findById(result.getLong("project_id")));
			entry.setStartTime(LocalDateTime.fromDateFields(result.getTimestamp("start_time")));
			if ( result.getDate("end_time") != null ) {
				entry.setEndTime(LocalDateTime.fromDateFields(result.getTimestamp("end_time")));
			}
			entry.setComment(result.getString("comment"));
		}
		catch (SQLException ex) {
			System.err.println("Error while mapping WorkLogEntry entity: " + ex.getMessage());
		}
		return entry;
	}

	@Override
	protected String getEntityName() {
		return WorkLogEntry.class.getSimpleName();
	}

	@Override
	protected void setInsertUpdateParameters( PreparedStatement statement, WorkLogEntry entity ) throws SQLException {
		statement.setLong(1, entity.getProject().getId());
		statement.setTimestamp(2, new Timestamp(entity.getStartTime().toDate().getTime()));
		if ( entity.getEndTime() == null ) {
			statement.setNull(3, Types.DATE);
		} else {
			statement.setTimestamp(3, new Timestamp(entity.getEndTime().toDate().getTime()));
		}
		if ( entity.getComment() == null ) {
			statement.setNull(4, Types.VARCHAR);
		} else {
			statement.setString(4, entity.getComment());
		}
	}

	@Override
	protected void ensureForeignKeyConsistency( WorkLogEntry entity ) {
		this.projectDAO.save(entity.getProject());
	}

	public List<WorkLogEntry> findWithoutEndDate() {
		List<WorkLogEntry> entries = new ArrayList<>();
		try {
			Connection connection = getDbProvider().getDatabaseConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(getSelectWithoutEndDateStatement());
			while ( result.next() ) {
				WorkLogEntry entry = getEntityFromResultSet(result);
				entries.add(entry);
			}
		}
		catch (Exception ex) {
			System.err.println("Failed to retrieve WorkLogEntries without end date (Error: " + ex.getMessage() + ")");
		}
		return entries;
	}

	private String getSelectWithoutEndDateStatement() {
		return "SELECT " + this.getIdColumnName() + ", " + String.join(", ", this.getFieldList()) +
			   " FROM " + this.getTableName() +
			   " WHERE end_time IS NULL";
	}
}

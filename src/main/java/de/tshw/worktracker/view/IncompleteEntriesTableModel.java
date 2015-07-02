/******************************************************************************
 * This file is part of WorkTracker, Copyright (c) 2015 Till Helge Helwig.    *
 *                                                                            *
 * WorkTracker is distributed under the MIT License, so feel free to do       *
 * whatever you want with application or code. You may notify the author      *
 * about bugs via http://github.com/Tar-Minyatur/WorkTracker/issues, but      *
 * be aware that he is not (legally) obligated to provide support. You are    *
 * using this software at your own risk.                                      *
 ******************************************************************************/

package de.tshw.worktracker.view;

import de.tshw.worktracker.model.Project;
import de.tshw.worktracker.model.WorkLogEntry;
import de.tshw.worktracker.model.WorkTracker;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class IncompleteEntriesTableModel extends AbstractTableModel {

	private final static Class<?>[] COLUMN_TYPES =
			new Class<?>[]{ Project.class, String.class, String.class };
	private static       ResourceBundle resourceBundle =
			ResourceBundle.getBundle("i18n/swingView");
	private final static String[]       COLUMN_NAMES   =
			new String[]{ resourceBundle.getString("column.project"),
						  resourceBundle.getString("column.date"),
						  resourceBundle.getString("column.start.time") };
	private WorkTracker workTracker;
	private DateTimeFormatter dateFormatter      =
			DateTimeFormat.forPattern(resourceBundle.getString("date.pattern.day.month"));
	private DateTimeFormatter startTimeFormatter =
			DateTimeFormat.forPattern(resourceBundle.getString("date.pattern.time"));

	public IncompleteEntriesTableModel( WorkTracker workTracker ) {
		this.workTracker = workTracker;
	}

	@Override
	public int getRowCount() {
		return workTracker.getUnfinishedLogEntries().size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt( int rowIndex, int columnIndex ) {
		List<WorkLogEntry> unfinishedLogEntries = new ArrayList<>(workTracker.getUnfinishedLogEntries());
		Object value = null;
		if ( columnIndex == 0 ) {
			value = unfinishedLogEntries.get(rowIndex).getProject();
		} else if ( columnIndex == 1 ) {
			value = dateFormatter.print(unfinishedLogEntries.get(rowIndex).getStartTime());
		} else if ( columnIndex == 2 ) {
			value = startTimeFormatter.print(unfinishedLogEntries.get(rowIndex).getStartTime());
		}
		return value;
	}

	@Override
	public String getColumnName( int columnIndex ) {
		return COLUMN_NAMES[columnIndex];
	}

	@Override
	public Class<?> getColumnClass( int columnIndex ) {
		return COLUMN_TYPES[columnIndex];
	}

	@Override
	public boolean isCellEditable( int rowIndex, int columnIndex ) {
		return false;
	}

	public void update( WorkTracker workTracker ) {
		this.fireTableDataChanged();
	}
}
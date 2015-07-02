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
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.MutablePeriod;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class TimeEntriesTableModel extends AbstractTableModel {

	private final static Class<?>[] COLUMN_TYPES = new Class<?>[]{ Project.class, String.class };
	private static       ResourceBundle resourceBundle =
			ResourceBundle.getBundle("i18n/swingView");
	private final static String[]       COLUMN_NAMES   =
			new String[]{ resourceBundle.getString("column.project"),
						  resourceBundle.getString("column.time") };
	private WorkTracker          workTracker;
	private Map<Project, Period> elapsedTimes;
	private PeriodFormatter      periodFormatter;
	private Period               totalTimeElapsedToday;

	public TimeEntriesTableModel( WorkTracker workTracker ) {
		this.workTracker = workTracker;
		this.elapsedTimes = new HashMap<>();
		this.periodFormatter =
				new PeriodFormatterBuilder().printZeroAlways().appendHours().appendSuffix(":").minimumPrintedDigits(2)
											.appendMinutes().appendSuffix(":").appendSeconds().toFormatter();
	}

	@Override
	public int getRowCount() {
		return workTracker.getProjects().size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt( int rowIndex, int columnIndex ) {
		Object value = null;
		Set<Project> projects = workTracker.getProjects();
		if ( columnIndex == 0 ) {
			value = projects.toArray(new Project[projects.size()])[rowIndex];
		} else if ( columnIndex == 1 ) {
			Project project = projects.toArray(new Project[projects.size()])[rowIndex];
			Period period = elapsedTimes.get(project);
			if ( period == null ) {
				value = "0:00:00";
			} else {
				value = periodFormatter.print(period.normalizedStandard());
			}
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

	public String update( WorkTracker workTracker ) {
		int oldProjectCount = elapsedTimes.size();
		HashMap<Project, MutablePeriod> newTimes = new HashMap<>();
		for ( Project p : workTracker.getProjects() ) {
			newTimes.put(p, new MutablePeriod());
		}
		MutablePeriod totalTimeToday = new MutablePeriod();
		for ( WorkLogEntry entry : workTracker.getTodaysWorkLogEntries() ) {
			if ( !newTimes.containsKey(entry.getProject()) ) {
				newTimes.put(entry.getProject(), new MutablePeriod());
			}
			if ( entry.getStartTime().toLocalDate().toDateTimeAtStartOfDay().equals(
					LocalDate.now().toDateTimeAtStartOfDay()) ) {
				newTimes.get(entry.getProject()).add(entry.getTimeElapsed());
				if ( !entry.getProject().equals(workTracker.getPauseProject()) ) {
					totalTimeToday.add(entry.getTimeElapsed());
				}
			}
		}
		WorkLogEntry entry = workTracker.getCurrentLogEntry();
		Period period = new Period(entry.getStartTime(), LocalDateTime.now());
		newTimes.get(entry.getProject()).add(period);
		if ( !entry.getProject().equals(workTracker.getPauseProject()) ) {
			totalTimeToday.add(period);
		}

		for ( Project p : newTimes.keySet() ) {
			elapsedTimes.put(p, newTimes.get(p).toPeriod());
		}

		this.totalTimeElapsedToday = totalTimeToday.toPeriod();
		if ( oldProjectCount == elapsedTimes.size() ) {
			this.fireTableRowsUpdated(0, elapsedTimes.size());
		} else {
			this.fireTableDataChanged();
		}

		return periodFormatter.print(this.totalTimeElapsedToday.normalizedStandard());
	}
}
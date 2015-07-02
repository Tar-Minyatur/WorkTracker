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

import de.tshw.worktracker.model.WorkTracker;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class SimpleConsoleView implements WorkTrackerView {

	@Override
	public void update( WorkTracker workTracker ) {
		LocalDateTime startTime = workTracker.getCurrentLogEntry().getStartTime();
		Period timeElapsed = new Period(startTime, LocalDateTime.now());
		PeriodFormatter formatter = new PeriodFormatterBuilder().appendDays().appendSuffix(
				"d").appendPrefix(" ").appendHours().appendSuffix("h").appendPrefix(" ").appendMinutes().appendSuffix(
				"m").appendPrefix(" ").appendSeconds().appendSuffix("s")
																.toFormatter();
		String formattedTimeElapsed = formatter.print(timeElapsed);
		if ( formattedTimeElapsed.equals("") ) {
			formattedTimeElapsed = " 0s";
		}
		System.out.print("\rCurrent Project: " + workTracker.getCurrentLogEntry().getProject() + " - Time elapsed: " +
						 formattedTimeElapsed);
	}

}

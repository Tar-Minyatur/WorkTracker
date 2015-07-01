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

import java.awt.*;

public class SwingViewHelper {

	public SwingViewHelper() {
	}

	void flashComponent( final Component component ) {
		component.setVisible(true);
		new Thread(() -> {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e1) {
				// There is really nothing to do here
			}
			finally {
				component.setVisible(false);
			}
		}).start();
	}
}
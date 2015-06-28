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

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

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

	void showAboutDialog( Component parent, SwingView swingView ) {
		URL aboutScreenUrl = swingView.getClass().getResource("/html/about.html");
		if ( aboutScreenUrl != null ) {
			try {
				File file = new File(aboutScreenUrl.toURI());
				FileInputStream stream = new FileInputStream(file);
				byte[] data = new byte[(int) file.length()];
				stream.read(data);
				stream.close();
				String aboutHTML = new String(data, "UTF-8");
				aboutHTML = aboutHTML.replace("%version%", WorkTracker.VERSION);
				aboutHTML = aboutHTML.replace("\n", "");
				aboutHTML = aboutHTML.replace("\t", "");
				URL iconUrl = swingView.getClass().getResource("/icons/logo.png");
				ImageIcon icon = null;
				if ( iconUrl != null ) {
					icon = new ImageIcon(iconUrl);
					int newWidth = 150;
					int newHeight = Math.round(
							icon.getIconHeight() * ( (float) newWidth / (float) icon.getIconWidth() ));
					Image newImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
					icon = new ImageIcon(newImage);
				}
				JOptionPane.showMessageDialog(parent,
											  aboutHTML, "Info!",
											  JOptionPane.INFORMATION_MESSAGE, icon);
			}
			catch (Exception ex) {
				System.err.println("Something failed:_" + ex.getMessage());
			}
		}
	}
}
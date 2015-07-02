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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

public class SwingViewAboutDialog extends JDialog {

	private static final String RESOURCE_BUNDLE = "i18n/swingViewAbout";

	private JPanel  contentPane;
	private JButton buttonOK;
	private JLabel  jodaTimeLabel;
	private JLabel  famfamfamLabel;
	private JLabel  homepageLabel;
	private JLabel  authorLabel;
	private JLabel versionLabel;

	private Desktop desktop;

	public SwingViewAboutDialog( JFrame frame ) {
		super(frame);

		ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);

		if ( Desktop.isDesktopSupported() ) {
			desktop = Desktop.getDesktop();
		}

		updateInformation();
		setupActions();

		setContentPane(contentPane);
		setModal(true);
		setResizable(false);
		getRootPane().setDefaultButton(buttonOK);
		setTitle(resourceBundle.getString("title"));
		pack();
	}

	private void updateInformation() {
		versionLabel.setText(WorkTracker.VERSION);
	}

	private void setupActions() {
		buttonOK.addActionListener(( ActionEvent e ) -> onOK());
		MouseListener listener = new MouseAdapter() {
			@Override
			public void mouseClicked( MouseEvent e ) {
				if ( e.getSource() == homepageLabel ) {
					browseTo("http://tar-minyatur.github.io");
				} else if ( e.getSource() == jodaTimeLabel ) {
					browseTo("http://www.joda.org/joda-time/");
				} else if ( e.getSource() == famfamfamLabel ) {
					browseTo("http://famfamfam.com/lab/icons/silk/");
				} else if ( e.getSource() == authorLabel ) {
					sendMailTo("thh@tshw.de");
				} else {
					super.mouseClicked(e);
				}
			}
		};
		homepageLabel.addMouseListener(listener);
		jodaTimeLabel.addMouseListener(listener);
		famfamfamLabel.addMouseListener(listener);
		authorLabel.addMouseListener(listener);
	}

	private void onOK() {
		dispose();
	}

	private void browseTo( String uri ) {
		if ( ( desktop != null ) && ( desktop.isSupported(Desktop.Action.BROWSE) ) ) {
			try {
				desktop.browse(new URI(uri));
			}
			catch (URISyntaxException | IOException e) {
				// If this doesn't work, it can't be helped
			}
		}
	}

	private void sendMailTo( String email ) {
		if ( ( desktop != null ) && ( desktop.isSupported(Desktop.Action.MAIL) ) ) {
			try {
				desktop.mail(new URI(email));
			}
			catch (URISyntaxException | IOException e) {
				// If this doesn't work, it can't be helped
			}
		}
	}
}

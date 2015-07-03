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

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class JComponentWithActionFeedback extends JComponent {

	private static final String DEFAULT_CONFIRMATION_ICON_PATH = "/icons/confirmed.png";
	private static final String DEFAULT_ERROR_ICON_PATH        = "/icons/exclamation.png";

	private JComponent  component;
	private ImageIcon   confirmationIcon;
	private ImageIcon   errorIcon;
	private JLabel      icon;
	private Action      action;
	private KeyListener keyListener;

	public JComponentWithActionFeedback() {
		this.component = null;
		this.action = null;
		this.setupComponent();
	}

	private void setupComponent() {
		this.setLayout(new BorderLayout());
		confirmationIcon = new ImageIcon(getClass().getResource(DEFAULT_CONFIRMATION_ICON_PATH));
		errorIcon = new ImageIcon(getClass().getResource(DEFAULT_ERROR_ICON_PATH));
		icon = new JLabel();
		icon.setVisible(false);
		icon.setOpaque(true);
		this.add(icon, BorderLayout.EAST);

		this.keyListener = new KeyAdapter() {
			@Override
			public void keyReleased( KeyEvent e ) {
				super.keyReleased(e);
				if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
					performAction();
				}
			}
		};
	}

	private void performAction() {
		if ( this.action != null ) {
			if ( this.action.execute(this.component) ) {
				showConfirmation();
			} else {
				showError();
			}
		}
	}

	private void showConfirmation() {
		icon.setIcon(confirmationIcon);
		flashIcon();
	}

	private void showError() {
		icon.setIcon(errorIcon);
		flashIcon();
	}

	private void flashIcon() {
		icon.setVisible(true);
		new Thread(() -> {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e1) {
				// There is really nothing to do here
			}
			finally {
				icon.setVisible(false);
			}
		}).start();
	}

	public void add( JComponent component ) {
		if ( this.component != null ) {
			this.component.setBorder(this.getBorder());
			this.component.removeKeyListener(keyListener);
			this.remove(this.component);
		}
		this.component = component;
		this.add(component, BorderLayout.CENTER);
		this.setBackground(this.component.getBackground());
		this.icon.setBackground(this.component.getBackground());
		this.setBorder(this.component.getBorder());
		this.component.setBorder(null);
		this.component.addKeyListener(keyListener);
	}

	public JComponent getComponent() {
		return component;
	}

	public void setComponent( JComponent component ) {
		this.component = component;
	}

	public Action getAction() {
		return action;
	}

	public void setAction( Action action ) {
		this.action = action;
	}

	public void setConfirmationIcon( ImageIcon confirmationIcon ) {
		this.confirmationIcon = confirmationIcon;
	}

	public void setErrorIcon( ImageIcon errorIcon ) {
		this.errorIcon = errorIcon;
	}

	public interface Action {

		boolean execute( JComponent component );
	}
}

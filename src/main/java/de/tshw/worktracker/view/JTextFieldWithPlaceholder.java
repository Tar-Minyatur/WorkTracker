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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class JTextFieldWithPlaceholder extends JTextField {

	private static final Color disabledTextColor;
	private static final Color activeTextColor;

	static {
		UIDefaults uiDefaults = UIManager.getDefaults();
		disabledTextColor = uiDefaults.getColor("Label.disabledForeground");
		activeTextColor = uiDefaults.getColor("TextField.foreground");
	}

	private String  placeholder;
	private boolean fieldIsEmpty;

	public JTextFieldWithPlaceholder( String placeholder ) {
		super("");
		this.placeholder = placeholder;
		this.fieldIsEmpty = true;
		this.showPlaceholder(true);
		this.setupComponent();
	}

	private void showPlaceholder( boolean show ) {
		if ( show ) {
			super.setText(this.placeholder);
			this.setForeground(disabledTextColor);
		} else {
			if ( fieldIsEmpty ) {
				super.setText("");
			}
			this.setForeground(activeTextColor);
		}
	}

	private void setupComponent() {
		this.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained( FocusEvent e ) {
				super.focusGained(e);
				updatePlaceholderStatus();
			}

			@Override
			public void focusLost( FocusEvent e ) {
				super.focusLost(e);
				setFieldIsEmpty(getEnteredText().length() == 0);
				updatePlaceholderStatus();
			}
		});
	}

	private void updatePlaceholderStatus() {
		if ( this.fieldIsEmpty ) {
			this.showPlaceholder(!this.hasFocus());
		} else {
			this.showPlaceholder(false);
		}
	}

	private void setFieldIsEmpty( boolean fieldIsEmpty ) {
		this.fieldIsEmpty = fieldIsEmpty;
	}

	private String getEnteredText() {
		return super.getText();
	}

	@Override
	public String getText() {
		return fieldIsEmpty ? "" : getEnteredText();
	}

	public JTextFieldWithPlaceholder( String text, String placeholder ) {
		super(text);
		this.fieldIsEmpty = false;
		this.placeholder = placeholder;
		this.showPlaceholder(( text.length() == 0 ));
		this.setupComponent();
	}

	@Override
	public void setText( String text ) {
		super.setText(text);
		setFieldIsEmpty(( text == null ) || ( text.length() == 0 ));
		updatePlaceholderStatus();
	}


}

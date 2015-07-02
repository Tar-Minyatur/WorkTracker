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

import de.tshw.worktracker.controller.WorkTrackerController;
import de.tshw.worktracker.model.Project;
import de.tshw.worktracker.model.WorkTracker;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SwingView implements WorkTrackerView {

	private static final String RESOURCE_BUNDLE = "i18n/swingView";
	private final TimeEntriesTableModel       entriesTableModel;
	private final IncompleteEntriesTableModel incompleteEntriesTableModel;
	private final SwingViewHelper swingViewHelper = new SwingViewHelper();
	private JTable                entriesTable;
	private JButton               addProjectButton;
	private JScrollPane           tableScrollPane;
	private JToolBar              bottomToolBar;
	private JPanel                incompleteEntriesPanel;
	private JLabel                incompleteEntriesHint;
	private JTable                incompleteEntriesTable;
	private JPanel                workTrackerPanel;
	private JButton               deleteIncompleteEntryButton;
	private JButton               editIncompleteEntryButton;
	private JLabel                totalTimeTodayLabel;
	private JTextField            commentTextField;
	private JButton               quitButton;
	private JButton               infoButton;
	private JButton               dataBrowserButton;
	private JLabel                commentConfirmLabel;
	private JLabel                commentFieldPlaceholder;
	private JPanel                commentFieldPanel;
	private WorkTracker           workTracker;
	private WorkTrackerController controller;
	private JFrame                frame;

	private ResourceBundle resourceBundle;

	public SwingView( WorkTracker workTracker, WorkTrackerController controller ) {
		this.workTracker = workTracker;
		this.controller = controller;
		controller.registerView(this);

		entriesTableModel = new TimeEntriesTableModel(workTracker);
		this.entriesTable.setModel(entriesTableModel);
		incompleteEntriesTableModel = new IncompleteEntriesTableModel(workTracker);
		this.incompleteEntriesTable.setModel(incompleteEntriesTableModel);

		commentTextField.setBorder(null);
		commentFieldPanel.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

		resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);

		configureTables(workTracker, controller);
		setupIncompleteEntryActions();
		setupEntriesTableActions();
		setupCommentTextFieldActions();
		setupBottomToolBarActions();
		showView();
	}

	private void configureTables( WorkTracker workTracker, WorkTrackerController controller ) {
		entriesTable.setRowHeight(20);

		entriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		entriesTable.getColumnModel().getColumn(1).setResizable(false);
		entriesTable.getColumnModel().getColumn(1).setMaxWidth(70);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

		ProjectTableCellRenderer projectRenderer = new ProjectTableCellRenderer(workTracker);
		entriesTable.setDefaultRenderer(String.class, rightRenderer);
		entriesTable.setDefaultRenderer(Project.class, projectRenderer);

		incompleteEntriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		incompleteEntriesTable.getColumnModel().getColumn(1).setResizable(false);
		incompleteEntriesTable.getColumnModel().getColumn(1).setMaxWidth(50);
		incompleteEntriesTable.getColumnModel().getColumn(2).setResizable(false);
		incompleteEntriesTable.getColumnModel().getColumn(2).setMaxWidth(70);
		incompleteEntriesTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
	}

	private void setupIncompleteEntryActions() {
		deleteIncompleteEntryButton.addActionListener(( ActionEvent e ) -> {
			JOptionPane.showMessageDialog(workTrackerPanel,
										  resourceBundle.getString("missing.feature"),
										  resourceBundle.getString("missing.feature.title"), JOptionPane.ERROR_MESSAGE);
		});

		editIncompleteEntryButton.addActionListener(( ActionEvent e ) -> {
			JOptionPane.showMessageDialog(workTrackerPanel,
										  resourceBundle.getString("missing.feature"),
										  resourceBundle.getString("missing.feature.title"), JOptionPane.ERROR_MESSAGE);
		});
	}

	private void setupEntriesTableActions() {
		entriesTable.getSelectionModel().addListSelectionListener(( ListSelectionEvent e ) -> {
			if ( !e.getValueIsAdjusting() ) {
				int index = entriesTable.getSelectedRow();
				if ( ( index < entriesTable.getRowCount() ) && ( index >= 0 ) ) {
					Project project = (Project) entriesTable.getValueAt(index, 0);
					this.controller.switchProject(project);
				}
			}
		});
	}

	private void setupCommentTextFieldActions() {
		// TODO Extract this special text field into its own class and encapsulate it properly
		commentFieldPanel.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained( FocusEvent e ) {
				commentTextField.grabFocus();
				commentFieldPlaceholder.setVisible(false);
			}
		});

		commentFieldPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked( MouseEvent e ) {
				if ( ( e.getButton() == MouseEvent.BUTTON1 ) && !commentTextField.hasFocus() ) {
					commentTextField.grabFocus();
				}
			}
		});

		commentTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained( FocusEvent e ) {
				commentFieldPlaceholder.setVisible(false);
			}

			@Override
			public void focusLost( FocusEvent e ) {
				if ( commentTextField.getText().isEmpty() ) {
					commentFieldPlaceholder.setVisible(true);
				}
			}
		});

		commentTextField.addActionListener(( ActionEvent e ) -> {
			JTextField textField = (JTextField) e.getSource();
			String comment = textField.getText();
			controller.changeComment(comment);
			frame.requestFocusInWindow();
			swingViewHelper.flashComponent(commentConfirmLabel);
		});
	}

	private void setupBottomToolBarActions() {
		addProjectButton.addActionListener(( ActionEvent e ) -> {
			String name = JOptionPane.showInputDialog(workTrackerPanel, resourceBundle.getString("enter.project.name"));
			if ( ( name != null ) && !name.isEmpty() ) {
				controller.addProject(name);
			}
		});


		dataBrowserButton.addActionListener(( ActionEvent e ) -> {
			JOptionPane.showMessageDialog(workTrackerPanel,
										  resourceBundle.getString("missing.feature"),
										  resourceBundle.getString("missing.feature.title"), JOptionPane.ERROR_MESSAGE);
		});

		infoButton.addActionListener(e -> new SwingViewAboutDialog(frame).setVisible(true));

		quitButton.addActionListener(( ActionEvent e ) -> {
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		});
	}

	private void showView() {
		frame = new JFrame("WorkTracker");
		frame.setContentPane(this.workTrackerPanel);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		URL iconUrl = getClass().getResource("/icons/logo.png");
		if ( iconUrl != null ) {
			frame.setIconImage(new ImageIcon(iconUrl).getImage());
		}
		frame.pack();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed( WindowEvent e ) {
				controller.quit();
			}
		});
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
	}

	@Override
	public void update( WorkTracker workTracker ) {
		String totalTimeToday = entriesTableModel.update(workTracker);
		incompleteEntriesTableModel.update(workTracker);

		if ( workTracker.getUnfinishedLogEntries().size() > 0 ) {
			incompleteEntriesPanel.setVisible(true);
		} else {
			incompleteEntriesPanel.setVisible(false);
		}
		totalTimeTodayLabel.setText(totalTimeToday);

		if ( !commentTextField.hasFocus() ) {
			String comment = workTracker.getCurrentLogEntry().getComment();
			commentTextField.setText(comment);
			commentFieldPlaceholder.setVisible(( comment == null ) || comment.isEmpty());
		}

		frame.setTitle("WorkTracker | " + workTracker.getCurrentLogEntry().getProject());

		if ( entriesTable.getSelectedRow() == -1 ) {
			int index = 0;
			for ( Project project : workTracker.getProjects() ) {
				if ( project == workTracker.getCurrentLogEntry().getProject() ) {
					break;
				}
				index++;
			}
			entriesTable.setRowSelectionInterval(index, index);
		}
	}

	private class ProjectTableCellRenderer extends DefaultTableCellRenderer {

		private WorkTracker workTracker;

		public ProjectTableCellRenderer( WorkTracker workTracker ) {
			this.workTracker = workTracker;
		}

		@Override
		public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected,
														boolean hasFocus, int row, int column ) {

			JLabel label =
					(JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if ( value instanceof Project ) {
				URL iconUrl;
				if ( value.equals(workTracker.getPauseProject()) ) {
					iconUrl = getClass().getResource("/icons/break.png");
					label.setFont(getFont().deriveFont(Font.BOLD));
				} else {
					iconUrl = getClass().getResource("/icons/table.png");
				}
				if ( iconUrl != null ) {
					label.setIcon(new ImageIcon(iconUrl));
				}
			}

			return label;
		}
	}

}

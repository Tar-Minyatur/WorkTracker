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
import de.tshw.worktracker.model.WorkLogEntry;
import de.tshw.worktracker.model.WorkTracker;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class SwingView implements WorkTrackerView {

	private static final String RESOURCE_BUNDLE = "i18n/swingView";
	private final TimeEntriesTableModel       entriesTableModel;
	private final IncompleteEntriesTableModel incompleteEntriesTableModel;
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
	private JButton               operationsButton;
	private JButton               infoButton;
	private JButton               dataBrowserButton;
	private JPanel                commentFieldPanel;
	private WorkTracker           workTracker;
	private WorkTrackerController controller;
	private JFrame                frame;
	private JPopupMenu            operationsMenu;

	private ResourceBundle resourceBundle;

	public SwingView( WorkTracker workTracker, WorkTrackerController controller ) {
		this.workTracker = workTracker;
		this.controller = controller;
		controller.registerView(this);

		entriesTableModel = new TimeEntriesTableModel(workTracker);
		this.entriesTable.setModel(entriesTableModel);
		incompleteEntriesTableModel = new IncompleteEntriesTableModel(workTracker);
		this.incompleteEntriesTable.setModel(incompleteEntriesTableModel);

		resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);

		configureTables(workTracker, controller);
		setupIncompleteEntryActions();
		setupEntriesTableActions();
		setupCommentField();
		setupBottomToolBarActions();
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

	private void setupCommentField() {
		commentTextField = new JTextFieldWithPlaceholder(resourceBundle.getString("add.a.comment"));
		JComponentWithActionFeedback feedbackComponent = new JComponentWithActionFeedback();
		feedbackComponent.add(commentTextField);
		feedbackComponent.setAction(component -> {
			controller.changeComment(commentTextField.getText());
			return true;
		});
		commentFieldPanel.add(feedbackComponent);
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

		// Operations Menu
		operationsMenu = new JPopupMenu();
		// -> Settings
		JMenu settingsMenu = new JMenu(resourceBundle.getString("operations.menu.settings"));
		settingsMenu.setMnemonic(resourceBundle.getString("operations.menu.settings.mnemonic").toCharArray()[0]);
		settingsMenu.setIcon(new ImageIcon(getClass().getResource("/icons/wrench_orange.png")));
		// ---> Not implemented
		JMenuItem menuItem = new JMenuItem(resourceBundle.getString("missing.feature"));
		menuItem.setEnabled(false);
		settingsMenu.add(menuItem);
		operationsMenu.add(settingsMenu);
		// ---------------------
		operationsMenu.add(new JPopupMenu.Separator());
		// -> Quit
		menuItem = new JMenuItem(resourceBundle.getString("operations.menu.quit"));
		menuItem.setMnemonic(resourceBundle.getString("operations.menu.quit.mnemonic").toCharArray()[0]);
		menuItem.setIcon(new ImageIcon(getClass().getResource("/icons/door.png")));
		menuItem.addActionListener(( ActionEvent e ) -> {
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		});
		operationsMenu.add(menuItem);

		operationsButton.addActionListener(( ActionEvent e ) -> {
			JComponent source = (JComponent) e.getSource();
			operationsMenu.setInvoker(source);
			operationsMenu.setLocation(source.getLocationOnScreen());
			operationsMenu.setVisible(true);
		});
	}

	public void showView() {
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
		frame.setMinimumSize(new Dimension(290, 330));
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

		WorkLogEntry currentLogEntry = workTracker.getCurrentLogEntry();
		Project currentProject = ( currentLogEntry == null ) ? null : currentLogEntry.getProject();

		if ( ( currentProject == null ) && !commentTextField.hasFocus() ) {
			String comment = currentLogEntry.getComment();
			commentTextField.setText(comment);
		}

		frame.setTitle("WorkTracker" + ( ( currentLogEntry == null ) ? "" : " | " + currentProject ));

		if ( entriesTable.getSelectedRow() == -1 ) {
			int index = 0;
			for ( Project project : workTracker.getProjects() ) {
				if ( project == currentProject ) {
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

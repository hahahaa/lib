package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import main.Borrower;
import main.Clerk;
import main.Librarian;


public class BorrowerPanel {
	
	private JTextField searchField;
	private JPanel mainPanel;
	
	public BorrowerPanel(){
	}
	
	private void openSearchForm(){
		// Add borrower Form
		JPanel searchForm = new JPanel();
		// Set form layout
		searchForm.setLayout(new GridLayout(0, 2, 10, 10));
		searchForm.setBorder(new EmptyBorder(10, 10, 10, 10) );

		// Field Labels
		JLabel searchLabel = new JLabel("Search: ");
		// Fields
		searchField = new JTextField(10);
		
		// Buttons
		JButton searchButton = new JButton("Search");
		JButton cancelButton = new JButton("Cancel");

		// Add components to panel
		searchForm.add(searchLabel);
		searchForm.add(searchField);
		searchForm.add(cancelButton);
		searchForm.add(searchButton);


		// Window
		final JFrame frame = new JFrame("Search");
		// Window Properties
		frame.pack();
		frame.setVisible(true);

		//Add content to the window.
		frame.add(searchForm, BorderLayout.WEST);

		frame.setResizable(true);
		frame.setSize(640/2,360/3);
		frame.setLocation( 180, 140 );

		// Button Listeners
		searchButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				openResultsForm();
			}
		});

		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
		});
	}
	
	private void openResultsForm(){
		// Add check overdue Form
		JPanel resultsForm = new JPanel();
		
		final String[] columnNames = {"Call Number", "Copy #", "Title", "Out Date", "Due Date", "Out"};
		Object[][] data = {};

		final DefaultTableModel model = new DefaultTableModel(data,columnNames);

	
		// Add table to view items
		JTable resultsTable = new JTable(model);
		
		TableColumn tc = resultsTable.getColumnModel().getColumn(5);  
        tc.setCellEditor(resultsTable.getDefaultEditor(Boolean.class));  
        tc.setCellRenderer(resultsTable.getDefaultRenderer(Boolean.class));

		model.insertRow(resultsTable.getRowCount(),new Object[]{"Call1", "1", "Book1", "date1", "date11", new Boolean(false)});
		model.insertRow(resultsTable.getRowCount(),new Object[]{"Call2", "2", "Book2", "date2", "date22", new Boolean(false)});
		model.insertRow(resultsTable.getRowCount(),new Object[]{"Call3", "3", "Book3", "date3", "date33", new Boolean(false)});
		model.insertRow(resultsTable.getRowCount(),new Object[]{"Call4", "4", "Book4", "date4", "date44", new Boolean(false)});
		
		
		// Add table to view items
		JScrollPane scrollPane = new JScrollPane(resultsTable);
		
		JButton closeButton = new JButton("Close");
		
		// Add components to panel
		scrollPane.setPreferredSize(new Dimension(480, 200));
		resultsForm.add(scrollPane, BorderLayout.PAGE_START);
		resultsForm.add(closeButton, BorderLayout.PAGE_END);

		// Window
		final JFrame frame = new JFrame("Search Results");
		// Window Properties
		frame.pack();
		frame.setVisible(true);
		//Add content to the window.
		frame.add(resultsForm, BorderLayout.WEST);

		frame.setResizable(true);
		frame.setSize(640,360/2);
		frame.setLocation( 50, 140 );

		// Button Listeners
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
		});
	}
	
	private void openAccountForm(){
		// Add checkout Form
		JPanel accountForm = new JPanel();
		// Set form layout
		accountForm.setLayout(new GridLayout(0, 3, 10, 10));
		accountForm.setBorder(new EmptyBorder(10, 10, 10, 10) );
		
		// Buttons
		JButton borrowedButton = new JButton("Borrowed Items");
		JButton finesButton = new JButton("Oustanding Fines");
		JButton holdButton = new JButton("Items on Hold");

		// Add components to panel
		accountForm.add(borrowedButton);
		accountForm.add(finesButton);
		accountForm.add(holdButton);

		// Window
		final JFrame frame = new JFrame("Account Information");
		// Window Properties
		frame.pack();
		frame.setVisible(true);
		frame.setSize(300, 125);
		//Add content to the window.
		frame.add(accountForm, BorderLayout.WEST);

		frame.setResizable(true);
		frame.setSize(640/3*2,360/3);
		frame.setLocation( 180, 140 );

		// Button Listeners
		borrowedButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{				
				openBorrowedForm();
			}
		});
		
		finesButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				openFinesForm();
			}
		});
		
		holdButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{				
				openViewHoldForm();
			}
		});


	}
	
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	private void openHoldForm(){
		// Add borrower Form
		JPanel holdForm = new JPanel();
		// Set form layout
		holdForm.setLayout(new GridLayout(0, 2, 10, 10));
		holdForm.setBorder(new EmptyBorder(10, 10, 10, 10) );

		// Field Labels
		JLabel callNumberLabel = new JLabel("Call Number: ");
		JLabel bidLabel = new JLabel( "Bid: " );
		JLabel passwordLabel = new JLabel( "Password: " );
		// Fields
		final JTextField callNumberField = new JTextField(10);
		final JTextField bidField = new JTextField(10);
		final JTextField passwordField = new JTextField(10);
		
		// Buttons
		JButton holdButton = new JButton("Place Hold");
		JButton cancelButton = new JButton("Cancel");

		// Add components to panel
		holdForm.add(callNumberLabel);
		holdForm.add(callNumberField);
		
		holdForm.add( bidLabel );
		holdForm.add( bidField );
		
		holdForm.add( passwordLabel );
		holdForm.add( passwordField );
		
		holdForm.add(cancelButton);
		holdForm.add(holdButton);


		// Window
		final JFrame frame = new JFrame("Hold Request");
		// Window Properties
		frame.pack();
		frame.setVisible(true);
		frame.setSize(300, 125);
		//Add content to the window.
		frame.add(holdForm, BorderLayout.WEST);

		frame.setResizable(true);
		frame.setSize(640/2,360/2);
		frame.setLocation( 180, 140 );
		// Button Listeners
		holdButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				int bid = Integer.parseInt( bidField.getText() );
				String callNumber = callNumberField.getText();
				String password = passwordField.getText();
				Borrower.placeHoldRequest( bid, password, callNumber );
			}
		});

		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
		});
	}
	
	private void openBorrowedForm(){
		// Add check overdue Form
		JPanel borrowedForm = new JPanel();
		
		final String[] columnNames = {"Call Number", "Copy #", "Title", "Out Date", "Due Date", "Out"};
		Object[][] data = {};

		final DefaultTableModel model = new DefaultTableModel(data,columnNames);

	
		// Add table to view items
		JTable borrowedTable = new JTable(model);
		
		TableColumn tc = borrowedTable.getColumnModel().getColumn(5);  
        tc.setCellEditor(borrowedTable.getDefaultEditor(Boolean.class));  
        tc.setCellRenderer(borrowedTable.getDefaultRenderer(Boolean.class));

		model.insertRow(borrowedTable.getRowCount(),new Object[]{"Call1", "1", "Book1", "date1", "date11", new Boolean(false)});
		model.insertRow(borrowedTable.getRowCount(),new Object[]{"Call2", "2", "Book2", "date2", "date22", new Boolean(false)});
		model.insertRow(borrowedTable.getRowCount(),new Object[]{"Call3", "3", "Book3", "date3", "date33", new Boolean(false)});
		model.insertRow(borrowedTable.getRowCount(),new Object[]{"Call4", "4", "Book4", "date4", "date44", new Boolean(false)});
		
		
		// Add table to view items
		JScrollPane scrollPane = new JScrollPane(borrowedTable);
		
		JButton closeButton = new JButton("Close");
		
		// Add components to panel
		scrollPane.setPreferredSize(new Dimension(480, 200));
		borrowedForm.add(scrollPane, BorderLayout.PAGE_START);
		borrowedForm.add(closeButton, BorderLayout.PAGE_END);

		// Window
		final JFrame frame = new JFrame("Borrowed Items");
		// Window Properties
		frame.pack();
		frame.setVisible(true);

		frame.add(borrowedForm, BorderLayout.WEST);
		
		frame.setResizable(true);
		frame.setSize(640,360/2);
		frame.setLocation( 50, 140 );
		// Button Listeners
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
		});
	}

	private void openViewHoldForm(){
		// Add check overdue Form
		JPanel viewHoldForm = new JPanel();
		
		final String[] columnNames = {"Call Number", "Copy #", "Title", "Out Date", "Due Date", "Out"};
		Object[][] data = {};

		final DefaultTableModel model = new DefaultTableModel(data,columnNames);

	
		// Add table to view items
		JTable viewHoldTable = new JTable(model);
		
		TableColumn tc = viewHoldTable.getColumnModel().getColumn(5);  
        tc.setCellEditor(viewHoldTable.getDefaultEditor(Boolean.class));  
        tc.setCellRenderer(viewHoldTable.getDefaultRenderer(Boolean.class));

		model.insertRow(viewHoldTable.getRowCount(),new Object[]{"Call1", "1", "Book1", "date1", "date11", new Boolean(false)});
		model.insertRow(viewHoldTable.getRowCount(),new Object[]{"Call2", "2", "Book2", "date2", "date22", new Boolean(false)});
		model.insertRow(viewHoldTable.getRowCount(),new Object[]{"Call3", "3", "Book3", "date3", "date33", new Boolean(false)});
		model.insertRow(viewHoldTable.getRowCount(),new Object[]{"Call4", "4", "Book4", "date4", "date44", new Boolean(false)});
		
		
		// Add table to view items
		JScrollPane scrollPane = new JScrollPane(viewHoldTable);
		
		JButton closeButton = new JButton("Close");
		
		// Add components to panel
		scrollPane.setPreferredSize(new Dimension(480, 200));
		viewHoldForm.add(scrollPane, BorderLayout.PAGE_START);
		viewHoldForm.add(closeButton, BorderLayout.PAGE_END);

		// Window
		final JFrame frame = new JFrame("Items on Hold");
		// Window Properties
		frame.pack();
		frame.setVisible(true);

		//Add content to the window.
		frame.add(viewHoldForm, BorderLayout.WEST);
		
		frame.setResizable(true);
		frame.setSize(640,360/2);
		frame.setLocation( 50, 140 );

		// Button Listeners
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
		});
	}
	
	private void openFinesForm(){
		// Add check overdue Form
		JPanel finesForm = new JPanel();

		final String[] columnNames = {"Call Number", "Copy #", "Title", "Out Date", "Due Date", "Out"};
		Object[][] data = {};

		final DefaultTableModel model = new DefaultTableModel(data,columnNames);


		// Add table to view items
		JTable finesTable = new JTable(model);

		TableColumn tc = finesTable.getColumnModel().getColumn(5);  
        tc.setCellEditor(finesTable.getDefaultEditor(Boolean.class));  
        tc.setCellRenderer(finesTable.getDefaultRenderer(Boolean.class));

		model.insertRow(finesTable.getRowCount(),new Object[]{"Call1", "1", "Book1", "date1", "date11", new Boolean(false)});
		model.insertRow(finesTable.getRowCount(),new Object[]{"Call2", "2", "Book2", "date2", "date22", new Boolean(false)});
		model.insertRow(finesTable.getRowCount(),new Object[]{"Call3", "3", "Book3", "date3", "date33", new Boolean(false)});
		model.insertRow(finesTable.getRowCount(),new Object[]{"Call4", "4", "Book4", "date4", "date44", new Boolean(false)});


		// Add table to view items
		JScrollPane scrollPane = new JScrollPane(finesTable);

		JButton closeButton = new JButton("Close");

		// Add components to panel
		scrollPane.setPreferredSize(new Dimension(480, 200));
		finesForm.add(scrollPane, BorderLayout.PAGE_START);
		finesForm.add(closeButton, BorderLayout.PAGE_END);

		// Window
		final JFrame frame = new JFrame("Outstanding Fines");
		// Window Properties
		frame.pack();
		frame.setVisible(true);

		//Add content to the window.
		frame.add(finesForm, BorderLayout.WEST);
		
		frame.setResizable(true);
		frame.setSize(640,360/2);
		frame.setLocation( 50, 140 );

		// Button Listeners
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
		});
	}

	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	private void openPayFineForm()
	{
		// Add a pay fine form
		JPanel finesForm = new JPanel();

		// Set form layout
		finesForm.setLayout( new GridLayout( 0, 2, 10, 10 ) );
		finesForm.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );

		// Field Labels
		JLabel bidLabel = new JLabel("BID: ");
		JLabel passwordLabel = new JLabel( "password: " );
		JLabel fidLabel = new JLabel("Fine ID: ");
		//JLabel amountLabel = new JLabel( "Amount: " );

		// Fields
		final JTextField bidField = new JTextField(10);
		final JTextField passwordField = new JTextField(10);
		final JTextField fidField = new JTextField(10);
		//final JTextField amountField = new JTextField(10);

		// Buttons
		JButton payButton = new JButton("Pay Fine");
		JButton cancelButton = new JButton("Cancel");

		// Add components to panel
		finesForm.add( bidLabel );
		finesForm.add( bidField );
		
		finesForm.add( passwordLabel );
		finesForm.add( passwordField );
		
		finesForm.add( fidLabel );
		finesForm.add( fidField );
		
		finesForm.add( payButton );
		finesForm.add( cancelButton );

		// Window
		final JFrame frame = new JFrame("Pay Fine");
		// Window Properties
		frame.pack();
		frame.setVisible(true);

		//Add content to the window.
		frame.add(finesForm, BorderLayout.WEST);
		
		frame.setResizable(true);
		frame.setSize(640/2,360/2);
		frame.setLocation( 180, 140 );

		
		
		// Button Listeners
		payButton.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e )
			{
				int bid = Integer.parseInt( bidField.getText() );
				String password = passwordField.getText();
				int fid = Integer.parseInt( fidField.getText() );
				//int amount = Integer.parseInt( amountField.getText() );
				Borrower.payFine( bid, password, fid );
			}
		});

		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
		});
	}

	
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////// //just adding open pay form()
	public JComponent getBorrowerPanel(){
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0, 2, 10, 10));
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );

		JButton searchButton = new JButton("Search");
		JButton viewAccountButton = new JButton("View Account");
		JButton holdRequestButton = new JButton("Hold Request");
		JButton payFineButton = new JButton("Pay Fine");
		
		
		mainPanel.add(viewAccountButton);
		mainPanel.add(holdRequestButton);
		mainPanel.add(searchButton);
		mainPanel.add(payFineButton);
		

		
		searchButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				openSearchForm();
			}
		});
		viewAccountButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				openAccountForm();
			}
		});
		holdRequestButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				openHoldForm();
			}
		});
		payFineButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				openPayFineForm();
			}
		});
		
		return mainPanel;
		
	}
}

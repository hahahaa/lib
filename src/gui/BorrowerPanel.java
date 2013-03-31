package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.sql.Connection;

import main.Borrower;
import main.Clerk;
import main.Librarian;


public class BorrowerPanel {
	
	private JFrame mainFrame;
	private JTextField searchField;
	private JPanel mainPanel;
	private Connection con;
	private Borrower borrower;
	
	public BorrowerPanel(Connection con){
		this.con = con;
		borrower = new Borrower(con);
	}
	
	private void openSearchForm(){
		// Add borrower Form
		JPanel searchForm = new JPanel();
		// Set form layout
		searchForm.setLayout(new GridLayout(0, 2, 10, 10));
		searchForm.setBorder(new EmptyBorder(10, 10, 10, 10) );

		Font bItalic = new Font("Arial", Font.ITALIC, 15);
//		loginButton.setFont(bItalic);
		
		
		// Field Labels
		JLabel searchLabel = new JLabel("Search: ");
		// Fields
		searchField = new JTextField(10);
		
		// Buttons
		JButton searchButton = new JButton("Search");
		JButton cancelButton = new JButton("Cancel");
		
		searchLabel.setFont(bItalic);
		searchButton.setFont(bItalic);
		cancelButton.setFont(bItalic);

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
		frame.add(searchForm, BorderLayout.CENTER);

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
		
		// Add components to panel
		scrollPane.setPreferredSize(new Dimension(480, 200));
		resultsForm.add(scrollPane, BorderLayout.PAGE_START);

		// Window
		final JFrame frame = new JFrame("Search Results");
		// Window Properties
		frame.pack();
		frame.setVisible(true);
		//Add content to the window.
		frame.add(resultsForm, BorderLayout.CENTER);

		frame.setResizable(true);
		frame.setSize(640,360/2);
		frame.setLocation( 50, 140 );

	}
	
	private void openAccountForm(){
		// Add checkout Form
		JPanel accountForm = new JPanel();
		// Set form layout
		accountForm.setLayout(new GridLayout(0, 1, 10, 10));
		accountForm.setBorder(new EmptyBorder(10, 10, 10, 10) );
		
		Font bItalic = new Font("Arial", Font.ITALIC, 15);
		
		// Buttons
		JButton borrowedButton = new JButton("Check Borrowed Books");
		JButton finesButton = new JButton("Check Oustanding Fines");
		JButton holdButton = new JButton("Check Hold Request");
		
		borrowedButton.setFont(bItalic);
		finesButton.setFont(bItalic);
		holdButton.setFont(bItalic);
		
		// Add components to panel
		accountForm.add(borrowedButton);
		accountForm.add(finesButton);
		accountForm.add(holdButton);

		// Window
		JFrame frame = new JFrame("Account Information");
		
		// Window Properties
		frame.pack();
		frame.setVisible(true);
		frame.add(accountForm, BorderLayout.CENTER);

		frame.setResizable(true);
		frame.setSize(300,200);
		frame.setLocation( 100, 140 );

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

		Font bItalic = new Font("Arial", Font.ITALIC, 15);
//		loginButton.setFont(bItalic);
		
		
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
		
		callNumberLabel.setFont(bItalic);
		bidLabel.setFont(bItalic);
		passwordLabel.setFont(bItalic);
		holdButton.setFont(bItalic);
		cancelButton.setFont(bItalic);
		

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
		frame.add(holdForm, BorderLayout.CENTER);

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
				borrower.placeHoldRequest( bid, password, callNumber );
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
		// Add Book Form
				JPanel addBookForm = new JPanel();
				// Set form layout
				addBookForm.setLayout(new GridLayout(0, 2, 10, 10));
				addBookForm.setBorder(new EmptyBorder(10, 10, 10, 10) );

				// Field Labels
				JLabel bidLabel = new JLabel("Borrower ID: ");
				JLabel passwordLabel = new JLabel("Password: ");

				// Fields
				JTextField bidField = new JTextField(20);
				JTextField passwordField = new JTextField(20);

				// Buttons
				JButton addButton = new JButton("Add");
				JButton cancelButton = new JButton("Cancel");

				// Add components to panel
				addBookForm.add(bidLabel);
				addBookForm.add(bidField);
				//addBookForm.add(isbnLabel);
				addBookForm.add(passwordField);
				//addBookForm.add(titleLabel);
				/*addBookForm.add(titleField);
				//addBookForm.add(mainAuthorLabel);
				addBookForm.add(mainAuthorField);
				//addBookForm.add(publisherLabel);
				addBookForm.add(publisherField);
				addBookForm.add(passwordLabel);
				addBookForm.add(yearField);
				addBookForm.add(addButton);
				addBookForm.add(cancelButton);*/

				// Window
				final JFrame frame = new JFrame("Log In");
				// Window Properties
				frame.pack();
				frame.setVisible(true);
				frame.setResizable(false);
				frame.setSize(300, 300);

				//Add content to the window.
				frame.add(addBookForm, BorderLayout.CENTER);

				// center the frame
				Dimension d = frame.getToolkit().getScreenSize();
				Rectangle r = frame.getBounds();
				frame.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );
				frame.dispose();

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
		
		// Add components to panel
		scrollPane.setPreferredSize(new Dimension(480, 200));
		viewHoldForm.add(scrollPane, BorderLayout.PAGE_START);

		// Window
		final JFrame frame = new JFrame("Items on Hold");
		// Window Properties
		frame.pack();
		frame.setVisible(true);

		//Add content to the window.
		frame.add(viewHoldForm, BorderLayout.CENTER);
		
		frame.setResizable(true);
		frame.setSize(640,360/2);
		frame.setLocation( 50, 140 );
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

		// Add components to panel
		scrollPane.setPreferredSize(new Dimension(480, 200));
		finesForm.add(scrollPane, BorderLayout.PAGE_START);

		// Window
		final JFrame frame = new JFrame("Outstanding Fines");
		// Window Properties
		frame.pack();
		frame.setVisible(true);

		//Add content to the window.
		frame.add(finesForm, BorderLayout.CENTER);
		
		frame.setResizable(true);
		frame.setSize(640,360/2);
		frame.setLocation( 50, 140 );
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

		Font bItalic = new Font("Arial", Font.ITALIC, 15);
//		loginButton.setFont(bItalic);
		
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
		
		bidLabel.setFont(bItalic);
		passwordLabel.setFont(bItalic);
		fidLabel.setFont(bItalic);
		payButton.setFont(bItalic);
		cancelButton.setFont(bItalic);

		// Add components to panel
		finesForm.add( bidLabel );
		finesForm.add( bidField );
		
		finesForm.add( passwordLabel );
		finesForm.add( passwordField );
		
		finesForm.add( fidLabel );
		finesForm.add( fidField );
		
		finesForm.add( cancelButton );
		finesForm.add( payButton );

		// Window
		final JFrame frame = new JFrame("Pay Fine");
		// Window Properties
		frame.pack();
		frame.setVisible(true);

		//Add content to the window.
		frame.add(finesForm, BorderLayout.CENTER);
		
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
				borrower.payFine( bid, password, fid );
			}
		});

		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
		});
	}


	public JComponent getBorrowerPanel(){
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0, 2, 10, 10));
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );

		
		JButton searchButton = new JButton("Search Book");
		
		JButton logInButton = new JButton("Log In");
		
		//JButton holdRequestButton = new JButton("Make Hold Request");
		
		//JButton payFineButton = new JButton("Pay Fine");
		
		
		
		mainPanel.add(logInButton);
		//mainPanel.add(holdRequestButton);
		mainPanel.add(searchButton);
		//mainPanel.add(payFineButton);
		

		
		searchButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				openSearchForm();
			}
		});
		logInButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//openAccountForm();
				openLogInForm();
			}
		});
		/*holdRequestButton.addActionListener(new ActionListener(){
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
		});*/
		
		return mainPanel;
		
	}
	
	private void openLogInForm(){
		
		/*
		 * constructs login window and loads JDBC driver
		 */ 
		mainFrame = new JFrame("User Login");

		JLabel usernameLabel = new JLabel("Enter username: ");
		JLabel passwordLabel = new JLabel("Enter password: ");

		final JTextField usernameField = new JTextField(10);
		final JPasswordField passwordField = new JPasswordField(10);
		passwordField.setEchoChar('*');

		JButton loginButton = new JButton("Log In");

		JPanel contentPane = new JPanel();
		mainFrame.setContentPane(contentPane);

		// layout components using the GridBag layout manager

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		contentPane.setLayout(gb);
		contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// place the username label 
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.insets = new Insets(10, 10, 5, 0);
		gb.setConstraints(usernameLabel, c);
		contentPane.add(usernameLabel);

		// place the text field for the username 
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(10, 0, 5, 10);
		gb.setConstraints(usernameField, c);
		contentPane.add(usernameField);

		// place password label
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.insets = new Insets(0, 10, 10, 0);
		gb.setConstraints(passwordLabel, c);
		contentPane.add(passwordLabel);

		// place the password field 
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0, 0, 10, 10);
		gb.setConstraints(passwordField, c);
		contentPane.add(passwordField);

		// place the login button
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(5, 10, 10, 10);
		c.anchor = GridBagConstraints.CENTER;
		gb.setConstraints(loginButton, c);
		contentPane.add(loginButton);

		loginButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String username = usernameField.getText();
				String password = String.valueOf(passwordField.getPassword());
				
				try{
					int bid = Integer.parseInt(username);
					if(borrower.accountValidated(bid, password)){
						mainFrame.dispose();
						openNewWindow();
					}else{
						JOptionPane.showMessageDialog(null,
								"Invalid username/password combination",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					}
					
				} catch (Exception e1){
					JOptionPane.showMessageDialog(null,
							"Invalid username " + e1.getMessage(),
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
				//System.out.println(password);
				//borrower.accountValidated(bid, password)
				//mainFrame.dispose();
			}
			
			
		});

		// anonymous inner class for closing the window
		mainFrame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){ 
				System.exit(0); 
			}
		});

		// size the window to obtain a best fit for the components
		mainFrame.pack();

		// center the frame
		Dimension d = mainFrame.getToolkit().getScreenSize();
		Rectangle r = mainFrame.getBounds();
		mainFrame.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		mainFrame.setVisible(true);

		// place the cursor in the text field for the username
		usernameField.requestFocus();
		
	}
	
	private void openNewWindow(){
		
	}
}

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
import java.sql.Connection;
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

import main.Borrower;


public class BorrowerPanel {

	int bid;
	String password;
	private JFrame mainFrame;
	private JPanel mainPanel;
	private Borrower borrower;

	public BorrowerPanel(Connection con){
		borrower = new Borrower(con);
	}

	private void openSearchForm(){
		JPanel viewPopForm = new JPanel();
		final String[] columnNames = {"CallNumber",  "Title", "Main Author", "Publisher", "Year","ISBN","Copies In","Copies Out"};
		String[][] data = {};

		final DefaultTableModel outModel = new DefaultTableModel(data, columnNames);
		JTable viewOutTable = new JTable(outModel);

		JScrollPane scrollPane = new JScrollPane(viewOutTable);

		JLabel bookTitleLabel = new JLabel("Title: ");
		final JTextField bookTitleField = new JTextField(15);
		JLabel authorLabel = new JLabel("Author: ");
		final JTextField authorField = new JTextField(15);
		JLabel subjectLabel = new JLabel("Subject: ");
		final JTextField subjectField = new JTextField(10);
		JPanel topPanel = new JPanel();

		topPanel.add(bookTitleLabel);
		topPanel.add(bookTitleField);
		topPanel.add(authorLabel);
		topPanel.add(authorField);
		topPanel.add(subjectLabel);
		topPanel.add(subjectField);

		JButton searchButton = new JButton("Search");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(searchButton, BorderLayout.LINE_START);		

		scrollPane.setPreferredSize(new Dimension(800, 350));
		viewPopForm.add(scrollPane, BorderLayout.PAGE_START);
		viewPopForm.add(topPanel, BorderLayout.CENTER);
		viewPopForm.add(buttonPanel, BorderLayout.PAGE_END);

		final JFrame frame = new JFrame("Search");
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(820, 450);
		frame.add(viewPopForm, BorderLayout.CENTER);
		Dimension d = frame.getToolkit().getScreenSize();
		Rectangle r = frame.getBounds();
		frame.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outModel.setRowCount(0);
				String title = bookTitleField.getText();
				String author = authorField.getText();
				String subject = subjectField.getText();
				borrower.searchBook(title, author, subject);
				ArrayList<String[]> report = borrower.searchBook(title, author, subject);
				for(int i = 0; i < report.size(); i++)
					outModel.addRow(report.get(i));
			}
		});
	}

	private void makeHoldRequest(){
		// Add borrower Form
		JPanel holdForm = new JPanel();
		// Set form layout
		holdForm.setLayout(new GridLayout(0, 2, 10, 10));
		holdForm.setBorder(new EmptyBorder(10, 10, 10, 10) );

		Font bItalic = new Font("Arial", Font.ITALIC, 15);


		// Field Labels
		JLabel callNumberLabel = new JLabel("Call Number: ");

		// Fields
		final JTextField callNumberField = new JTextField(10);

		// Buttons
		JButton makeRequestButton = new JButton("Place Hold");
		JButton cancelButton = new JButton("Cancel");

		callNumberLabel.setFont(bItalic);
		makeRequestButton.setFont(bItalic);
		cancelButton.setFont(bItalic);


		// Add components to panel
		holdForm.add(callNumberLabel);
		holdForm.add(callNumberField);

		holdForm.add(cancelButton);
		holdForm.add(makeRequestButton);


		// Window
		final JFrame frame = new JFrame("Hold Request");
		// Window Properties
		frame.pack();
		frame.setVisible(true);
		frame.add(holdForm, BorderLayout.CENTER);

		frame.setResizable(false);
		frame.setSize(275,125);
		frame.setLocation( 180, 140 );
		// Button Listeners
		makeRequestButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{

				String callNumber = callNumberField.getText();
				if (callNumber.compareTo("")==0){
					JOptionPane.showMessageDialog(null,
							"Please key in the CallNumber",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				borrower.placeHoldRequest(bid, callNumber );
			}
		});

		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
		});
	}

	private void checkBorrowedBooks(){
		JPanel viewOutForm = new JPanel();
		final String[] columnNames = {"Borrow ID", "Title", "Main Author", 
				"Publisher", "Borrowed Date", "Expiry Date"};
		String[][] data = {};

		final DefaultTableModel outModel = new DefaultTableModel(data, columnNames);
		JTable viewOutTable = new JTable(outModel);

		JScrollPane scrollPane = new JScrollPane(viewOutTable);

		scrollPane.setPreferredSize(new Dimension(600, 200));
		viewOutForm.add(scrollPane, BorderLayout.PAGE_START);
		//viewOutForm.add(subjectsPanel, BorderLayout.CENTER);

		final JFrame frame = new JFrame("Borrowed Books");
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(625, 310);
		frame.add(viewOutForm, BorderLayout.CENTER);

		Dimension d = frame.getToolkit().getScreenSize();
		Rectangle r = frame.getBounds();
		frame.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		outModel.setRowCount(0);
		//String subject = subjectsField.getText();
		ArrayList<String[]> result = borrower.getBorrowedBook(bid);
		for(int i = 0; i < result.size(); i++)
			outModel.addRow(result.get(i));
	}

	private void checkHoldRequest(){
		JPanel viewOutForm = new JPanel();
		final String[] columnNames = {"Request ID", "Title", "Main Author", 
				"Amount", "Issued Date", "Since"};
		String[][] data = {};

		final DefaultTableModel outModel = new DefaultTableModel(data, columnNames);
		JTable viewOutTable = new JTable(outModel);

		JScrollPane scrollPane = new JScrollPane(viewOutTable);

		scrollPane.setPreferredSize(new Dimension(600, 200));
		viewOutForm.add(scrollPane, BorderLayout.PAGE_START);
		//viewOutForm.add(subjectsPanel, BorderLayout.CENTER);

		final JFrame frame = new JFrame("Outstanding Fines");
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(625, 310);
		frame.add(viewOutForm, BorderLayout.CENTER);

		Dimension d = frame.getToolkit().getScreenSize();
		Rectangle r = frame.getBounds();
		frame.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		outModel.setRowCount(0);
		//String subject = subjectsField.getText();
		ArrayList<String[]> result = borrower.checkHoldRequests(bid);
		for(int i = 0; i < result.size(); i++)
			outModel.addRow(result.get(i));
	}

	private void checkOutstandingFine(){
		JPanel viewOutForm = new JPanel();
		final String[] columnNames = {"Fine ID", "Title", "Main Author", 
				"Amount", "Issued Date", "Since"};
		String[][] data = {};

		final DefaultTableModel outModel = new DefaultTableModel(data, columnNames);
		JTable viewOutTable = new JTable(outModel);

		JScrollPane scrollPane = new JScrollPane(viewOutTable);

		scrollPane.setPreferredSize(new Dimension(600, 200));
		viewOutForm.add(scrollPane, BorderLayout.PAGE_START);

		final JFrame frame = new JFrame("Outstanding Fines");
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(625, 310);
		frame.add(viewOutForm, BorderLayout.CENTER);

		Dimension d = frame.getToolkit().getScreenSize();
		Rectangle r = frame.getBounds();
		frame.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		outModel.setRowCount(0);
		//String subject = subjectsField.getText();
		ArrayList<String[]> result = borrower.checkFine(bid);
		for(int i = 0; i < result.size(); i++)
			outModel.addRow(result.get(i));
	}

	private void payFine()
	{
		// Add a pay fine form
		JPanel finesForm = new JPanel();

		// Set form layout
		finesForm.setLayout( new GridLayout( 0, 2, 10, 10 ) );
		finesForm.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );

		Font bItalic = new Font("Arial", Font.ITALIC, 15);

		// Field Labels
		JLabel fidLabel = new JLabel("Fine ID: ");

		// Fields
		final JTextField fidField = new JTextField(10);

		// Buttons
		JButton payButton = new JButton("Pay Fine");
		JButton cancelButton = new JButton("Cancel");

		fidLabel.setFont(bItalic);
		payButton.setFont(bItalic);
		cancelButton.setFont(bItalic);

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

		frame.setResizable(false);
		frame.setSize(275,125);
		frame.setLocation( 180, 140 );



		// Button Listeners
		payButton.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e )
			{
				try{
					int fid = Integer.parseInt(fidField.getText());
					borrower.payFine(bid,fid );
				}catch (Exception e1){
					JOptionPane.showMessageDialog(null,
							"Invalid FineID " + e1.getMessage(),
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}

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
				openLogInPanel();
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

	private void openLogInPanel(){

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
				String username2 = usernameField.getText();
				String password2 = String.valueOf(passwordField.getPassword());

				try{
					int usernameInInt;
					usernameInInt = Integer.parseInt(username2);
					if(borrower.accountValidated(usernameInInt, password2)){
						mainFrame.dispose();
						bid = usernameInInt;
						password = password2;
						openBorrowerAction();
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

	private void openBorrowerAction(){

		// Add checkout Form
		JPanel accountForm = new JPanel();
		// Set form layout
		accountForm.setLayout(new GridLayout(0, 1, 10, 10));
		accountForm.setBorder(new EmptyBorder(10, 10, 10, 10) );

		Font bItalic = new Font("Arial", Font.ITALIC, 15);

		// Buttons
		JButton checkBorrowButton = new JButton("Check Borrowed Books");
		JButton checkFineButton = new JButton("Check Oustanding Fines");
		JButton checkRequestButton = new JButton("Check Hold Request");
		JButton makeRequestButton = new JButton("Make Hold Request");
		JButton payFineButton = new JButton("Pay Outstanding Fine");
		JButton closeButton = new JButton("Close");

		checkBorrowButton.setFont(bItalic);
		checkFineButton.setFont(bItalic);
		checkRequestButton.setFont(bItalic);
		makeRequestButton.setFont(bItalic);
		payFineButton.setFont(bItalic);
		closeButton.setFont(bItalic);

		// Add components to panel
		accountForm.add(checkBorrowButton);
		accountForm.add(checkFineButton);
		accountForm.add(checkRequestButton);
		accountForm.add(makeRequestButton);
		accountForm.add(payFineButton);
		accountForm.add(closeButton);

		// Window
		final JFrame frame = new JFrame("Account Information");

		// Window Properties
		frame.pack();
		frame.setVisible(true);
		frame.add(accountForm, BorderLayout.CENTER);

		frame.setResizable(false);
		frame.setSize(300,400);
		frame.setLocation( 100, 140 );

		// Button Listeners
		checkBorrowButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{				
				checkBorrowedBooks();
			}
		});

		checkFineButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				checkOutstandingFine();
			}
		});

		checkRequestButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{				
				checkHoldRequest();
			}
		});

		makeRequestButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{				
				makeHoldRequest();
			}
		});

		payFineButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{				
				payFine();
			}
		});

		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
		});

	}
}

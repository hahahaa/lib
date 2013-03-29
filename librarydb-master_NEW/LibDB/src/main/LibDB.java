package main;

import gui.MainFrame;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


import javax.swing.*;
import java.awt.*;



public class LibDB implements ActionListener {

	// command line reader 
	private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public static Connection con;

	// user is allowed 3 login attempts
	private int loginAttempts = 0;

	// components of the login window
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JFrame mainFrame;
	private MainFrame mainWindow;

	public LibDB(){
		/*
		 * constructs login window and loads JDBC driver
		 */ 
		
		mainFrame = new JFrame("CPSC 304: Project 3");
//		Container c =mainFrame.getContentPane();
//		c.setLayout(null);

		JLabel usernameLabel = new JLabel("Enter username:  ");
		JLabel passwordLabel = new JLabel("Enter password:  ");

		usernameField = new JTextField(10);
		passwordField = new JPasswordField(10);
		passwordField.setEchoChar('*');

		JButton loginButton = new JButton("Log In");

		JPanel contentPane = new JPanel();
		mainFrame.setContentPane(contentPane);

		// layout components using the GridBag layout manager

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		contentPane.setLayout(gb);
		contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JLabel Jbt1, Jbt2, Jbt3;
		Jbt1 = new JLabel("Please Sign in to SQLPLUS...      ");
		contentPane.add(Jbt1);

		
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


		Jbt2 = new JLabel("                                  ");
		contentPane.add(Jbt2);

		
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
		c.anchor = GridBagConstraints.EAST;
		gb.setConstraints(loginButton, c);
		contentPane.add(loginButton);

		// register password field and OK button with action event handler
		passwordField.addActionListener(this);
		loginButton.addActionListener(this);

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
		mainFrame.setSize(640,360);
		mainFrame.setLocation( 50, 50 );

		// make the window visible
		mainFrame.setVisible(true);

		// place the cursor in the text field for the username
		usernameField.requestFocus();
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try{
			// Load the Oracle JDBC driver
			DriverManager.registerDriver ( new oracle.jdbc.OracleDriver() );
		}
		catch (SQLException ex){
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
	}

	/*
	 * connects to Oracle database named ug using user supplied username and password
	 */ 
	private boolean connect(String username, String password){
		String connectURL = "jdbc:oracle:thin:@dbhost.ugrad.cs.ubc.ca:1522:ug"; 

		try{
//			con = DriverManager.getConnection(connectURL,username,password);
		
			con = DriverManager.getConnection(connectURL,"ora_w6a8","a46753109");


			//System.out.println("\nConnected to Oracle!");
			JOptionPane.showMessageDialog(null,
					"Connected to Oracle!",
					"Information",
					JOptionPane.INFORMATION_MESSAGE);
			
			return true;
		}
		catch (SQLException ex){
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	/*
	 * event handler for login window
	 */ 
	public void actionPerformed(ActionEvent e) {
		if (connect(usernameField.getText(), String.valueOf(passwordField.getPassword()))){
			// if the username and password are valid, 
			// remove the login window and display a text menu 
			mainFrame.dispose();
			mainWindow = new MainFrame();
			mainWindow.loadMainFrame();    
		}
		else{
			loginAttempts++;

			if (loginAttempts >= 3){
				mainFrame.dispose();
				System.exit(-1);
			}
			else{
				// clear the password
				passwordField.setText("");
			}
		}             
	}

	/*
	 * display information about branches
	 */ 
	private void showBranch()
	{
		String callNumber;
		String isbn;
		String title;
		String mainAuthor;
		String publisher;
		String year;
		Statement  stmt;
		ResultSet  rs;

		try
		{
			stmt = con.createStatement();

			rs = stmt.executeQuery("SELECT * FROM book");

			// get info on ResultSet
			ResultSetMetaData rsmd = rs.getMetaData();

			// get number of columns
			int numCols = rsmd.getColumnCount();

			System.out.println(" ");

			// display column names;
			for (int i = 0; i < numCols; i++)
			{
				// get column name and print it

				System.out.printf("%-15s", rsmd.getColumnName(i+1));    
			}

			System.out.println(" ");

			while(rs.next())
			{
				// for display purposes get everything from Oracle 
				// as a string

				// simplified output formatting; truncation may occur

				callNumber = rs.getString("callNo");
				System.out.printf("%-10.10s", callNumber);

				isbn = rs.getString("isbn");
				System.out.printf("%-20.20s", isbn);
				
				title = rs.getString("title");
				System.out.printf("%-20.20s", title);

				mainAuthor = rs.getString("mainAuthor");
				System.out.printf("%-15.15s", mainAuthor);
				
				publisher = rs.getString("publisher");
				System.out.printf("%-15.15s", publisher);

				year = rs.getString("year");
				System.out.printf("%-15.15s", year);

			}

			// close the statement; 
			// the ResultSet will also be closed
			stmt.close();
		}
		catch (SQLException ex)
		{
			System.out.println("Message: " + ex.getMessage());
		}	
	}

	private void createBranch()
	{
		Statement  stmt;

		try
		{
			stmt = con.createStatement();
			String sql = "CREATE TABLE BOOK " +
					"(callNo VARCHAR(16), " +
					" isbn INTEGER NOT NULL, " +
					" title VARCHAR(50) NOT NULL, " +
					" mainAuthor VARCHAR(40) NOT NULL, " +
					" publisher VARCHAR(30) NOT NULL, " +
					" year INTEGER NOT NULL, " +
					" PRIMARY KEY ( callno ))"; 

			stmt.executeUpdate(sql);
			System.out.println("Created table in given database...");


			// close the statement; 
			// the ResultSet will also be closed
			stmt.close();
		}
		catch (SQLException ex)
		{
			System.out.println("Message: " + ex.getMessage());
		}	
	}

	private void dropBranch()
	{
		Statement  stmt;

		try
		{
			stmt = con.createStatement();
			String sql = "DROP TABLE BOOK"; 

			stmt.executeUpdate(sql);
			System.out.println("Book dropped");


			// close the statement; 
			// the ResultSet will also be closed
			stmt.close();
		}
		catch (SQLException ex)
		{
			System.out.println("Message: " + ex.getMessage());
		}	
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LibDB libDB = new LibDB();
	}

}

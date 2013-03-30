package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

import java.sql.Connection;

import main.Librarian;

public class LibrarianPanel {

	private JTextField callNumberField;
	private JTextField isbnField;
	private JTextField titleField;
	private JTextField mainAuthorField;
	private JTextField publisherField;
	private JTextField yearField;
	private JPanel mainPanel;
	
	public static JTable viewPopTable;
	public static DefaultTableModel popModel;
	
	public static JTable viewOutTable;
	public static DefaultTableModel outModel;
	
	private Connection con;

	public LibrarianPanel(Connection con) {
		this.con = con;
	}
	private void openAddBookForm(){
		// Add Book Form
		JPanel addBookForm = new JPanel();
		// Set form layout
		addBookForm.setLayout(new GridLayout(0, 2, 10, 10));
		addBookForm.setBorder(new EmptyBorder(10, 10, 10, 10) );

		// Field Labels
		JLabel callNumberLabel = new JLabel("Call Number: ");
		JLabel isbnLabel = new JLabel("ISBN: ");
		JLabel titleLabel = new JLabel("Title: ");
		JLabel mainAuthorLabel = new JLabel("Main Author: ");
		JLabel publisherLabel = new JLabel("Publisher: ");
		JLabel yearLabel = new JLabel("Year: ");

		// Fields
		callNumberField = new JTextField(20);
		isbnField = new JTextField(20);
		titleField = new JTextField(20);
		mainAuthorField = new JTextField(20);
		publisherField = new JTextField(20);
		yearField = new JTextField(10);

		Font bItalic = new Font("Arial", Font.ITALIC, 15);
//		loginButton.setFont(bItalic);
		
		// Buttons
		JButton addButton = new JButton("Add");
		JButton cancelButton = new JButton("Cancel");

		callNumberLabel.setFont(bItalic);mainAuthorLabel.setFont(bItalic);addButton.setFont(bItalic);
		isbnLabel.setFont(bItalic);publisherLabel.setFont(bItalic);cancelButton.setFont(bItalic);
		titleLabel.setFont(bItalic);yearLabel.setFont(bItalic);
		
		// Add components to panel
		addBookForm.add(callNumberLabel);
		addBookForm.add(callNumberField);
		addBookForm.add(isbnLabel);
		addBookForm.add(isbnField);
		addBookForm.add(titleLabel);
		addBookForm.add(titleField);
		addBookForm.add(mainAuthorLabel);
		addBookForm.add(mainAuthorField);
		addBookForm.add(publisherLabel);
		addBookForm.add(publisherField);
		addBookForm.add(yearLabel);
		addBookForm.add(yearField);
		addBookForm.add(cancelButton);
		addBookForm.add(addButton);
		// Window
		final JFrame frame = new JFrame("Add Book");
		// Window Properties
		frame.pack();
		frame.setVisible(true);


		//Add content to the window.
		frame.add(addBookForm, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.setResizable(true);
		frame.setSize(640/2,320);
		frame.setLocation( 180, 100 );

		// Button Listeners
		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String callNumber;
				try{
					callNumber = callNumberField.getText();
				}
				catch(NumberFormatException numExcept){
					JOptionPane.showMessageDialog(null,
							"Invalid Call Number.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				};
				
				int isbn = 0;
				try{
					isbn = Integer.parseInt(isbnField.getText());
				}
				catch(NumberFormatException numExcept){
					JOptionPane.showMessageDialog(null,
							"Invalid ISBN.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				};

				String title = titleField.getText();
				if (title.equals("")) {
					JOptionPane.showMessageDialog(null,
							"Please fill in book title.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				String mainAuthor = mainAuthorField.getText();
				if (mainAuthor.equals("")) {
					JOptionPane.showMessageDialog(null,
							"Please fill in main author.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				String publisher = publisherField.getText();
				if (publisher.equals("")) {
					JOptionPane.showMessageDialog(null,
							"Please fill in publisher.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				int year = 0;
				try{
					year = Integer.parseInt(yearField.getText());
				}
				catch(NumberFormatException numExcept){
					JOptionPane.showMessageDialog(null,
							"Invalid year.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				};
				Librarian.addNewBook(callNumber, isbn, title, mainAuthor, publisher, year);
				frame.setVisible(false);
			}
		});

		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
		});
	}
	
	private void openViewOutForm(){
		// Add check overdue Form
		JPanel viewOutForm = new JPanel();
		
		final String[] columnNames = {"Call Number", "Copy #", "Out Date", "Due Date", "Out"};
		Object[][] data = {};

		outModel = new DefaultTableModel(data,columnNames);
	
		// Add table to view items
		viewOutTable = new JTable(outModel);
		
		TableColumn tc = viewOutTable.getColumnModel().getColumn(4);  
        tc.setCellEditor(viewOutTable.getDefaultEditor(Boolean.class));  
        tc.setCellRenderer(viewOutTable.getDefaultRenderer(Boolean.class));

		// Add table to view items
		JScrollPane scrollPane = new JScrollPane(viewOutTable);
		
		JLabel subjectsLabel = new JLabel("Subjects: ");
		final JTextField subjectsField = new JTextField(25);
		JButton searchButton = new JButton("Search");
		JPanel subjectsPanel = new JPanel();
		
		subjectsPanel.add(subjectsLabel, BorderLayout.LINE_START);
		subjectsPanel.add(subjectsField, BorderLayout.CENTER);
		subjectsPanel.add(searchButton, BorderLayout.LINE_END);
		
		Font bItalic = new Font("Arial", Font.ITALIC, 15);
//		loginButton.setFont(bItalic);
		
		
		// Buttons
		JButton sendSeleButton = new JButton("Send to selected");
		JButton sendAllButton = new JButton("Send to All");
		sendSeleButton.setFont(bItalic);
		sendAllButton.setFont(bItalic);
		
		subjectsLabel.setFont(bItalic);
		searchButton.setFont(bItalic);
		
		
		
		// Button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(sendSeleButton, BorderLayout.LINE_START);
		buttonPanel.add(sendAllButton, BorderLayout.CENTER);
		

		// Add components to panel
		scrollPane.setPreferredSize(new Dimension(480, 200));
		viewOutForm.add(scrollPane, BorderLayout.PAGE_START);
		viewOutForm.add(subjectsPanel, BorderLayout.CENTER);
		viewOutForm.add(buttonPanel, BorderLayout.PAGE_END);

		// Window
		final JFrame frame = new JFrame("View Out Items");
		// Window Properties
		frame.pack();
		frame.setVisible(true);


		//Add content to the window.
		frame.add(viewOutForm, BorderLayout.CENTER);


		frame.setVisible(true);
		frame.setResizable(true);
		frame.setSize(500,360);
		frame.setLocation( 140, 50 );

		// Button Listeners
		searchButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{			
				String subjects = subjectsField.getText();
				Librarian.checkedOutItems(subjects);
			}
		});


	}
	
	private void openViewPopForm(){
		// Add check overdue Form
		JPanel viewPopForm = new JPanel();

		final String[] columnNames = {"Call Number", "ISBN", "Title", "Main Author", "Publisher", "Year"};
		Object[][] data = {};

		popModel = new DefaultTableModel(data,columnNames);

		// Add table to view items
		viewPopTable = new JTable(popModel);
		
		// Add table to view items
		JScrollPane scrollPane = new JScrollPane(viewPopTable);
		
		JLabel topLabel = new JLabel("Top: ");
		final JTextField topField = new JTextField(10);
		JLabel yearLabel = new JLabel("of year: ");
		final JTextField yearField = new JTextField(10);
		JPanel topPanel = new JPanel();
		
		Font bItalic = new Font("Arial", Font.ITALIC, 15);
//		loginButton.setFont(bItalic);
		
		topLabel.setFont(bItalic);
		yearLabel.setFont(bItalic);
		
		
		topPanel.add(topLabel);
		topPanel.add(topField);
		topPanel.add(yearLabel);
		topPanel.add(yearField);
		
		// Buttons
		JButton showButton = new JButton("Show");
		showButton.setFont(bItalic);
		
		// Button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(showButton, BorderLayout.LINE_START);		

		// Add components to panel
		scrollPane.setPreferredSize(new Dimension(480, 200));
		viewPopForm.add(scrollPane, BorderLayout.PAGE_START);
		viewPopForm.add(topPanel, BorderLayout.CENTER);
		viewPopForm.add(buttonPanel, BorderLayout.PAGE_END);

		// Window
		final JFrame frame = new JFrame("View Popular Items");
		// Window Properties
		frame.pack();
		frame.setVisible(true);

		//Add content to the window.
		frame.add(viewPopForm, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.setResizable(true);
		frame.setSize(500,360);
		frame.setLocation( 140, 50 );


		// Button Listeners
		showButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{			
				int top = 0;
				try{
					top = Integer.parseInt(topField.getText());
				}
				catch(NumberFormatException numExcept){
					JOptionPane.showMessageDialog(null,
							"Invalid top number.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				};
				
				int year = 0;
				try{
					year = Integer.parseInt(yearField.getText());
				}
				catch(NumberFormatException numExcept){
					JOptionPane.showMessageDialog(null,
							"Invalid year.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				};
				
				Librarian.popularItems(top, year);
			}
		});


	}
	
	public JComponent getLibrarianPanel(){

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0, 1, 10, 10));
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );

		Font bItalic = new Font("Arial", Font.ITALIC, 30);
		
		JButton addBookButton = new JButton("Add Book");
		addBookButton.setFont(bItalic);
		
		JButton viewOutButton = new JButton("View Out Items");
		viewOutButton.setFont(bItalic);
		
		JButton viewPopularButton = new JButton("View Popular Items");
		viewPopularButton.setFont(bItalic);
		

		addBookButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				//Execute when button is pressed
				openAddBookForm();
			}
		});  

		viewOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				//Execute when button is pressed
				openViewOutForm();
			}
		});  

		viewPopularButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				//Execute when button is pressed
				openViewPopForm();
			}
		});  

		mainPanel.add(addBookButton);
		mainPanel.add(viewOutButton);
		mainPanel.add(viewPopularButton);

		return mainPanel;
	}
}

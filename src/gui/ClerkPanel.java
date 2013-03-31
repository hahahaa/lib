package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
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

import main.Clerk;

public class ClerkPanel {
	
	private JTextField bidField;
	private JTextField callNumberField;
	
	private JTextField copyNoField;
	
	private JPanel mainPanel;

	private Connection con;
	
	private Clerk clerk;
	
	public ClerkPanel(Connection con) {
		this.con = con;
		this.clerk = new Clerk(this.con);
	}

	private void openAddBorrowerForm(){
		JPanel addBorrowerForm = new JPanel();
		addBorrowerForm.setLayout(new GridLayout(0, 2, 10, 10));
		addBorrowerForm.setBorder(new EmptyBorder(10, 10, 10, 10) );
		
		JLabel passwordLabel = new JLabel("Password:");
		JLabel nameLabel = new JLabel("Name:");
		JLabel addressLabel = new JLabel("Address:");
		JLabel phoneLabel = new JLabel("Phone:");
		JLabel emailLabel = new JLabel("Email Address:");
		JLabel sinOrStNoLabel = new JLabel("SIN/Student #:");
		final String dateFormat = "MM/dd/yyyy";
		JLabel expiryDateLabel = new JLabel("Expiry Date (mm/dd/yyyy):");
		JLabel typeLabel = new JLabel("Type:");

		final JTextField passwordField = new JTextField();
		final JTextField nameField = new JTextField();
		final JTextField addressField = new JTextField();
		final JTextField phoneField = new JTextField();
		final JTextField emailField = new JTextField();
		final JTextField sinOrStNoField = new JTextField();
		final JTextField expiryDateField = new JTextField();
		String[] types = {"", "Student", "Faculty", "Staff"};
		final JComboBox typeComboBox = new JComboBox(types);
		
		JButton addButton = new JButton("Add");
		JButton cancelButton = new JButton("Cancel");

		addBorrowerForm.add(nameLabel);
		addBorrowerForm.add(nameField);
		addBorrowerForm.add(passwordLabel);
		addBorrowerForm.add(passwordField);
		addBorrowerForm.add(addressLabel);
		addBorrowerForm.add(addressField);
		addBorrowerForm.add(phoneLabel);
		addBorrowerForm.add(phoneField);
		addBorrowerForm.add(emailLabel);
		addBorrowerForm.add(emailField);
		addBorrowerForm.add(sinOrStNoLabel);
		addBorrowerForm.add(sinOrStNoField);
		addBorrowerForm.add(expiryDateLabel);
		addBorrowerForm.add(expiryDateField);
		addBorrowerForm.add(typeLabel);
		addBorrowerForm.add(typeComboBox);

		addBorrowerForm.add(cancelButton);
		addBorrowerForm.add(addButton);
		
		final JFrame frame = new JFrame("Add Borrower");
		frame.pack();
		frame.setVisible(true);
		frame.add(addBorrowerForm, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.setResizable(true);
		frame.setSize(500,400);
		frame.setLocation(50,50);

		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{	
				String password = passwordField.getText();
				if (password.equals("")) {
					JOptionPane.showMessageDialog(null,
							"Please enter a password",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				String name = nameField.getText();
				if (name.equals("")) {
					JOptionPane.showMessageDialog(null,
							"Please enter your name",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				String address = addressField.getText();
				if (address.equals("")) {
					JOptionPane.showMessageDialog(null,
							"Please enter your address",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				int phone = 0;
				try{
					phone = Integer.parseInt(phoneField.getText());
				}
				catch(NumberFormatException numExcept){
					JOptionPane.showMessageDialog(null,
							"Please enter a valid phone number",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				};

				String email = emailField.getText();
				if (email.equals("")) {
					JOptionPane.showMessageDialog(null,
							"Please enter your an email address",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				int sinOrStNo = 0;
				try{
					sinOrStNo = Integer.parseInt(sinOrStNoField.getText());
				}
				catch(NumberFormatException numExcept){
					JOptionPane.showMessageDialog(null,
							"Invalid SIN/Student number",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				Calendar cal = java.util.Calendar.getInstance(); 
				SimpleDateFormat df = new SimpleDateFormat(dateFormat);
				java.util.Date tempDate = new java.util.Date(cal.getTimeInMillis());
				
				try {
					tempDate = df.parse(expiryDateField.getText());
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				Date expiryDate = new Date(tempDate.getTime());
				if (expiryDate.equals(null)) {
					JOptionPane.showMessageDialog(null,
							"Please fill in an expiry date.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				String type = (String)typeComboBox.getSelectedItem();
				if (type.equals("")) {
					JOptionPane.showMessageDialog(null,
							"Please select borrower type.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				clerk.addBorrower(password, name, address, phone, email, sinOrStNo, expiryDate, type);
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
	
	private void openCheckoutForm(){
		// Add checkout Form
		JPanel checkoutForm = new JPanel();
		// Set form layout
		checkoutForm.setLayout(new GridLayout(0, 2, 10, 10));
		checkoutForm.setBorder(new EmptyBorder(10, 10, 10, 10) );

		Font bItalic = new Font("Arial", Font.ITALIC, 15);
//		loginButton.setFont(bItalic);
		
		
		// Field Labels
		JLabel bidLabel = new JLabel("Bid: ");
		JLabel callNumberLabel = new JLabel("Call Numbers(separated by ;): ");
		// Fields
		bidField = new JTextField(10); 
		callNumberField = new JTextField(10);
		
		// Buttons
		JButton checkoutButton = new JButton("Checkout");
		JButton cancelButton = new JButton("Cancel");

		bidLabel.setFont(bItalic);
		callNumberLabel.setFont(bItalic);
		checkoutButton.setFont(bItalic);
		cancelButton.setFont(bItalic);
		
		// Add components to panel
		checkoutForm.add(bidLabel);
		checkoutForm.add(bidField);
		checkoutForm.add(callNumberLabel);
		checkoutForm.add(callNumberField);		
		checkoutForm.add(cancelButton);		
		checkoutForm.add(checkoutButton);


		// Window
		final JFrame frame = new JFrame("Checkout");
		// Window Properties
		frame.pack();
		frame.setVisible(true);

		//Add content to the window.
		frame.add(checkoutForm, BorderLayout.CENTER);

		
		frame.setVisible(true);
		frame.setResizable(true);
		frame.setSize(550,360/2);
		frame.setLocation( 100, 140 );

		// Button Listeners
		checkoutButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				int bid = 0;
				try{
					bid = Integer.parseInt(bidField.getText());
				}
				catch(NumberFormatException numExcept){
					JOptionPane.showMessageDialog(null,
							"Invalid bid.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				};
				
				String callNumber = callNumberField.getText();
				ArrayList<String> callNumbers = new ArrayList<String>();
				if (callNumber.equals("")) {
					JOptionPane.showMessageDialog(null,
							"Please fill in call numbers.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				clerk.checkoutItems(bid, callNumbers);
			}
		});

		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
		});
	}
	
	private void openReturnForm(){
		// Add checkout Form
		JPanel returnForm = new JPanel();
		// Set form layout
		returnForm.setLayout(new GridLayout(0, 2, 10, 10));
		returnForm.setBorder(new EmptyBorder(10, 10, 10, 10) );

		Font bItalic = new Font("Arial", Font.ITALIC, 15);
//		loginButton.setFont(bItalic);
		
		// Field Labels
		JLabel callNumberLabel = new JLabel("Call Number: ");
		JLabel copyNoLabel = new JLabel("Copy Number: ");
		// Fields
		callNumberField = new JTextField(10);
		copyNoField = new JTextField(10);
		
		// Buttons
		JButton returnButton = new JButton("Return");
		JButton cancelButton = new JButton("Cancel");
		
		callNumberLabel.setFont(bItalic);
		copyNoLabel.setFont(bItalic);
		returnButton.setFont(bItalic);
		cancelButton.setFont(bItalic);
		
		
		

		// Add components to panel
		returnForm.add(callNumberLabel);
		returnForm.add(callNumberField);
		returnForm.add(copyNoLabel);
		returnForm.add(copyNoField);
		
		returnForm.add(cancelButton);
		returnForm.add(returnButton);

		// Window
		final JFrame frame = new JFrame("Checkout");
		// Window Properties
		frame.pack();
		frame.setVisible(true);

		//Add content to the window.
		frame.add(returnForm, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.setResizable(true);
		frame.setSize(640/2,360/2);
		frame.setLocation( 180, 140 );

		// Button Listeners
		returnButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{				
				String callNumber = callNumberField.getText();
				if (callNumber.equals("")) {
					JOptionPane.showMessageDialog(null,
							"Please fill in call number.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				int copyNo = 0;
				try{
					copyNo = Integer.parseInt(bidField.getText());
				}
				catch(NumberFormatException numExcept){
					JOptionPane.showMessageDialog(null,
							"Invalid copy number.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				};
				
				clerk.processReturn(callNumber, copyNo);
			}
		});

		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
		});
	}
	
	private void openOverdueForm(){

		
		JPanel overdueForm = new JPanel();

		overdueForm.setBorder(new EmptyBorder(10, 10, 10, 10) );

		overdueForm.setLayout(new GridLayout(0, 1, 10, 10));
		
		final String[] columnNames = {"Call Number", "Copy #", "Title", "Bid", "Name", "Select"};
		Object[][] data = {};

		final DefaultTableModel model = new DefaultTableModel(data,columnNames);

		// Add table to view items
		JTable overdueTable = new JTable(model);
		
		TableColumn tc = overdueTable.getColumnModel().getColumn(5);  
        tc.setCellEditor(overdueTable.getDefaultEditor(Boolean.class));  
        tc.setCellRenderer(overdueTable.getDefaultRenderer(Boolean.class));
        
        ArrayList<String> list = clerk.checkOverDueItems();
        String[] temp;
		
        for(int i = 0; i < list.size(); i++){
        	temp = list.get(i).split(";");
        	System.out.println(temp);
    		model.insertRow(overdueTable.getRowCount(),new Object[]{temp[0], temp[1], temp[2], temp[3], temp[4], new Boolean(false)});
        }
		
		Font bItalic = new Font("Arial", Font.ITALIC, 15);
//		loginButton.setFont(bItalic);
		
		// Buttons
		JButton sendSeleButton = new JButton("Send to selected");
		JButton sendAllButton = new JButton("Send to All");
		
		sendSeleButton.setFont(bItalic);
		sendAllButton.setFont(bItalic);
		

		// Button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(sendSeleButton);
		buttonPanel.add(sendAllButton);
		
		
		// Add table to view items
		JScrollPane scrollPane = new JScrollPane(overdueTable);

		// Add components to panel
		scrollPane.setPreferredSize(new Dimension(480, 200));
		
			overdueForm.add(scrollPane, BorderLayout.CENTER);
			overdueForm.add(buttonPanel, BorderLayout.CENTER);	

		// Window
		final JFrame frame = new JFrame("Overdue Items");
		// Window Properties
		frame.pack();
		frame.setVisible(true);

		//Add content to the window.
		frame.add(overdueForm, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.setResizable(true);
		frame.setSize(750,300);
		frame.setLocation( 50, 50 );
		

		// Button Listeners
		sendSeleButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{			
				ArrayList<String> bids = new ArrayList<String>();
				for(int x=0; x<model.getRowCount(); x++){
					if(model.getValueAt(x, 5).equals(true)){
						bids.add((String) model.getValueAt(x, 3));
					}
				}
				for(String bid : bids){
					System.out.println(bid);
				}
			}
		});
		
		sendAllButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{			
				ArrayList<String> bids = new ArrayList<String>();
				for(int x=0; x<model.getRowCount(); x++){
						bids.add((String) model.getValueAt(x, 3));
				}
				for(String bid : bids){
					System.out.println(bid);
				}
			}
		});


	}


	public JComponent getClerkPanel(){

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0, 2, 10, 10));
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
		
		JButton addBorrowerButton = new JButton("Add Borrower");
		JButton checkoutButton = new JButton("Checkout");
		JButton processReturnButton = new JButton("Process Return");
		JButton checkOverdueButton = new JButton("Check Overdue");

		addBorrowerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				//Execute when button is pressed
				openAddBorrowerForm();
			}
		});  

		checkoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				//Execute when button is pressed
				openCheckoutForm();
			}
		});  

		processReturnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				//Execute when button is pressed
				openReturnForm();
			}
		});
		checkOverdueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				//Execute when button is pressed
				openOverdueForm();
			}
		});  

		mainPanel.add(addBorrowerButton);
		mainPanel.add(checkoutButton);
		mainPanel.add(processReturnButton);
		mainPanel.add(checkOverdueButton);

		return mainPanel;

	}
}

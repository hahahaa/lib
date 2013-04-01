package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
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

import main.Clerk;

public class ClerkPanel {
		
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
		frame.setLocation(100,140);

		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{	
				String password = passwordField.getText();
				if (password.compareTo("")== 0) {
					JOptionPane.showMessageDialog(null,
							"Please enter a password",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				String name = nameField.getText();
				if (name.compareTo("") == 0) {
					JOptionPane.showMessageDialog(null,
							"Please enter your name",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				String address = addressField.getText();
				if (address.compareTo("") == 0) {
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
				if (email.compareTo("") == 0) {
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
					JOptionPane.showMessageDialog(null,
							"Please fill in expiry date in format mm/dd/yyyy",
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
				Date expiryDate = new Date(tempDate.getTime());
				if (expiryDate.equals(null)) {
					JOptionPane.showMessageDialog(null,
							"Please fill in an expiry date",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				String type = (String)typeComboBox.getSelectedItem();
				if (type.compareTo("")==0) {
					JOptionPane.showMessageDialog(null,
							"Please select borrower type",
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
		
		JLabel bidLabel = new JLabel("Bid:");
		JLabel callNumberLabel = new JLabel("Call Numbers(separated by ; ):");
		
		final JTextField bidField = new JTextField(); 
		final JTextField callNumberField = new JTextField();
		
		JButton checkoutButton = new JButton("Checkout");
		JButton cancelButton = new JButton("Cancel");
		
		checkoutForm.add(bidLabel);
		checkoutForm.add(bidField);
		checkoutForm.add(callNumberLabel);
		checkoutForm.add(callNumberField);		
		checkoutForm.add(cancelButton);		
		checkoutForm.add(checkoutButton);

		final JFrame frame = new JFrame("Checkout");
		frame.pack();
		frame.setVisible(true);

		frame.add(checkoutForm, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(550,180);
		frame.setLocation(100, 140);

		checkoutButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				int bid = 0;
				try{
					bid = Integer.parseInt(bidField.getText());
				}
				catch(NumberFormatException numExcept){
					JOptionPane.showMessageDialog(null,
							"Bid entered is invalid",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				};
				
				String callNumber = callNumberField.getText();
				if (callNumber.compareTo("") == 0) {
					JOptionPane.showMessageDialog(null,
							"Please enter call number(s)",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				clerk.checkoutItem(bid, callNumber);
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
	
	private void openReturnForm(){
		JPanel returnForm = new JPanel();
		returnForm.setLayout(new GridLayout(0, 2, 10, 10));
		returnForm.setBorder(new EmptyBorder(10, 10, 10, 10) );

		JLabel callNumberLabel = new JLabel("Call Number: ");
		JLabel copyNoLabel = new JLabel("Copy Number: ");
		final JTextField callNumberField = new JTextField();
		final JTextField copyNoField = new JTextField();
		
		JButton returnButton = new JButton("Return");
		JButton cancelButton = new JButton("Cancel");
	
		returnForm.add(callNumberLabel);
		returnForm.add(callNumberField);
		returnForm.add(copyNoLabel);
		returnForm.add(copyNoField);
		
		returnForm.add(cancelButton);
		returnForm.add(returnButton);

		final JFrame frame = new JFrame("Return");
		frame.pack();
		frame.setVisible(true);

		frame.add(returnForm, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(320,180);
		frame.setLocation( 100, 140 );

		returnButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{				
				String callNumber = callNumberField.getText();
				if (callNumber.compareTo("") == 0) {
					JOptionPane.showMessageDialog(null,
							"Please enter a call number.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				int copyNo = 0;
				try{
					copyNo = Integer.parseInt(copyNoField.getText());
					if (copyNoField.getText().compareTo("") == 0) {
						JOptionPane.showMessageDialog(null,
								"Please enter a call number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				catch(NumberFormatException numExcept){
					JOptionPane.showMessageDialog(null,
							"Please enter a valid copy number",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				};
				clerk.processReturn(callNumber, copyNo);
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
	
	private void openOverdueForm(){

		JPanel overdueForm = new JPanel();

		overdueForm.setBorder(new EmptyBorder(10, 10, 10, 10) );
		
		final String[] columnNames = {"Call Number", "Copy #", "Title", "Bid", "Name", "Select"};
		Object[][] data = {};

		final DefaultTableModel tableModel = new DefaultTableModel(data,columnNames);

		JTable overdueTable = new JTable(tableModel);
		
		TableColumn tableColumn = overdueTable.getColumnModel().getColumn(5);  
        tableColumn.setCellEditor(overdueTable.getDefaultEditor(Boolean.class));  
        tableColumn.setCellRenderer(overdueTable.getDefaultRenderer(Boolean.class));
        
        final ArrayList<String> list = clerk.checkOverDueItems();
        String[] temp;
		
        for(int i = 0; i < list.size(); i++){
        	temp = list.get(i).split(";");
    		tableModel.insertRow(overdueTable.getRowCount(),new Object[]{temp[0], temp[1], temp[2], temp[3], temp[4], new Boolean(false)});
        }
				
		JButton sendSeletedButton = new JButton("Send to selected");
		JButton sendAllButton = new JButton("Send to All");
		JButton cancelButton = new JButton("Cancel");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(sendSeletedButton);
		buttonPanel.add(sendAllButton);
		buttonPanel.add(cancelButton);
		
		JScrollPane scrollPane = new JScrollPane(overdueTable);
		scrollPane.setPreferredSize(new Dimension(600, 400));
		overdueForm.add(scrollPane, BorderLayout.PAGE_START);
		overdueForm.add(buttonPanel, BorderLayout.CENTER);	

		final JFrame frame = new JFrame("Overdue Items");
		frame.pack();
		frame.setVisible(true);

		frame.add(overdueForm, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(620,500);
		frame.setLocation(100, 140);
		
		sendSeletedButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{			
				ArrayList<String> bids = new ArrayList<String>();
				for(int x=0; x<tableModel.getRowCount(); x++){
					if(tableModel.getValueAt(x, 5).equals(true)){
						bids.add((String) tableModel.getValueAt(x, 3));
					}
				}
				JOptionPane.showMessageDialog(null,
						"Notifications sent to all selected borrowers",
						"Notification",
						JOptionPane.INFORMATION_MESSAGE);

			}
		});
		
		sendAllButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{			
		        for(int i = 0; i < list.size(); i++){
		        	tableModel.setValueAt(new Boolean(true), i, 5);
		        }
		    	JOptionPane.showMessageDialog(null,
						"Notifications sent to all borrowers",
						"Notification",
						JOptionPane.INFORMATION_MESSAGE);

			}
		});
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
		});
	}


	public JComponent getClerkPanel(){

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0, 2, 10, 10));
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
		
		JButton addBorrowerButton = new JButton("Add Borrower");
		JButton checkoutButton = new JButton("Checkout");
		JButton processReturnButton = new JButton("Return");
		JButton checkOverdueButton = new JButton("Check Overdue Items");

		addBorrowerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				openAddBorrowerForm();
			}
		});  

		checkoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				openCheckoutForm();
			}
		});  

		processReturnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				openReturnForm();
			}
		});
		checkOverdueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
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

package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JOptionPane;

public class Clerk {

	public static void addBorrower(String password, String name, String address, int phone, 
			String email, int sinOrStNo, Date expiryDate, String type)
	{
		PreparedStatement  ps;

		try {
			ps = LibDB.con.prepareStatement("INSERT INTO borrower VALUES (bid_counter.nextval,?,?,?,?,?,?,?,?)");

			ps.setString(1, password);
			ps.setString(2, name);
			ps.setString(3, address);
			ps.setInt(4, phone);
			ps.setString(5, email);
			ps.setInt(6, sinOrStNo);
			java.sql.Date sqlDate = new java.sql.Date(expiryDate.getTime());
			ps.setDate(7, sqlDate);
			ps.setString(8, type);

			ps.executeUpdate();
			// commit work 
			LibDB.con.commit();
			ps.close();
			
			JOptionPane.showMessageDialog(null,
					"Borrower added.",
					"Information",
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (SQLException ex)
		{
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
			try 
			{
				// undo the insert
				LibDB.con.rollback();	
			}
			catch (SQLException ex2)
			{
				JOptionPane.showMessageDialog(null,
						"Message: " + ex2.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	// TODO display/print receipt
	public static void checkoutBooks(int bid, String callNumbers) {
		// Check borrower account validity
		if (!expired(bid)) {
			return;
		}
		
		String[] callNums = callNumbers.split(";");
		for(String callNumber : callNums) {
			checkOut(bid, callNumber.trim());
		}
	}


	// TODO remove HoldRequest once borrower picks up reserved copy.
	public static void checkOut(int bid, String callNumber) {

		PreparedStatement ps;
		ResultSet rs;
		
		int copyNo;
		
		// Try to find available copy
		try {
			ps = LibDB.con.prepareStatement( "SELECT * FROM BookCopy WHERE callNumber = ? AND status = 'in'" );
			ps.setString( 1, callNumber);

			rs = ps.executeQuery();
			
			
			// check to see if a copy is ready to be borrowed
			if(!rs.next()) {
				JOptionPane.showMessageDialog(null,
						"No Copies Available for " + callNumber,
						"Information",
						JOptionPane.INFORMATION_MESSAGE);
				ps.close();
				return;
			} else {
				copyNo = rs.getInt("copyNo");
				ps.close();
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Process Borrowing
		try {

			int borrowDuration = getBorrowDuration(getBorrowerType(bid));
			
			ps = LibDB.con.prepareStatement("INSERT INTO Borrowing VALUES (borid_counter.nextval, ?, ?, ?, SYSDATE, SYSDATE + ?)");
			ps.setInt(1, bid);
			ps.setString( 2, callNumber);
			ps.setInt(3, copyNo);
			ps.setInt(4, borrowDuration);

			ps.executeUpdate();
			
			PreparedStatement ps2;
			
			// Update status of item copy
			ps2 = LibDB.con.prepareStatement("UPDATE BookCopy SET status='out' WHERE BookCopy.callNumber = ? AND BookCopy.copyNo = ?");
			ps2.setString(1, callNumber);
			ps2.setInt(2, copyNo);
			
			ps2.executeUpdate();

			LibDB.con.commit();
			ps.close();
			ps2.close();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
			try {
				// undo the insert
				LibDB.con.rollback();
			} catch (SQLException ex2) {
				JOptionPane.showMessageDialog(null,
						"Message: " + ex2.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public static void processReturn(String callNumber, int copyNo)
	{
		int bid;
		int borid;
		Date outDate;
		Date dueDate;
		java.sql.Date sqlDate;
		String type;

		PreparedStatement ps;
		ResultSet rs;
		
		// Find borrower of book
		try {
			// using inDate as dueDate
			ps = LibDB.con.prepareStatement(
					"SELECT borid, bid, outDate, inDate, status " +
					"FROM Borrowing, BookCopy " +
					"WHERE Borrowing.callNumber = BookCopy.callNumber AND Borrowing.CopyNo = BookCopy.copyNo AND callNumber = ? AND copyNo = ? AND status = 'out' " +
					"ORDER BY outDate DESC");
			ps.setString(1, callNumber);
			ps.setInt(2, copyNo);
			
			rs = ps.executeQuery();

			if (rs.next()) {
				if (rs.getString("status").equals("out")) {
					bid = rs.getInt("bid"); // Got the borrower.
					borid = rs.getInt("borid"); // For Fine
					outDate = rs.getDate("outDate"); // Also not very useful
					dueDate = rs.getDate("inDate"); // due date
				} else {
					JOptionPane.showMessageDialog(null,
							"Book is not checked out!",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					ps.close();
					return;
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"Book was never checked out!",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				ps.close();
				return;
			}
			ps.close();
		} catch (SQLException ex)	{
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// If book was overdue
		// Charge everybody $10
		if(new Date().after(dueDate)) {
			try {
				// Fine the borrower
				ps = LibDB.con.prepareStatement("INSERT INTO Fine VALUES (fid_counter.nextval, 10, SYSDATE, NULL, ?)");
				ps.setInt(1, borid);

				ps.executeUpdate();
				
				LibDB.con.commit();
				ps.close();
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(null,
						"Message: " + ex.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);
				try {
					// undo the insert
					LibDB.con.rollback();
				} catch (SQLException ex2) {
					JOptionPane.showMessageDialog(null,
							"Message: " + ex2.getMessage(),
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		// TODO send email to hold requester
		// if book was on hold
		int bidHolding;
		if(onHold(callNumber)) {
			try {
				// Change item copy status to on hold.
				ps = LibDB.con.prepareStatement("UPDATE BookCopy SET status='on hold' WHERE BookCopy.callNumber = ? AND BookCopy.copyNo = ?");
				ps.setString( 1, callNumber);
				ps.setInt(2, copyNo);
				
				ps.executeUpdate();
				LibDB.con.commit();
				ps.close();
				
				// find hold requester and send message/email
				try {
					ps = LibDB.con.prepareStatement(
							"SELECT hid, bid, callNumber, issuedDate " +
							"FROM HoldRequest " +
							"WHERE callNumber = ? " +
							"ORDER BY issedDate ASC");
					ps.setString(1, callNumber);
					
					rs = ps.executeQuery();
					
					if (rs.next()) {
						bidHolding = rs.getInt("bid"); // get hold requester
					}
					
					ps.close();
					
				} catch (SQLException ex)	{
					JOptionPane.showMessageDialog(null,
							"Message: " + ex.getMessage(),
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(null,
						"Message: " + ex.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);
				try {
					// undo the insert
					LibDB.con.rollback();
				} catch (SQLException ex2) {
					JOptionPane.showMessageDialog(null,
							"Message: " + ex2.getMessage(),
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		
		
		
	}

	public void checkOverdue()
	{
		Dictionary dueItem = new Hashtable();
		Statement stmt;
		ResultSet rs;

		int bid;
		String callNumber;
		String copyNo;
		String name;
		String emailAddress;

		SimpleDateFormat fm = new SimpleDateFormat( "dd/MM/yy" ); // Edit as needed
		java.sql.Date sqlDate;
		java.util.Date utilDate;

		try
		{
			stmt = LibDB.con.createStatement();

			// If we decide not to use indate as duedate, this line of code need to be changed.
			// Our table definition needs to be changed so that inDate can be null.
			rs = stmt.executeQuery( "SELECT callNumber, copyNo, bid, name, emailAddress, inDate FROM Borrowing, Borrower WHERE sysdate > inDate and status = 'out'" );

			if ( rs.next() )
			{
				System.out.println( "Items that are past due are:" );
				System.out.println();
			}
			else
			{
				System.out.println( "No items are past due." );
			}

			while ( rs.next() )
			{
				bid = rs.getInt( "bid" );
				callNumber = rs.getString( "callNumber" );
				copyNo = rs.getString( "copyNo" );
				name = rs.getString( "name" );
				emailAddress = rs.getString( "emailAddress" );

				sqlDate = rs.getDate( "inDate" );	// get SQL Date type date, may not need it

				/*
				utilDate.setTime( sqlDate.getTime() );	// change date into Java Date type

				dateString = fm.format( utilDate );	// parse the date into a string using the format specified in fm
				 */

				// Change format to make it look nicer later
				System.out.println( "Item Call Number: " + callNumber );
				System.out.println( "Item Copy Number: " + copyNo );
				System.out.println( "Borrower ID: " + bid );
				System.out.println( "Name: " + name );
				System.out.println( "Email: " + emailAddress );
				System.out.println();
			}
		}
		catch ( SQLException ex )
		{
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static boolean onHold(String callNumber) {
		boolean onHold = false;;
		PreparedStatement ps;
		ResultSet rs;
		try {
			// See if on hold
			ps = LibDB.con.prepareStatement("SELECT hid FROM HoldRequest WHERE callNumber = ?");
			ps.setString(1, callNumber);

			rs = ps.executeQuery();

			if(rs.next()){
				onHold = true;
			} else {
				onHold = false;
			}
		} catch(SQLException ex) {
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
		return onHold;
	}

	public static boolean expired(int bid) {

		boolean expired = true;
		PreparedStatement  ps;
		ResultSet  rs;

		try
		{
			ps = LibDB.con.prepareStatement("SELECT * FROM Borrower WHERE bid = ? AND expiryDate <= TRUNC(SYSDATE)");
			ps.setInt(1, bid);
			
			rs = ps.executeQuery();

			if (!rs.next()) {
				JOptionPane.showMessageDialog(null,
						"Account expired or does not exist!",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				expired = true;
			} else 
				expired = false;

			// close the statement; 
			// the ResultSet will also be closed
			ps.close();
		}
		catch (SQLException ex)
		{
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
		return expired;
	}
	
	public static String getBorrowerType(int bid) {

		PreparedStatement  ps;
		ResultSet  rs;

		try
		{
			ps = LibDB.con.prepareStatement("SELECT type FROM Borrower WHERE bid = ?");
			ps.setInt(1, bid);
			
			rs = ps.executeQuery();

			if (!rs.next()) {
				JOptionPane.showMessageDialog(null,
						"No type associated with Borrower account.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				ps.close();
				return null;
			} else {
				ps.close();
				return rs.getString("type");
			}
			
		}
		catch (SQLException ex)
		{
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	public static int getBorrowDuration(String borrowerType) {
		int borrowDuration; // maximum borrow duration in days
		if (borrowerType.equals("Student")) {
			borrowDuration = 14;
		} else if (borrowerType.equals("Faculty")) {
			borrowDuration = 84;
		} else if (borrowerType.equals("Staff")) {
			borrowDuration = 42;
		} else {
			JOptionPane.showMessageDialog(null,
					"Unknown borrower acount type.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			borrowDuration = 0;
		}
		return borrowDuration;
	}

}

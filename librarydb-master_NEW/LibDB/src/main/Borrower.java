package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.swing.JOptionPane;

public class Borrower {
	private int bid;
	private String password;

	public void searchBooks(String title, String author, String subject){

		PreparedStatement  ps;
		ResultSet  rs;

		String titleStatement = "";
		String authStatement = "";
		String subStatement = "";

		if(!title.equals("")){
			titleStatement = 
					"(SELECT callNumber " +
							"FROM Book " +
							"WHERE title LIKE '%" +title+ "%')" +
							"UNION";
		}
		if(!author.equals("")){
			authStatement = 
					"(SELECT callNumber " +
							"FROM HasAuthor " +
							"WHERE name LIKE '%" +author+ "%')" +
							"UNION";
		}
		if(!subject.equals("")){
			subStatement = 
					"(SELECT callNumber " +
							"FROM Book " +
							"WHERE title LIKE '%" +subject+ "%')" +
							"UNION";
		}
		
		String queryString = titleStatement + authStatement + subStatement;
		queryString = queryString.substring(0, queryString.length()-5);
		
		try
		{
			ps = LibDB.con.prepareStatement(queryString);
			rs = ps.executeQuery();

			while(rs.next()){
				String callNumber = rs.getString("callNumber");
				
				int numCopiesIn = 0;
				int numCopiesOut = 0;
				
				// Get Number of Copies Checked In
				try{
					PreparedStatement  ps2;
					ResultSet  rs2;
					
					ps2 = LibDB.con.prepareStatement(
							"SELECT COUNT(*) " +
							"FROM BookCopy " +
							"WHERE status = 'in' AND callNumber = ?");
					ps2.setString(1, callNumber);
					
					rs2 = ps2.executeQuery();
					
					if(rs2.next()) {
						numCopiesIn = rs2.getInt(1);
					}

				} catch(SQLException exAuth){
					JOptionPane.showMessageDialog(null,
							"Message: " + exAuth.getMessage(),
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}

				// Get Number of Copies Checked Out
				try{
					PreparedStatement  ps2;
					ResultSet  rs2;
					
					ps2 = LibDB.con.prepareStatement(
							"SELECT COUNT(*) " +
							"FROM Book " +
							"WHERE status = 'out' AND callNumber = ?");
					ps2.setString(1, callNumber);
					
					rs2 = ps2.executeQuery();
					
					if(rs2.next()) {
						numCopiesOut = rs2.getInt(1);
					}
					
				} catch(SQLException exAuth){
					JOptionPane.showMessageDialog(null,
							"Message: " + exAuth.getMessage(),
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
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
	}

	// edit
	public void checkAccount(int bid, String password) {
		
		if (!authenticate(bid, password)) {
			return;
		}
		
		checkBorrowerOutItems(bid);
		checkBorrowerFines(bid);
		checkBorrowerHoldRequests( bid );
	}
	
	public void checkBorrowerOutItems(int bid) {
		PreparedStatement  ps;
		ResultSet  rs;

		// Out Items
		String callNumber;
		int copyNumber;
		Date outDate;
		
		try {
			ps = LibDB.con.prepareStatement(
					"SELECT callNumber, copyNo, outDate " +
					"FROM Borrowing, BookCopy " +
					"WHERE Borrowing.callNumber = BookCopy.callNumber AND Borrowing.copyNo = BookCopy.copyNo AND BookCopy.status = 'out' AND Borrowing.bid = ?");
			ps.setInt(1, bid);
			
			rs = ps.executeQuery();
			while(rs.next()) {
				callNumber = rs.getString("callNumber");
				copyNumber = rs.getInt("copyNo");
				outDate = rs.getDate("outDate");
			}
			ps.close();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void checkBorrowerFines(int bid) {
		PreparedStatement  ps;
		ResultSet  rs;

		// outstanding fees;
		int fid;
		int amount;
		Date issuedDate;
		
		String callNumber;
		int copyNumber;
		Date outDate;
		
		try
		{
			ps = LibDB.con.prepareStatement(
					"SELECT Borrowing.callNumber, Borrowing.copyNo, Fine.fid, Fine.amount, Fine.issuedDate " +
					"FROM Borrowing, Fine " +
					"WHERE Borrowing.borid = Fine.borid AND Fine.paidDate IS NULL AND Borrowing.bid = ?");
			ps.setInt(1, bid);
			
			rs = ps.executeQuery();
			while(rs.next()) {
				fid = rs.getInt("fid");
				amount = rs.getInt("amount");
				issuedDate = rs.getDate("issuedDate");
				
				callNumber = rs.getString("callNumber");
				copyNumber = rs.getInt("copyNo");
				outDate = rs.getDate("outDate");
			}
			ps.close();
		}
		catch (SQLException ex)
		{
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void checkBorrowerHoldRequests(int bid) {
		PreparedStatement  ps;
		ResultSet  rs;

		// outstanding fees;
		int hid;
		Date issuedDate;
		
		String callNumber;
		String title;
		String isbn;
		String mainAuthor;
		
		try
		{
			ps = LibDB.con.prepareStatement(
					"SELECT HoldRequest.hid, HoldRequest.issuedDate, Book.callNumber, Book.title, Book.isbn, Book.mainAuthor " +
					"FROM HoldRequest, Book " +
					"WHERE HoldRequest.callNumber = Book.callNumber AND HoldRequest.bid = ?");
			ps.setInt(1, bid);
			
			rs = ps.executeQuery();
			while(rs.next()) {
				hid = rs.getInt("hid");
				issuedDate = rs.getDate("issuedDate");
				
				callNumber = rs.getString("callNumber");
				title = rs.getString("title");
				isbn = rs.getString("isbn");
				mainAuthor = rs.getString("mainAuthor");
			}
			ps.close();
		}
		catch (SQLException ex)
		{
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	public static void placeHoldRequest(int bid, String password, String callNumber){
		
		if (!authenticate(bid, password)) {
			return;
		}
		
		PreparedStatement  ps;
		ResultSet  rs;
		
		// Check to see if borrower is already holding the item.
		try {
			ps = LibDB.con.prepareStatement(
					"SELECT COUNT(*) " +
					"FROM HoldRequest " +
					"WHERE bid = ? AND callNumber = ?");
			ps.setInt(1, bid);
			ps.setString(2, callNumber);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				if(rs.getInt(1) >= 1) {
					JOptionPane.showMessageDialog(null,
							"You already have a hold request on this item.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					ps.close();
					return;
				}
			}

			ps.close();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
		
		// Check to see if all copies of item is out
		try {
			ps = LibDB.con.prepareStatement(
					"SELECT COUNT(*) " +
					"FROM BookCopy " +
					"WHERE status <> 'out' AND callNumber = ?");
			ps.setString(1, callNumber);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				if(rs.getInt(1) >= 1) {
					JOptionPane.showMessageDialog(null,
							"One or more copies of the item is still checked in.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					ps.close();
					return;
				}
			}

			ps.close();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
		
		// Process the hold request
		try {
			java.util.Date utlDate = new java.util.Date();
			java.sql.Date sqlDate = new java.sql.Date(utlDate.getTime());
			
			ps = LibDB.con.prepareStatement("INSERT INTO HoldRequest VALUES (hid_counter.nextval,?,?,?)");
			ps.setInt(1, bid);
			ps.setString(2, callNumber);
			ps.setDate(3, sqlDate);
			
			ps.executeUpdate();
			// commit work 
			LibDB.con.commit();
			ps.close();
			
			JOptionPane.showMessageDialog(null,
					"Holdrequest added.",
					"Information",
					JOptionPane.INFORMATION_MESSAGE);
			
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
	
	
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	public static void payFine(int bid, String password, int fid) {
		
		if (!authenticate(bid, password)) {
			return;
		}
		
		PreparedStatement  ps;
		ResultSet  rs;
		
		int amount;
		Date issuedDate;
		
		// Check to see if fine is attached to borrower.
		try {
			ps = LibDB.con.prepareStatement(
					"SELECT Fine.amount, Fine.issuedDate " +
					"FROM Borrowing, Fine " +
					"WHERE Borrowing.borid = Fine.borid AND Fine.paidDate IS NULL AND Borrowing.bid = " + bid + " AND Fine.fid = " + fid);
			
			rs = ps.executeQuery();
			if (!rs.next()) {
				JOptionPane.showMessageDialog(null,
						"The fine ID was not found under your account.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				ps.close();
				return;
			}
			
			amount = rs.getInt("amount");
			issuedDate = rs.getDate("issuedDate");

			ps.close();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
		
		// Process Payment
		try {
			java.util.Date utlDate = new java.util.Date();
			java.sql.Date sqlDate = new java.sql.Date(utlDate.getTime());
			
			ps = LibDB.con.prepareStatement(
					"UPDATE Fine " +
					"SET paidDate=? " +
					"WHERE fid = ?");
			ps.setDate(1, sqlDate);
			ps.setInt(2, fid);
			
			ps.executeUpdate();
			// commit work 
			LibDB.con.commit();
			ps.close();
			
			JOptionPane.showMessageDialog(null,
					"Fine payed.",
					"Information",
					JOptionPane.INFORMATION_MESSAGE);
			
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

	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	public static boolean authenticate(int bid, String password){

		String pass = null;
		PreparedStatement  ps;
		ResultSet  rs;

		try
		{
			ps = LibDB.con.prepareStatement("SELECT password FROM borrower WHERE bid=" + bid);

			rs = ps.executeQuery();

			if(rs.next()){
				pass = rs.getString("password");
			}
			else
			{
				JOptionPane.showMessageDialog(null,
						"That bid does not exist!",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
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

		if(pass.equals(password)){
			return true;
		} else {
			JOptionPane.showMessageDialog(null,
					"Incorrect Password!",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

}
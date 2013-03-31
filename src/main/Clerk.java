package main;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

public class Clerk {

	private Connection con;

	public Clerk(Connection con) {
		this.con = con;
	}
	/**
	 * Adds a new borrower into the database
	 * 
	 * @param bid
	 * @param password
	 * @param name
	 * @param address
	 * @param phone
	 * @param emailAddress
	 * @param sinOrStNo
	 * @param expiryDate
	 * @param type
	 */

	public void addBorrower(String password, String name, String address, int phone, 
			String emailAddress, int sinOrStNo, Date expiryDate, String type){
		PreparedStatement prepStatement;
		try{

			String sqlQuery = "INSERT INTO borrower VALUES (bid_count.nextval,?,?,?,?.?.?.?.?)";
			prepStatement = con.prepareStatement(sqlQuery);
			prepStatement.setString(1, password);
			prepStatement.setString(2, name);
			prepStatement.setString(3, address);
			prepStatement.setInt(4, phone);
			prepStatement.setString(5, emailAddress);
			prepStatement.setInt(6, sinOrStNo);
			prepStatement.setDate(7, expiryDate);
			prepStatement.setString(8, type);

			prepStatement.executeUpdate();
			con.commit();
			prepStatement.close();

			JOptionPane.showMessageDialog(null,
					"New Borrower added",
					"Notification",
					JOptionPane.INFORMATION_MESSAGE);

		} catch(SQLException e){
			displayExceptionError(e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				displayExceptionError(e1);
			}
		}

	}

	public void checkoutItems(int bid, ArrayList<String> callNumbers){
		if (hasExpired(bid))
			return;
		
		for (int i = 0; i < callNumbers.size(); i++)
			checkout(bid, callNumbers.get(i));
		
		return;
	}
	
	/**
	 * processes the return of a book with the corresponding callNumber
	 * and copyNo
	 * 
	 * @param callNumber
	 * @param copyNo
	 */
	public void processReturn(String callNumber, int copyNo){
		PreparedStatement prepStatement;
		ResultSet result;
		String sqlQuery;
		Calendar cal = java.util.Calendar.getInstance(); 
		Date currDate = new Date(cal.getTimeInMillis());	
		Date dueDate;
		@SuppressWarnings("unused")
		int bid;
		int borid;
		int bidToNotify;
		String email = "";
		
		try{
			// find out the borrower of the book
			sqlQuery = "SELECT borid, bid, inDate, status" +
					    "FROM Borrowing, BookCopy" +  
					    "WHERE Borrowing.callNumber=BookCopy.callNumber AND" +
					    "Borrowing.copyNo = BookCopy.copyNo AND" +
					    "Borrowing.callNumber = 1001 AND Borrowing.copyNo = 1 AND" +
					    "BookCopy.status = 'out'"+
					    "ORDER BY outDate DESC";

			prepStatement = con.prepareStatement(sqlQuery);
			prepStatement.setString(1, callNumber);
			prepStatement.setInt(2, copyNo);
			result = prepStatement.executeQuery();
			
			if (result.next()){ 
				// found the borrower
				bid = result.getInt("bid");
				borid = result.getInt("borid");
				dueDate = result.getDate("inDate");
				prepStatement.close();
				// delete the tuple from Borrowing table
				sqlQuery = "DELETE FROM Borrowing"+
						   "WHERE borid = ?";
				prepStatement = con.prepareStatement(sqlQuery);
				prepStatement.setInt(1, borid);
				prepStatement.execute();
				con.commit();
				prepStatement.close();
				result.close();
			}else {
				JOptionPane.showMessageDialog(null,
						"Book is not checked out!",
						"Notification",
						JOptionPane.INFORMATION_MESSAGE);
				prepStatement.close();
				result.close();
				return;
			}
			
			//check for due date
			if (dueDate.after(currDate)){
				// book is overdue -> fine all borrower 5 dollars
				sqlQuery = "INSERT INTO Fine VALUES (fid_counter.nextval, 10, SYSDATE, NULL, ?)";
				prepStatement = con.prepareStatement(sqlQuery);
				prepStatement.setInt(1, borid);
				prepStatement.executeUpdate();
				con.commit();
				prepStatement.close();	
			}
			
			//check if another borrower place this book on hold
			if ((bidToNotify = isOnHold(callNumber)) > 0){
				// book is on hold --> update book copy status
				sqlQuery = "UPDATE BookCopy SET status='on hold' WHERE BookCopy.callNumber = ? AND BookCopy.copyNo = ?";
				prepStatement = con.prepareStatement(sqlQuery);
				prepStatement.setString(1, callNumber);
				prepStatement.setInt(2, copyNo);
				prepStatement.executeUpdate();
				con.commit();
				prepStatement.close();
				
				// find borrower's email address
				sqlQuery = "SELECT emailAddress" +
							"FROM Borrower" +
							"WHERE bid = ? ";
				prepStatement = con.prepareStatement(sqlQuery);
				prepStatement.setInt(1, bidToNotify);
				result = prepStatement.executeQuery();
				
				if(result.next()){
					email = result.getString("emailAddress");
				}	
				prepStatement.close();
				// show message sent with pop up
				JOptionPane.showMessageDialog(null,
						"Email has been sent to borrower " + email,
						"Notification",
						JOptionPane.INFORMATION_MESSAGE);
			}
			
		}catch(SQLException e){
			displayExceptionError(e);
			try{
				con.rollback();
			}catch(SQLException e1){
				displayExceptionError(e1);
			}
		}
	}

	public ArrayList<String> checkOverDueItems(){
		Statement statement;
		ResultSet result;
		String sqlQuery;
		
		int index = 0;
		
		ArrayList<Integer> bids = new ArrayList<Integer>();
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> callNumbers = new ArrayList<String>();
		ArrayList<Integer> copyNos = new ArrayList<Integer>();
		//ArrayList<Date> inDates = new ArrayList<Date>();
		//ArrayList<String> emails = new ArrayList<String>();
		
		ArrayList<String> list = new ArrayList<String>();
		try
		{
		
			statement = con.createStatement();
			
			// find bid, name , title, call number, copy no., indate, email 
			sqlQuery = "SELECT distinct Borrower.bid, Borrower.name, Borrowing.copyNo, Book.title, Borrowing.callNumber " +
					"FROM Borrowing, Borrower, Book, BookCopy WHERE Borrowing.bid = Borrower.bid and " +
					"Borrowing.callNumber = Book.callNumber and BookCopy.callNumber = Borrowing.callNumber " +
					"and Borrowing.inDate < SYSDATE and BookCopy.status = 'out'";
			result = statement.executeQuery(sqlQuery);
			while (result.next()){
				// store values
				bids.add(result.getInt("bid"));
				names.add(result.getString("name"));
				titles.add(result.getString("title"));
				callNumbers.add(result.getString("callNumber"));
				copyNos.add(result.getInt("copyNo"));
				//inDates.add(result.getDate("inDate"));
				//emails.add(result.getString("emailAddress"));
				index++;
			}
			statement.close();
			result.close();
			for (int i = 0; i < index; i++) {
				list.add(callNumbers.get(i) + ";" +  Integer.toString(copyNos.get(i)) + ";" + titles.get(i) + ";" 
						+ Integer.toString(bids.get(i)) + ";" + names.get(i));
			}
						
		} catch ( SQLException e ){
			displayExceptionError(e);
		}
		return list;
	}
	/**
	 * Checks if borrower with corresponding bid has expired
	 * 
	 * @param bid
	 * @return true if borrower is expired, false otherwise
	 */
	private boolean hasExpired(int bid){
		PreparedStatement prepStatement;
		ResultSet result;
		boolean hasExpired = true;

		try {
			String sqlQuery = "SELECT * " +
							  "FROM Borrower " +
							  "WHERE bid = ? AND expiryDate <= TRUNC(SYSDATE)";
			prepStatement = con.prepareStatement(sqlQuery);
			prepStatement.setInt(1, bid);
			result = prepStatement.executeQuery();
			if(result.next()){   							//    <----------------------------------- Check this when testing !!!
				JOptionPane.showMessageDialog(null,
						"Account expired!",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				hasExpired = false;
			}
			prepStatement.close();
			result.close();


		} catch (SQLException e) {
			displayExceptionError(e);
		}
		return hasExpired;
	}

	/**
	 * checks out a copy of item with callNumber if it is 
	 * currently available in the library
	 * 
	 * @param bid
	 * @param callNumber
	 */
	private void checkout(int bid, String callNumber){
		PreparedStatement prepStatement;
		PreparedStatement prepStatement2;
		ResultSet result;
		int copyNo;
		String sqlQuery;

		try {	
			// search for available copies
			sqlQuery = "SELECT * " +
					   "FROM BookCopy " +
					   "WHERE callNumber = ? AND status = 'in'";
			prepStatement = con.prepareStatement(sqlQuery);
			prepStatement.setString(1, callNumber);
			result = prepStatement.executeQuery();
			if(!result.next()){
				JOptionPane.showMessageDialog(null,
						"No copies are currently available for " + callNumber,
						"Notification",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			} else {
				copyNo = result.getInt("copyNo");
			}
			prepStatement.close();
			result.close();


			// process borrowing
			Date returnDate = getReturnDate(getBorrowerType(bid));
			// insert int Borrowing table
			sqlQuery = "INSERT INTO Borrowing VALUES (borid_counter.nextval, ?, ?, ?, SYSDATE, ?)"; 
			prepStatement = con.prepareStatement(sqlQuery);
			prepStatement.setInt(1, bid);
			prepStatement.setString(2, callNumber);
			prepStatement.setInt(3, copyNo);
			prepStatement.setDate(4, returnDate);
			prepStatement.executeUpdate();
			
			// update BookCopy table
			sqlQuery = "UPDATE BookCopy SET status='out' WHERE BookCopy.callNumber = ? AND BookCopy.copyNo = ?";
			prepStatement2 = con.prepareStatement(sqlQuery);
			prepStatement2.setString(1, callNumber);
			prepStatement2.setInt(2, copyNo);
			prepStatement2.executeUpdate();
			con.commit();	
			prepStatement.close();
			prepStatement2.close();

		} catch (SQLException e) {
			displayExceptionError(e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				displayExceptionError(e1);
			}
			return;
		}	

	}

	/**
	 * Displays the exception error message in JOptionPane
	 * 
	 * @param e 
	 * 		exception thrown
	 */
	private void displayExceptionError(Exception e){
		JOptionPane.showMessageDialog(null,
				e.getMessage(),
				"Error",
				JOptionPane.ERROR_MESSAGE);	
		return;
	}


	/**
	 * 
	 * Returns the 'type' of borrower with corresponding bid
	 *  
	 * 
	 * @param bid
	 * @return returns the 'type' if found,	returns null otherwise
	 * 	
	 */
	private String getBorrowerType(int bid) throws SQLException{

		PreparedStatement  prepStatement;
		ResultSet  result;

		String sqlQuery = "SELECT type " +
						  "FROM Borrower " +
						  "WHERE bid = ?";
		prepStatement = con.prepareStatement(sqlQuery);
		prepStatement.setInt(1, bid);

		result = prepStatement.executeQuery();

		if (!result.next()) {
			JOptionPane.showMessageDialog(null,
					"No type is associated with the borrower's account",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			prepStatement.close();
			result.close();

			return null;
		} else {
			prepStatement.close();
			result.close();
			return result.getString("type");
		}

	}

	/**
	 * 
	 * Determines the date of return based on borrowerType
	 * 
	 * @param borrowerType
	 * @return +14 if Student, +84 if Faculty, +42 if Staff,  0 otherwise
	 */
	private Date getReturnDate(String borrowerType) {
		Calendar cal = java.util.Calendar.getInstance(); 
		Date currDate = new Date(cal.getTimeInMillis());	
		int borrowDuration;
		
		if (borrowerType.equals("Student")) {
			borrowDuration = 14;
		} 
		else if (borrowerType.equals("Faculty")) {
			borrowDuration = 84;
		} 
		else if (borrowerType.equals("Staff")) {
			borrowDuration = 42;
		} 
		else {
			JOptionPane.showMessageDialog(null,
					"Unknown borrower acount type",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			borrowDuration = 0;
		}
		Date returnDate = new Date(currDate.getTime() + TimeUnit.DAYS.toMillis( borrowDuration ));
		return returnDate;
		
	}
	
	/**
	 * checks if book with corresponding callNumber is on hold
	 * 
	 * @param callNumber
	 * @return true if the book is on hold, false otherwise
	 */
	private int isOnHold(String callNumber) throws SQLException{
		int bid = -1;
		PreparedStatement prepStatement;
		ResultSet result;
		String sqlQuery = "SELECT bid" +
				          "FROM HoldRequest" +
				          "WHERE callNumber = ?" +
				          "ORDER BY issuedDate ASC";
		prepStatement = con.prepareStatement(sqlQuery);
		prepStatement.setString(1, callNumber);
		result = prepStatement.executeQuery();

		if(result.next()){
			// it is on holds
			bid = result.getInt("bid");
		}
		prepStatement.close();
		result.close();

		return bid;
	}
	
}
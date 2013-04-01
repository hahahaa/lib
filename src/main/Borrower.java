package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/*
 * Implements the transactions that a borrower can make
 */
public class Borrower {

	private Connection con;

	public Borrower(Connection con) {
		this.con = con;
	}

	/**
	 * 
	 * @param title
	 * @param author
	 * @param subject
	 * @return a list of books that match the search together with the number of copies that are in and out.
	 */
	public ArrayList<String[]> searchBook(String title, String author, String subject){

		ArrayList<String[]> finalResult = new ArrayList<String[]>();
		String[] row; 

		PreparedStatement prepared;
		ResultSet result;

		String titleQuery = "";
		String authorQuery = "";
		String authorQueryInBook = "";
		String subjectQuery = "";

		if ( title.compareTo("")!=0 ){
			titleQuery = "(SELECT callNumber " +
					"FROM Book " +
					"WHERE title LIKE '%" + title + "%')" +
					"UNION";
		}

		if (author.compareTo("")!=0){
			authorQuery = "(SELECT callNumber " +
					"FROM HasAuthor " +
					"WHERE name LIKE '%" + author + "%')" +
					"UNION";
			authorQueryInBook = "(SELECT callNumber " +
					"FROM Book " +
					"WHERE mainAuthor LIKE '%" + author + "%')" +
					"UNION";
		}

		if(subject.compareTo("")!=0){
			subjectQuery = 
					"(SELECT callNumber " +
							"FROM HasSubject " +
							"WHERE subject LIKE '%" + subject + "%')" +
							"UNION";
		}

		String finalQuery = titleQuery + authorQuery + authorQueryInBook + subjectQuery;


		if (subject.compareTo("")==0 && author.compareTo("")==0 && subject.compareTo("") == 0)
			finalQuery = "select callnumber from bookUNION";

		finalQuery = finalQuery.substring(0, finalQuery.length()-5);

		try {

			prepared = con.prepareStatement(finalQuery);
			result = prepared.executeQuery();

			while(result.next()){
				row = new String[8];
				String callNumber = result.getString("callNumber");
				row[0] = callNumber;

				String subQuery = "select title,mainauthor,publisher,year,isbn"
						+ " from book where callnumber = ?";
				PreparedStatement prep = con.prepareStatement(subQuery);
				prep.setString(1, callNumber);
				ResultSet subResult = prep.executeQuery();
				if (subResult.next()){
					row[1] = subResult.getString("title");
					row[2] = subResult.getString("mainauthor");
					row[3] = subResult.getString("publisher");
					row[4] = subResult.getString("year");
					row[5] = subResult.getString("isbn");
				}

				//PreparedStatement prep 

				// Get Number of Copies Checked In
				try{
					PreparedStatement  prepared2;
					ResultSet  result2;

					prepared2 = con.prepareStatement(
							"SELECT COUNT(*) " +
									"FROM BookCopy " +
							"WHERE status = 'in' AND callNumber = ?");
					prepared2.setString(1, callNumber);

					result2 = prepared2.executeQuery();

					if(result2.next()) {
						int numCopiesIn = result2.getInt(1);
						row[6] = Integer.toString(numCopiesIn);
					}

				} catch(SQLException e){
					printErrorMessage(e);
				}

				// Get Number of Copies Checked Out
				try{
					PreparedStatement  prepared2;
					ResultSet  result2;

					prepared2 = con.prepareStatement(
							"SELECT COUNT(*) " +
									"FROM BookCopy " +
							"WHERE status = 'out' AND callNumber = ?");
					prepared2.setString(1, callNumber);

					result2 = prepared2.executeQuery();

					if(result2.next()) {
						int numCopiesOut = result2.getInt(1);
						row[7] = Integer.toString(numCopiesOut);
					}

					finalResult.add(row);

				} catch(SQLException e){
					printErrorMessage(e);
				}
			}

		} catch (SQLException e) {
			printErrorMessage(e);
		}

		return finalResult;
	}

	/**
	 * 
	 * @param bid	ID of the borrower
	 * @return	a list of outstanding fines charged to the borrower
	 */
	public ArrayList<String[]> checkFine(int bid) {

		// outstanding fees;

		int fid;
		int amount;
		ArrayList<String[]> finalResult = new ArrayList<String[]>();

		try
		{
			PreparedStatement prepared = con.prepareStatement(
					"SELECT distinct fid, title, mainauthor, amount, issuedDate, outDate " +
							"FROM Borrowing, Fine, Book " +
							"WHERE Borrowing.borid = Fine.borid AND Fine.paidDate IS NULL " +
							"AND Borrowing.bid = ? and book.callnumber = borrowing.callnumber " +
					"order by fid");
			prepared.setInt(1, bid);

			ResultSet result = prepared.executeQuery();
			while(result.next()) {
				String[] row = new String [6];
				fid = result.getInt("fid");
				row[0] = Integer.toString(fid);
				row[1] = result.getString("title");
				row[2] = result.getString("mainAuthor");
				amount = result.getInt("amount");
				row[3] = Integer.toString(amount);
				row[4] = result.getDate("issuedDate").toString();
				row[5] = result.getDate("outDate").toString();
				finalResult.add(row);
			}
			prepared.close();
		}
		catch (SQLException e)
		{
			printErrorMessage(e);
		}

		return finalResult;
	}

	/**
	 * 
	 * @param bid	ID of the borrower
	 * @return	a list of books borrowed by the borrower
	 */
	public ArrayList<String[]> getBorrowedBook(int bid){
		int borrowID;
		ArrayList<String[]> finalResult = new ArrayList<String[]>();


		try {
			PreparedStatement prepared = con.prepareStatement(
					"SELECT distinct borid, title, mainauthor, publisher, outDate, inDate " +
							"FROM Borrowing, Book WHERE bid = ? and " +
					"book.callnumber = borrowing.callnumber order by borid");
			prepared.setInt(1, bid);

			ResultSet result = prepared.executeQuery();
			while(result.next()) {
				String[] row = new String[6];
				borrowID = result.getInt("borid");
				row[0] = Integer.toString(borrowID);
				row[1] = result.getString("title");
				row[2] = result.getString("mainAuthor");
				row[3] = result.getString("publisher");
				row[4] = result.getDate("outDate").toString();
				row[5] = result.getDate("inDate").toString();
				finalResult.add(row);
			}
			prepared.close();
		} catch (SQLException e) {
			printErrorMessage(e);
		}

		return finalResult;
	}

	/**
	 * 
	 * @param bid	ID of the borrower
	 * @return	a list of hold requests made by the borrower
	 */
	public ArrayList<String[]> checkHoldRequests(int bid) {


		ArrayList<String[]> finalResult = new ArrayList<String[]>();
		int hid;

		try
		{
			PreparedStatement prepared = con.prepareStatement(
					"SELECT hid, publisher, issuedDate, title, isbn, mainAuthor " +
							"FROM HoldRequest, Book WHERE " +
					"HoldRequest.callNumber = Book.callNumber AND bid = ? order by hid");
			prepared.setInt(1, bid);

			ResultSet result = prepared.executeQuery();
			while(result.next()) {
				String[] row = new String [6];
				hid = result.getInt("hid");
				row[0] = Integer.toString(hid);
				row[1] = result.getString("title");
				row[2] = result.getString("mainAuthor");
				row[3] = result.getString("publisher");
				row[4] = result.getString("isbn");
				row[5] = result.getDate("issuedDate").toString();
				finalResult.add(row);

			}
			prepared.close();
		}
		catch (SQLException e)
		{
			printErrorMessage(e);
		}

		return finalResult;
	}

	/**
	 * validate borrower and his/her password
	 * @param bid	borrower ID
	 * @param password
	 * @return	true if the ID exists and the password is correct, otherwise false
	 */
	public boolean accountValidated(int bid, String password){

		PreparedStatement prepared;
		ResultSet result;

		try
		{
			prepared = con.prepareStatement("SELECT password FROM borrower WHERE bid=? and password =?");
			prepared.setInt(1, bid);
			prepared.setString(2, password);
			result = prepared.executeQuery();

			if (result.next()){
				prepared.close();
				return true;
			}

		}
		catch (SQLException e)
		{
			printErrorMessage(e);
		}

		return false;

	}

	private void printErrorMessage(SQLException e){

		JOptionPane.showMessageDialog(null,
				"Message: " + e.getMessage(),
				"Error",
				JOptionPane.ERROR_MESSAGE);

	}
	
	/**
	 * generate a hold request
	 * @param bid	borrower ID
	 * @param callNumber
	 */
	public void placeHoldRequest(int bid, String callNumber){



		PreparedStatement prepared;
		ResultSet result;

		// Check to see if borrower is already holding the item.
		try {
			prepared = con.prepareStatement(
					"SELECT * FROM Book " +
					"WHERE callNumber = ?");
			prepared.setString(1, callNumber);
			result = prepared.executeQuery();
			if (!result.next()){
				JOptionPane.showMessageDialog(null,
						"No book with such CallNumber found",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			prepared = con.prepareStatement(
					"SELECT * FROM HoldRequest " +
					"WHERE bid = ? AND callNumber = ?");
			prepared.setInt(1, bid);
			prepared.setString(2, callNumber);

			result = prepared.executeQuery();
			if (result.next()) {

				JOptionPane.showMessageDialog(null,
						"You already have a hold request on this item.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				prepared.close();
				return;

			}

			prepared.close();
		} catch (SQLException e) {
			printErrorMessage(e);
		}

		// Check to see if any copy of item is in
		try {
			prepared = con.prepareStatement(
					"SELECT * FROM BookCopy " +
					"WHERE status = 'in' AND callNumber = ?");
			prepared.setString(1, callNumber);

			result = prepared.executeQuery();
			if (result.next()) {

				JOptionPane.showMessageDialog(null,
						"There are copies of the book available",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				prepared.close();
				return;

			}

			prepared.close();
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

			prepared = con.prepareStatement("INSERT INTO HoldRequest VALUES " +
					"(hid_counter.nextval,?,?,?)");
			prepared.setInt(1, bid);
			prepared.setString(2, callNumber);
			prepared.setDate(3, sqlDate);

			prepared.executeUpdate();
			// commit work 
			con.commit();
			prepared.close();

			JOptionPane.showMessageDialog(null,
					"Holdrequest added.",
					"Information",
					JOptionPane.INFORMATION_MESSAGE);

		} catch (SQLException e) {
			printErrorMessage(e);
			try {
				// undo the insert
				con.rollback();	
			} catch (SQLException e2) {
				printErrorMessage(e2);
			}
		}
	}
	
	/**
	 * Pay a fine charged to the borrower
	 * @param bid	borrower ID
	 * @param fid	fine ID
	 */
	public void payFine(int bid, int fid) {

		PreparedStatement  prepared;
		ResultSet  result;

		// Check to see if fine is attached to borrower.
		try {
			prepared = con.prepareStatement(
					"SELECT amount, issuedDate " +
							"FROM Borrowing, Fine " +
							"WHERE Borrowing.borid = Fine.borid AND Fine.paidDate IS NULL AND Borrowing.bid = " + bid + " AND Fine.fid = " + fid);

			result = prepared.executeQuery();
			if (!result.next()) {
				JOptionPane.showMessageDialog(null,
						"The fine ID was not found under your account.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				prepared.close();
				result.close();
				return;
			}
			result.close();
			prepared.close();
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

			prepared = con.prepareStatement(
					"UPDATE Fine " + "SET paidDate=? " + "WHERE fid = ?");
			prepared.setDate(1, sqlDate);
			prepared.setInt(2, fid);

			prepared.executeUpdate();
			// commit work 
			con.commit();
			prepared.close();

			JOptionPane.showMessageDialog(null,
					"Fine payed.",
					"Information",
					JOptionPane.INFORMATION_MESSAGE);

		} catch (SQLException e) {
			printErrorMessage(e);
			try {
				// undo the insert
				con.rollback();	
			} catch (SQLException e2) {
				printErrorMessage(e2);
			}
		}
	}

}
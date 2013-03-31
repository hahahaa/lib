package main;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import java.sql.Connection;

public class Borrower {

	private Connection con;

	public Borrower(Connection con) {
		this.con = con;
	}

	// TODO Search for books
	public void searchBook(String title, String author, String subject){

		PreparedStatement prepared;
		ResultSet result;

		String titleQuery = "";
		String authorQuery = "";
		String authorQueryInBook = "";
		String subjectQuery = "";

		if (title.compareTo("")!=0){
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
							"WHERE subject LIKE '%" + subject + "%')";
		}

		String finalQuery = titleQuery + authorQuery + authorQueryInBook + subjectQuery;

		try {

			prepared = con.prepareStatement(finalQuery);
			result = prepared.executeQuery();

			while(result.next()){
				String callNumber = result.getString("callNumber");

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
					}

				} catch(SQLException e){
					printErrorMessage(e);
				}
			}

		} catch (SQLException e) {
			printErrorMessage(e);
		}
	}
	// TODO Check account
	public void checkAccount(String bid, String password) throws IOException {
		if (validateAccount(bid, password) != false) return;
	}

	public void checkFine(int bid) {

		// outstanding fees;
		int fid;
		int amount;
		Date issuedDate;

		String callNumber;
		int copyNumber;
		Date outDate;

		try
		{
			PreparedStatement prepared = con.prepareStatement(
					"SELECT Borrowing.callNumber, Borrowing.copyNo, Fine.fid, Fine.amount, Fine.issuedDate " +
							"FROM Borrowing, Fine " +
					"WHERE Borrowing.borid = Fine.borid AND Fine.paidDate IS NULL AND Borrowing.borid = ?");
			prepared.setInt(1, bid);

			ResultSet result = prepared.executeQuery();
			while(result.next()) {
				fid = result.getInt("fid");
				amount = result.getInt("amount");
				issuedDate = result.getDate("issuedDate");

				callNumber = result.getString("callNumber");
				copyNumber = result.getInt("copyNo");
				outDate = result.getDate("outDate");
			}
			prepared.close();
		}
		catch (SQLException e)
		{
			printErrorMessage(e);
		}
	}

	private void checkBorrowedBook(String bid){

		String callNumber;
		String copyNumber;
		Date outDate;
		String title;


		try {
			PreparedStatement prepared = con.prepareStatement(
					"SELECT callNumber, copyNo, outDate, title " +
							"FROM Borrowing, Book " +
					"WHERE Borrowing.callNumber = Book.callNumber AND Borrowing.bid = ?");
			prepared.setString(1, bid);

			ResultSet result = prepared.executeQuery();
			while(result.next()) {
				callNumber = result.getString("callNumber");
				copyNumber = result.getString("copyNo");
				outDate = result.getDate("outDate");
				title = result.getString("title");
			}
			prepared.close();
		} catch (SQLException e) {
			printErrorMessage(e);
		}
	}

	public void checkHoldRequests(int bid) {

		// outstanding fees;
		int hid;
		Date issuedDate;

		String callNumber;
		String title;
		String isbn;
		String mainAuthor;

		try
		{
			PreparedStatement prepared = con.prepareStatement(
					"SELECT HoldRequest.hid, HoldRequest.issuedDate, Book.callNumber, Book.title, Book.isbn, Book.mainAuthor " +
							"FROM HoldRequest, Book " +
					"WHERE HoldRequest.callNumber = Book.callNumber AND HoldRequest.bid = ?");
			prepared.setInt(1, bid);

			ResultSet result = prepared.executeQuery();
			while(result.next()) {
				hid = result.getInt("hid");
				issuedDate = result.getDate("issuedDate");

				callNumber = result.getString("callNumber");
				title = result.getString("title");
				isbn = result.getString("isbn");
				mainAuthor = result.getString("mainAuthor");
			}
			prepared.close();
		}
		catch (SQLException e)
		{
			printErrorMessage(e);
		}
	}

	private boolean validateAccount(String bid, String password){

		PreparedStatement prepared;
		ResultSet result;

		try
		{
			prepared = con.prepareStatement("SELECT password FROM borrower WHERE bid=" + bid);
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
	// TODO Place a hold
	public void placeHoldRequest(int bid, String password, String callNumber){

		PreparedStatement prepared;
		ResultSet result;

		// Check to see if borrower is already holding the item.
		try {
			prepared = con.prepareStatement(
					"SELECT COUNT(*) " +
							"FROM HoldRequest " +
					"WHERE bid = ? AND callNumber = ?");
			prepared.setInt(1, bid);
			prepared.setString(2, callNumber);

			result = prepared.executeQuery();
			if (result.next()) {
				if(result.getInt(1) >= 1) {
					JOptionPane.showMessageDialog(null,
							"You already have a hold request on this item.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					prepared.close();
					return;
				}
			}

			prepared.close();
		} catch (SQLException e) {
			printErrorMessage(e);
		}

		// Check to see if all copies of item is out
		try {
			prepared = con.prepareStatement(
					"SELECT COUNT(*) " +
							"FROM BookCopy " +
					"WHERE status <> 'out' AND callNumber = ?");
			prepared.setString(1, callNumber);

			result = prepared.executeQuery();
			if (result.next()) {
				if(result.getInt(1) >= 1) {
					JOptionPane.showMessageDialog(null,
							"One or more copies of the item is still checked in.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					prepared.close();
					return;
				}
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
	// TODO Pay a fine
	public void payFine(int bid, String password, int fid) {


		PreparedStatement  prepared;
		ResultSet  result;

		int amount;
		Date issuedDate;

		// Check to see if fine is attached to borrower.
		try {
			prepared = con.prepareStatement(
					"SELECT Fine.amount, Fine.issuedDate " +
							"FROM Borrowing, Fine " +
							"WHERE Borrowing.borid = Fine.borid AND Fine.paidDate IS NULL AND Borrowing.bid = " + bid + " AND Fine.fid = " + fid);

			result = prepared.executeQuery();
			if (!result.next()) {
				JOptionPane.showMessageDialog(null,
						"The fine ID was not found under your account.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				prepared.close();
				return;
			}

			amount = result.getInt("amount");
			issuedDate = result.getDate("issuedDate");

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
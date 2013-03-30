package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Connection;

import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JOptionPane;

public class Librarian {
	
	private Connection con;
	
	private final int NUM_CHECKED_OUT_REPORT_COL = 5;
	
	public Librarian(Connection con) {
		this.con = con;
	}
	
	/**
	 * precondition: callNumber, title, mainAuthor, publisher are not null
	 */
	public void addBook(String callNumber, int isbn, String title, String mainAuthor, String publisher, int year) {
		PreparedStatement  ps;
		try {
			if(!hasBook(callNumber)) {
				ps = con.prepareStatement("INSERT INTO Book VALUES (branch_counter.nextval,?,?,?,?,?,?)");
				
				ps.setString(1, callNumber);
				ps.setInt(2, isbn);
				ps.setString(3, title);
				ps.setString(4, mainAuthor);
				ps.setString(5, publisher);
				ps.setInt(6, year);
				
				ps.executeUpdate();
				con.commit();
				ps.close();	
			}
			
			addOneBookCopy(callNumber);
		}
		catch (SQLException ex) {	
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);	
			try {
				con.rollback();	
			}
			catch (SQLException ex2) {
				System.out.println("Message: " + ex2.getMessage());
				System.exit(-1);
			}
		}
	}
	
	/**
	 * If a subject is provided the report lists only books related to that subject, 
	 * otherwise all the books that are out are listed by the report.
	 * The following attributes are shown on the report
	 * callNumber, copyNo, Out Date, In Date, isOverdue
	 * if isOverDue is 1 if Due Date < Current Date
	 * otherwise isOverDue is 0 
	 * 
	 * @param subject	the subject to search for
	 * @return a report with all the books have been checked out
	 */
	public ArrayList<String[]> getCheckedOutReport(String subject)
	{
		ArrayList<String[]> report = new ArrayList<String[]>();
		Date dueDate;
		Calendar cal = java.util.Calendar.getInstance();
		Date currDate = new Date(cal.getTimeInMillis());
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			if(subject.compareTo("") == 0)
				ps = con.prepareStatement(	"SELECT Borrowing.callNumber, Borrowing.copyNo, Borrowing.outDate, Borrowing.inDate" +
											"FROM Borrowing" +
											"ORDER BY Borrowing.callNumber");
			else
			{
				ps = con.prepareStatement(	"SELECT Borrowing.callNumber, Borrowing.copyNo, Borrowing.outDate, Borrowing.inDate" +
											"FROM Borrowing, HasSubject" +
											"WHERE Borrowing.callNumber = HasSubject.callNumber AND HasSubject.subject = ?" +
											"ORDER BY Borrowing.callNumber");
				ps.setString(1, subject);
			}
			
			rs = ps.executeQuery();
			while(rs.next())
			{
				String[] row = new String[NUM_CHECKED_OUT_REPORT_COL];
				
				row[0] = rs.getString("callNumber");
				row[1] = rs.getString("copyNo");
				row[2] = rs.getDate("outDate").toString();
				dueDate = rs.getDate("inDate");
				row[3] = dueDate.toString();
				if (dueDate.after(currDate))
					row[4] = "1";
				else
					row[4] = "0";
				
				report.add(row);
			}
			rs.close();
			ps.close();
		}
		catch (SQLException ex) {	
			JOptionPane.showMessageDialog(null,
					"Message: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);	
			try {
				con.rollback();	
			}
			catch (SQLException ex2) {
				System.out.println("Message: " + ex2.getMessage());
				System.exit(-1);
			}
		}
		
		return report;
	}
	
	/**
	 * @return	true if there exists a book with the given callNumber
	 * @throws SQLException 
	 */
	private boolean hasBook(String callNumber) throws SQLException {
		PreparedStatement ps = con.prepareStatement("SELECT callNumber FROM Book WHERE Book.callNumber = ?");
		ps.setString(1, callNumber);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			rs.close();
			ps.close();
			return true;
		}
		else
		{
			rs.close();
			ps.close();
			return false;
		}
	}
	
	/**
	 * add one book copy of a book with the given callNumber
	 */
	private void addOneBookCopy(String callNumber) throws SQLException {
		String copyNumber;
		PreparedStatement ps = con.prepareStatement("SELECT MAX(copyNo) as copyNumber FROM BookCopy WHERE BookCopy.callNumber = ?");
		ps.setString(1, callNumber);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) 
			copyNumber = rs.getString("copyNumber");
		else
			throw new SQLException("No copyNo is returned");
		rs.close();
		
		int copyNum = Integer.parseInt(copyNumber);
		copyNum++;
		ps = con.prepareStatement("INSERT INTO BookCopy VALUES (?,?,?)");
		ps.setString(1, callNumber);
		ps.setString(2, Integer.toString(copyNum));
		ps.setString(3, "in");
		
		ps.executeUpdate();
		con.commit();
		ps.close();
	}
}
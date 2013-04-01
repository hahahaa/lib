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
	private final int NUM_POPURLAR_REPROT_COL = 7;
	
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
				ps = con.prepareStatement("INSERT INTO Book VALUES (?,?,?,?,?,?)");
				
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
				JOptionPane.showMessageDialog(null,
						"Message: " + ex2.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);	
				System.exit(-1);
			}
		}
		JOptionPane.showMessageDialog(null,
				"A copy of " + callNumber + " is added",
				"Successful",
				JOptionPane.INFORMATION_MESSAGE);	
	}
	
	/**
	 * If a subject is provided the report lists only books related to that subject, 
	 * otherwise all the books that are out are listed by the report.
	 * The following attributes are shown on the report
	 * callNumber, copyNo, Out Date, In Date, isOverdue
	 * 
	 * @param subject	the subject to search for
	 * @return a report with all the books have been checked out
	 */
	public ArrayList<String[]> getCheckedOutReport(String subject) {
		ArrayList<String[]> report = new ArrayList<String[]>();
		Date dueDate;
		Calendar cal = java.util.Calendar.getInstance();
		Date currDate = new Date(cal.getTimeInMillis());
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			if(subject == null || subject.compareTo("") == 0)
				ps = con.prepareStatement(	"SELECT callNumber, copyNo, outDate, inDate " +
											"FROM Borrowing " +
											"ORDER BY callNumber");
			else
			{
				ps = con.prepareStatement(	"SELECT Borrowing.callNumber, Borrowing.copyNo, Borrowing.outDate, Borrowing.inDate " +
											"FROM Borrowing, HasSubject " +
											"WHERE Borrowing.callNumber = HasSubject.callNumber AND HasSubject.subject = ? " +
											"ORDER BY Borrowing.callNumber");
				ps.setString(1, subject.trim());
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
				if(dueDate.after(currDate))
					row[4] = "";
				else
					row[4] = "Overdue";
				
				report.add(row);
			}
			rs.close();
			ps.close();
			
			if(report.isEmpty())				
				JOptionPane.showMessageDialog(null,
						"No record, the search is case-sensitive",
						"",
						JOptionPane.INFORMATION_MESSAGE);				
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
				JOptionPane.showMessageDialog(null,
						"Message: " + ex2.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);	
				System.exit(-1);
			}
		}
		return report;
	}
	
	/**
	 * Lists out the top n books that where borrowed the most times during that year.
	 * The books are ordered by the number of times they were borrowed.
	 * The report contains the books' callNumber, isbn, title, main author, 
	 * publisher, year and the number of times the books were borrowed
	 * @param year
	 * @param n	number of books to be shown on the report, must > 0
	 * @return	a report with the most popular n items in a given year
	 */
	public ArrayList<String[]> getPopularReport(int year, int n) {
		ArrayList<String[]> report = new ArrayList<String[]>();
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = con.prepareStatement("select * from book natural join ( " +
					"select callNumber, count(*) as scount from borrowing " +
					"where EXTRACT(year from borrowing.outdate) = ? " +
					"group by callnumber " +
					"order by scount desc) " +
					"where ROWNUM <= ?");
			
			ps.setString(1, Integer.toString(n));
			ps.setString(2, Integer.toString(year));
			
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				String[] row = new String[NUM_POPURLAR_REPROT_COL];
				
				row[0] = rs.getString("callNumber");
				row[1] = Integer.toString(rs.getInt("isbn"));
				row[2] = rs.getString("title");
				row[3] = rs.getString("mainAuthor");
				row[4] = rs.getString("publisher");
				row[5] = Integer.toString(rs.getInt("year"));
				row[6] = Integer.toString(rs.getInt("scount"));
				
				report.add(row);
			}
			rs.close();
			ps.close();	
			
			if(report.isEmpty())				
				JOptionPane.showMessageDialog(null,
						"No record",
						"",
						JOptionPane.INFORMATION_MESSAGE);		
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
				JOptionPane.showMessageDialog(null,
						"Message: " + ex2.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);	
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
		boolean result = rs.next();
		rs.close();
		ps.close();
		return result;
	}
	
	/**
	 * add one book copy of a book with the given callNumber
	 */
	private void addOneBookCopy(String callNumber) throws SQLException {
		int copyNumber;
		PreparedStatement ps = con.prepareStatement("SELECT MAX(copyNo) as copyNumber FROM BookCopy WHERE callNumber = ?");
		ps.setString(1, callNumber);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			copyNumber = rs.getInt("copyNumber");
		}
		else
			copyNumber = 0;
		rs.close();
		
		copyNumber++;
		ps = con.prepareStatement("INSERT INTO BookCopy VALUES (?,?,?)");
		ps.setString(1, callNumber);
		ps.setInt(2, copyNumber);
		ps.setString(3, "in");
		
		ps.executeUpdate();
		con.commit();
		ps.close();
	}
}
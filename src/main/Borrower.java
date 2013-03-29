package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.swing.JOptionPane;

import java.sql.Connection;

public class Borrower {
	
	private Connection con;
	
	public Borrower(Connection con) {
		this.con = con;
	}
	
	// TODO Search for books
	// TODO Check account
	// TODO Place a hold
	// TODO Pay a fine
}
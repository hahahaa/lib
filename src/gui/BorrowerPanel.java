package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
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

import main.Borrower;

import java.sql.Connection;

public class BorrowerPanel {
	
	private Connection con;
	private JPanel borrowerPanel;
	
	public BorrowerPanel(Connection con) {
		this.con = con;
		
		//TODO initialize borrowerPanel, buttons, actionListeners
	}
	
	public JPanel getPanel() {
		return borrowerPanel;
	}
	
}
package gui;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.sql.Connection;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;

public class Menu extends JPanel {
	
	private Connection con;
	
	public Menu() {
		super(new GridLayout(1, 1));

		JTabbedPane tabbedPane = new JTabbedPane();
		
		BorrowerPanel borrower = new BorrowerPanel(con);
		tabbedPane.addTab("Borrower", borrower.getPanel());

//		ClerkPanel clerk = new ClerkPanel();
//		tabbedPane.addTab("Clerk", clerk.getClerkPanel());
//
//		LibrarianPanel librarian = new LibrarianPanel();
//		tabbedPane.addTab("Librarian", librarian.getLibrarianPanel());

		//Add the tabbed pane to this panel.
		add(tabbedPane);

		//The following line enables to use scrolling tabs.
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	public void createMenu(Connection connection) {
		this.con = connection;
		
		//Create and set up the window.
		JFrame frame = new JFrame("Library Database");
		frame.setPreferredSize(new Dimension(400, 150));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add content to the window.
		frame.add(new Menu(), BorderLayout.CENTER);
		
		//Display the window.
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);

		// center the frame
		Dimension d = frame.getToolkit().getScreenSize();
		Rectangle r = frame.getBounds();
		frame.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );
	}

}

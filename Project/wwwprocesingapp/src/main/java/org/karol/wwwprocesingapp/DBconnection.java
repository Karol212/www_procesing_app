package org.karol.wwwprocesingapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

/**
 * This class is responsible for connection with database (PostgreSQL)
 * @version 1.0.0 2015.10.27
 * @author Karol Riegel
 */
public class DBconnection {
	Connection dbConnection = null;

	/**
	 * This method checks the connection to database. 
	 * @return true - connection to database is correct, false - connection is not correct
	 */
	protected boolean checkConnectionWithDB()
	{
		boolean connectionOK = false;

		try {
		    Class.forName("org.postgresql.Driver");
		    dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/partners", "postgres", "baza"); 
		    connectionOK = true;
		    dbConnection.close();
		}catch(Exception ex)
		{
			System.out.println("Błąd bazy danych.\n" + ex + "\n");
			JOptionPane.showMessageDialog(null, "Wystąpił problem z połączeniem z bazą danych", "Błąd", JOptionPane.ERROR_MESSAGE);
		}
		
		return connectionOK;
	}
	
	/**
	 * This method add new partner to the database. The table has two columns ID (primary key) and partner name. 
	 * @param partnerName name of the partner which will be added to database
	 */
	protected void addNewPartner(String partnerName) {	
		try {
		    Class.forName("org.postgresql.Driver");
		    dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/partners",
		    		"postgres", "baza");
		    Statement stmt = dbConnection.createStatement();        
		    String query = "INSERT INTO partners_list (partner_name) SELECT '" + partnerName + "' WHERE NOT EXISTS ( SELECT partner_name FROM partners_list WHERE partner_name = '" + partnerName + "');";         
		    stmt.execute(query);
		    dbConnection.close();
		}catch(Exception ex)
		{	
			JOptionPane.showMessageDialog(null, "Problem z dodaniem partnera do bazy danych", "Błąd", JOptionPane.ERROR_MESSAGE);
		}
	}
}

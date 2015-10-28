package org.karol.wwwprocesingapp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * This class is responsible for parsing the website
 * @version 1.0.0 2015.10.27
 * @author Karol Riegel
 */
public class Parser {
	
	private PrintWriter save;
	private final String USER_AGENT = "Chrome";
	private static final String URL = "http://www.bluemedia.pl/o-nas/partnerzy-i-klienci";
	private StringBuffer result;
	
	public Parser () {	
	}
	
	/**
	 * This method get the source code of website
	 * @return result - the source code of website
	 */
	protected boolean sendGet() throws Exception {
		

		boolean isConnectionWithWebsite = false;
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(URL);

		// add request header
		request.addHeader("User-Agent", USER_AGENT);

		HttpResponse response = client.execute(request);

		System.out.println("Sending 'GET' request to URL : " + URL);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
		
		result = new StringBuffer();
		
		if (response.getStatusLine().getStatusCode() != 200) {
			JOptionPane.showMessageDialog(null, "Strona o podanym adresie nie istnieje", "Błąd", JOptionPane.ERROR_MESSAGE);
			isConnectionWithWebsite = false;
		} else {
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			isConnectionWithWebsite = true;
		}
		
		return isConnectionWithWebsite;
	}
	
	/**
	 * This method get the partners from source code of website.
	 * @param result - the source code of website 
	 * @param whereSave - 1 if data should be save in .txt file, 2 - save in database
	 */
	protected void getPartnersFromText(int whereSave)
	{
		DBconnection dbConnection = new DBconnection();
		
		String text = result.toString();
		
		String searchingStartOfList = "<ul>                            <li>";
		String charOfEnd = "<";
		String endOfPartnerList = "</li>                        </ul>";
		String newPartner = "<li>";
		String partnerName = "";
		
		boolean isStartOfPartners = false;
		boolean isEndOfPartners = false;
		boolean isFoundNewPartner = false;
		
		int index = 0;
		
		if (whereSave == 1) // if the user want to save the list of partners in .txt file
		{
			try {
				save = new PrintWriter("Partnerzy i klienci.txt"); // Create the .txt file for partners
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Wystąpił problem z zapisem danych do pliku tekstowego", "Błąd", JOptionPane.ERROR_MESSAGE);
			}
		}

		// searching the first partner
		do {
			// searching the text: "<ul> <li>"
			isStartOfPartners = text.startsWith(searchingStartOfList, index);
				
			if (isStartOfPartners) // if "<ul> <li>" is found
			{
				index += 36; // moving the index after <ul><li>
				
				isStartOfPartners = text.startsWith(charOfEnd, index); // checking if the next char is not '<'
				
				if (isStartOfPartners == true) // if the next char is '<', we do not have the first partner
					isStartOfPartners = false;
				else  						   // if the next char is not '<', we have got the first partner, exit the 'do...while' loop
					isStartOfPartners = true; 
			} else 
				index++;
			
		} while (isStartOfPartners == false);
		
		// searching other partners  
		do {
			// if char is not '<', we have the first letter of partner
			while (text.charAt(index) != '<')
			{	
				partnerName += text.charAt(index); // get the next letter and add it to the partnerName variable
				index++;
				isFoundNewPartner = true;
			}
		
			if(isFoundNewPartner)
			{
				System.out.println(partnerName);
					
				if (whereSave == 1)
					save.println(partnerName); // add the partner name to the .txt file
				else 
					dbConnection.addNewPartner(partnerName);
				
				isFoundNewPartner = false;
				partnerName = ""; 
			}
				
			isEndOfPartners = text.startsWith(endOfPartnerList, index); // checking if this is not the end of partner list
			
			if (isEndOfPartners == false) // if is not
			{
				do {
					index++; // increase the index until not find the new partner ('<li>')
				} while (text.startsWith(newPartner, index) == false);
					
				index += 4; // moving the index after <li>
			}
				
		} while (isEndOfPartners == false);
			
		if (whereSave == 1)
			save.close();
	}
}

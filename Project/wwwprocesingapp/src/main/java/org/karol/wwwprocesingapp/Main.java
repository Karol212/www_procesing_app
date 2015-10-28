package org.karol.wwwprocesingapp;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * This is the main class of application. It create the frame.
 * @version 1.0.0 2015.10.27
 * @author Karol Riegel
 */
public class Main
{ 
	public static void main( String[] args )
    {
		MainFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
    }
}

/**
 * This class is responsible for initializes the components (layout, labels, buttons etc.). This class creates the GUI.
 */
class MainFrame extends JFrame implements ActionListener 
{
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_WIDTH = 900;
	private static final int DEFAULT_HEIGHT = 250;
	private static final int FONTSIZE = 20;
	
	private JPanel generatingPartnersPanel,
				   linkPanel,
				   pathPanel;
	private JLabel linkLabel,
	 			   pathLabel;
	private JButton linkButton,
					generatePartnersButton,
					pathButton;
	private ButtonGroup group;
	private JRadioButton fileRadioButton,
						 databaseRadioButton;
	private String workingDir;
	
	Parser parser;
	DBconnection db;
	
	public MainFrame ()
	{
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setTitle("Aplikacja do przetwarzania strony WWW");
		setResizable(false);
		
		// Display a frame in center of the screen  
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dimension.width/2-this.getSize().width/2, dimension.height/2-this.getSize().height/2);

		// Initialize the three layouts for application frame
		initLayout();
		
		initComponentsInGeneratingPartnersPanel();
		initComponentsInLinkPanel();
		initComponentsInPathPanel();
		
		add(generatingPartnersPanel);
		add(linkPanel);
		add(pathPanel);
	}
	
	/**
	 * This method initializes the three layouts for main frame application. 
	 */
	private void initLayout()
	{
		setLayout(new GridLayout(3,1));
				
		// Creating the first layout for generating the partners of company
		generatingPartnersPanel = new JPanel();
		generatingPartnersPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
		generatingPartnersPanel.setBorder(BorderFactory.createEtchedBorder());
		
		// Creating the second layout for displaying the hyperlink to the website
		linkPanel = new JPanel();
		linkPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
				
		// Creating the third layout for displaying the hyperlink to the file path 
		pathPanel = new JPanel();
		pathPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));	
	}
	
	/**
	 * This method initializes the components for first panel. This is the panel with ButtonGroup with a choice of data storage location
	 * and button to generate the data.
	 */
	private void initComponentsInGeneratingPartnersPanel()
	{
		//Create the radio buttons.
	    fileRadioButton = new JRadioButton("Zapis partnerów do pliku .txt");
	    fileRadioButton.setSelected(true);

	    databaseRadioButton = new JRadioButton("Zapis partnerów do bazy danych");

	    //Group the radio buttons.
	    group = new ButtonGroup();
	    group.add(fileRadioButton);
	    group.add(databaseRadioButton);
		generatingPartnersPanel.add(fileRadioButton);
		generatingPartnersPanel.add(databaseRadioButton);
		
		// Create the button to generate the data
		generatePartnersButton = new JButton("Generuj");
		generatePartnersButton.setFont(new Font("Serif", Font.PLAIN, FONTSIZE));
		generatePartnersButton.addActionListener(this);
		generatingPartnersPanel.add(generatePartnersButton);
	}

	/**
	 * This method initializes the components for second panel. This is the panel with hyperlink to the company website
	 */
	private void initComponentsInLinkPanel()
	{
		linkLabel = new JLabel("Link do artukułu:");
		linkLabel.setFont(new Font("Serif", Font.PLAIN, FONTSIZE));
		linkPanel.add(linkLabel);
		
		linkButton = new JButton("<HTML><U>http://www.bluemedia.pl/o-nas/partnerzy-i-klienci</U></HTML>");
		linkButton.setFont(new Font("Serif", Font.PLAIN, FONTSIZE));
		linkButton.setForeground(Color.BLUE);
		linkButton.setHorizontalAlignment(SwingConstants.LEFT);
		linkButton.setBorderPainted(false);
		linkButton.setOpaque(false);
		linkButton.setBackground(Color.WHITE);
		linkButton.addActionListener(this);	
		linkPanel.add(linkButton);
	}
	
	/**
	 * This method initializes the components for third panel. This is the panel with hyperlink to the file path
	 */
	private void initComponentsInPathPanel() {
		pathLabel = new JLabel("Adres do pliku .txt z listą partnerów:");
		pathLabel.setFont(new Font("Serif", Font.PLAIN, FONTSIZE));
		pathPanel.add(pathLabel);
		
		workingDir = System.getProperty("user.dir");
		
		pathButton = new JButton(workingDir);
		pathButton.setFont(new Font("Serif", Font.PLAIN, FONTSIZE));
		pathButton.setForeground(Color.BLUE);
		pathButton.setHorizontalAlignment(SwingConstants.LEFT);
		pathButton.setBorderPainted(false);
		pathButton.setOpaque(false);
		pathButton.setBackground(Color.WHITE);
		pathButton.addActionListener(this);	
		pathPanel.add(pathButton);
	}
	
	/**
	 * This method is responsible for open the web browser and displaying the website
	 */
	private static void openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	        } catch (Exception e) {
	            e.printStackTrace();
		        JOptionPane.showMessageDialog(null, "Wystąpił problem z otworzeniem strony internetowej w przeglądarce", "Błąd", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}

	/**
	 * This method is responsible for open the web browser and displaying the website
	 */
	private static void openWebpage(URL url) {
	    try {
	        openWebpage(url.toURI());
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Wystąpił problem z otworzeniem strony internetowej w przeglądarce", "Błąd", JOptionPane.ERROR_MESSAGE);
	    }
	}

	/**
	 * This method supports the buttons. It say what each buttons should do.
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if(source == generatePartnersButton) {
			// new thread for generating the data with partners list
			new Thread()
			{
			    public void run() {
			    	try {
			    		parser = new Parser();
			    		boolean connectionOK = false;
			    		
			    		if (databaseRadioButton.isSelected()) {
			    			db = new DBconnection();
			    			connectionOK = db.checkConnectionWithDB();

			    			if (connectionOK) { // check if the connection to database is correct
			    				if (parser.sendGet()) { // check if the connection with website is correct
			    					parser.getPartnersFromText(2);
			    					JOptionPane.showMessageDialog(null, "Dane zostały wygenerowane poprawnie", "Proces zakończony", JOptionPane.INFORMATION_MESSAGE);
			    				} 
			    			}
			    		}
			    		else {
			    			if(parser.sendGet()) { // check if the connection with website is correct
			    				parser.getPartnersFromText(1);
								JOptionPane.showMessageDialog(null, "Dane zostały wygenerowane poprawnie", "Proces zakończony", JOptionPane.INFORMATION_MESSAGE);
			    			} 
			    		}
			    		
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "Wystąpił problem z pobraniem danych ze strony internetowej", "Błąd", JOptionPane.ERROR_MESSAGE);
					}
			    }
			}.start();
		}
		
		else if (source == linkButton) {
			try {
				openWebpage(new URL("http://www.bluemedia.pl/o-nas/partnerzy-i-klienci"));
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Wystąpił problem z otworzeniem strony internetowej w przeglądarce", "Błąd", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		else if (source == pathButton) {
			try {
				Desktop.getDesktop().open(new File(workingDir));
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Wystąpił problem z otworzeniem folderu z projektem", "Błąd", JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	
}
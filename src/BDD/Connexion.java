package BDD;

import java.sql.*;
import java.util.*;


public class Connexion extends Properties {
	
	
		Connection maConnexion = null;
	
		public Connexion() {}
		
		public Connection creeConnexion() {
			
	    	String url="jdbc:oracle:thin:@localhost:1521/XEPDB1";
	    	String username ="MANDIN";
	    	String password ="mandin";
			
				try {
					maConnexion = DriverManager.getConnection(url, username, password);
					System.out.println("Connection à la BDD réussie ! ");
				}
				catch (SQLException sqle) {
				System.out.println("Erreur connexion" + sqle.getMessage());
				}
				
			return maConnexion;
			 }
		
		
		public Connection fermerConnexion()
		{
			try {
				maConnexion.close();
				System.out.println("Fermeture de la connexion");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return maConnexion;
		}

}	


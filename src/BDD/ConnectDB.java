package BDD;

import java.sql.*;



public class ConnectDB {
	 
    public static void main(String[] args) {
    
    	/**
    	 * TUTO
    	 * https://www.youtube.com/watch?v=ea0HhETxOIs
    	 * https://mkyong.com/jdbc/connect-to-oracle-db-via-jdbc-driver-java/
    	 * https://stackoverflow.com/questions/18192521/ora-12505-tnslistener-does-not-currently-know-of-sid-given-in-connect-descript
    	 * 
    	 * jdbc:oracle:thin:@localhost:1521/XEPDB1
    	 * jdbc:oracle:thin:@localhost:1521:XE dans le cas ou username = SYSTEM
    	 */
    	
    	String url="jdbc:oracle:thin:@localhost:1521/XEPDB1";
    	String username ="MANDIN";
    	String password ="mandin";
    	
    	try {
			Connection connection = DriverManager.getConnection(url,username,password);
			System.out.println("Connection effectué ! ");
			
			String sql = "SELECT * FROM VEHICULES";
			Statement satement = connection.createStatement();
			ResultSet result = satement.executeQuery(sql);
			
			while(result.next())
			{
				String modele = result.getString(2);
				System.out.println(modele);
			}
			
    	} catch (Exception e) {
			System.out.println("Opps, error:");
			e.printStackTrace();
		}
    

    }
         	
}



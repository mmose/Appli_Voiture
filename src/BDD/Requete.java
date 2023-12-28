package BDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Requete {

	
	private ResultSet resulSet;
	private PreparedStatement requete;





	public void requete_select (Connection estconnect� ,String sql) throws SQLException
	{

		try {
		Statement statement=estconnect�.createStatement();
		resulSet = statement.executeQuery(sql);
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public void requete_insert (Connection estconnect�, String sql) throws SQLException
	{
		
		try {
		requete = estconnect�.prepareStatement(sql);
		requete.executeUpdate();
		}
		 catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
	}
	


	public ResultSet getResulSet() {
		return resulSet;
	}

	public void setResulSet(ResultSet resulSet) {
		this.resulSet = resulSet;
	}

	public PreparedStatement getRequete() {
		return requete;
	}


	
}


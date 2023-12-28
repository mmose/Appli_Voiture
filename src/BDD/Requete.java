package BDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Requete {

	
	private ResultSet resulSet;
	private PreparedStatement requete;





	public void requete_select (Connection estconnecté ,String sql) throws SQLException
	{

		try {
		Statement statement=estconnecté.createStatement();
		resulSet = statement.executeQuery(sql);
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public void requete_insert (Connection estconnecté, String sql) throws SQLException
	{
		
		try {
		requete = estconnecté.prepareStatement(sql);
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


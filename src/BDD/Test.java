package BDD;

import java.sql.Connection;
import java.sql.SQLException;

public class Test {

	public static void main(String[] args) throws SQLException {

		Connexion nouvelleconnection = new Connexion();
		Connection estconnect�;
		estconnect� = nouvelleconnection.creeConnexion();

		
		
		Requete r = new Requete();
		r.requete_select(estconnect�,"SELECT * FROM VEHICULES");
		while (r.getResulSet().next())
		{
			System.out.println(r.getResulSet().getString(2));
		}
		
		
		
	}

}

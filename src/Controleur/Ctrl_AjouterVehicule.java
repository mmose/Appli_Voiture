package Controleur;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.rmi.RemoteException;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import BDD.Requete;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;



public class Ctrl_AjouterVehicule {


	@FXML
	private VBox VBox_client;
	@FXML
	private Label titre_projet_rmi;
	@FXML
	private TextField input_kilometrage,input_matricule;
	@FXML
	private ComboBox<String> cmbx_modele;
	@FXML
	private DatePicker input_datematricule;
	@FXML
	private TextArea message_erreur;

	private Stage stage;
	
	@FXML
	private AnchorPane parent;
	
	Connection estconnecté;

	
	
	public void SessionConnectionOracle(Connection estconnecté)
	{
		this.estconnecté=estconnecté;
	}
	
	public void InitCombobox_Modele()
	{
		Requete r1 = new Requete();
		ResultSet resultat;
		try {
			r1.requete_select(estconnecté, "SELECT MODELE FROM MODELES ORDER BY NOCAT ASC");
			resultat = r1.getResulSet();
			
			 while (resultat.next())
			 {
				 cmbx_modele.getItems().add(resultat.getString(1));
			 }
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void ajoutervehicule() throws SQLException
	{
		message_erreur.clear();
		
		if (!(cmbx_modele.getSelectionModel().getSelectedItem()==null)&& !input_matricule.getText().isEmpty() && !input_kilometrage.getText().isEmpty() && !(input_datematricule.getValue()==null) && input_matricule.getText().length()==7)
		{
			
				try (
					     Statement s = estconnecté.createStatement()) {
					 
					    try {
					        s.executeUpdate("begin dbms_output.enable(); end;");
					        String formattedDate = input_datematricule.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
					        s.executeUpdate("begin AjouterVehicule('"+cmbx_modele.getSelectionModel().getSelectedItem().toString()+"','"+input_matricule.getText()+"','"+formattedDate+"','"+input_kilometrage.getText()+"'); end;");
					       
					        
					        try (CallableStatement call = estconnecté.prepareCall(
					            "declare "
					          + "  num integer := 1000;"
					          + "begin "
					          + "  dbms_output.get_lines(?, num);"
					          + "end;"
					        )) {
					            call.registerOutParameter(1, Types.ARRAY,
					                "DBMSOUTPUT_LINESARRAY");
					            call.execute();
					 
					            Array array = null;
					            try {
					                array = call.getArray(1);
					                Stream.of((Object[]) array.getArray())
					                      .forEach(System.out::println);
					                String[] stringArray = Arrays.asList((Object[]) array.getArray()).toArray(String[]::new);
					                
					                message_erreur.setWrapText(true);
					                String message = "";
					                
					                for (int i = 0; i < stringArray.length-1; i++) {
					                	
					                	message = message + stringArray[i]+"\n";
									}
					                message_erreur.setText(message);
					                
					            }
					            finally {
					                if (array != null)
					                    array.free();
					            }
					        }
					    }
		
					    finally {
					        s.executeUpdate("begin dbms_output.disable(); end;");
					    }
					}
		
		}
		else if (cmbx_modele.getSelectionModel().getSelectedItem()==null)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Ajouter véhicule");
			alert.setHeaderText("Modèle non sélectionné");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}
		else if (input_datematricule.getValue()==null)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Ajouter véhicule");
			alert.setHeaderText("Date non sélectionnée");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}
		
		else if (input_matricule.getText().isEmpty())
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Ajouter véhicule");
			alert.setHeaderText("Matricule non saisie");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}
		else if (input_kilometrage.getText().isEmpty())
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Ajouter véhicule");
			alert.setHeaderText("Kilométrage non saisie");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}
		else if (!(input_matricule.getText().length()==7))
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Ajouter véhicule");
			alert.setHeaderText("Le matricule doit être de 7 caractères");
			alert.setContentText("Vous avez saisie "+input_matricule.getText().length()+" caractères");
			alert.showAndWait();
		}

	}
	
/**
	public void ajoutervehiculeOLD() throws SQLException
	{
		message_erreur.clear();
		
		if (!(cmbx_modele.getSelectionModel().getSelectedItem()==null)&& !input_matricule.getText().isEmpty() && !input_kilometrage.getText().isEmpty() && !(input_datematricule.getValue()==null))
		
		{
				String query = "{call AjouterVehicule(?,?,?,?)}"; 
				CallableStatement statement = null;
				try {
					statement = estconnecté.prepareCall(query);
					statement.setString(1, cmbx_modele.getSelectionModel().getSelectedItem().toString());  
					statement.setString(2, input_matricule.getText());  
					String formattedDate = input_datematricule.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
					statement.setString(3, formattedDate);  
					statement.setString(4, input_kilometrage.getText());  
					statement.execute(); 
					System.out.println("Véhicule ajouté");
					message_erreur.setText("Véhicule ajouté");
				} catch (SQLException e) {
					System.out.println("Impossible d'ajouter le véhicule");
					message_erreur.setWrapText(true);
					message_erreur.setText("Impossible d'ajouter le véhicule \n : " +e.getMessage().toString());
					e.printStackTrace();
				}
		
		}
		else if (input_datematricule.getValue()==null)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Ajouter véhicule");
			alert.setHeaderText("Date non sélectionné");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}
		
		else if (cmbx_modele.getSelectionModel().getSelectedItem()==null)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Ajouter véhicule");
			alert.setHeaderText("Modèle non sélectionné");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}
		else if (input_matricule.getText().isEmpty())
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Ajouter véhicule");
			alert.setHeaderText("Matricule non saisie");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}
		else if (input_kilometrage.getText().isEmpty())
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Ajouter véhicule");
			alert.setHeaderText("Kilométrage non saisie");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}

	}
	*/
	
	
	
	public void rafraichir() throws ClassNotFoundException, IOException
	{

	}
	

	
	public void fermer() throws IOException 
	{
		System.out.println("Fermeture du client ");
		try {
			estconnecté.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.exit(0);	
	}
	
	
	public void reduire()
	{
	 stage.setIconified(true);
	}
	
	public void setPrimaryStage(Stage s) {
		this.stage = s;
	}




	
	
}

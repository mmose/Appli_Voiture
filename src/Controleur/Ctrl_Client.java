package Controleur;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.URL;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.DoubleConsumer;
import java.util.stream.Stream;

import BDD.Connexion;
import BDD.Requete;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;



public class Ctrl_Client {

	@FXML
	private VBox VBox_client;
	@FXML
	private Label titre_projet_rmi;
	@FXML
	private TableView tableview_vehicules_dispo;
	@FXML
	private TextArea message_erreur,area_vehicules_disponible;
	@FXML
	private TextField disponible_duree;
	@FXML
	private DatePicker disponible_quand;
	@FXML
	private ComboBox<String> cmbx_categorie;

	private Stage primaryStage;
	
	Connection estconnecté;
	
	
	public void NouvelleConnection()
	{
		Connexion nouvelleconnection = new Connexion();
		estconnecté = nouvelleconnection.creeConnexion();
	}
	
	public void init_cmbx_categorie()
	{
		Requete r1 = new Requete();
		ResultSet resultat;
		try {
			r1.requete_select(estconnecté, "SELECT categorie FROM CATEGORIES ORDER BY CATEGORIE ASC");
			resultat = r1.getResulSet();
			
			 while (resultat.next())
			 {
				 cmbx_categorie.getItems().add(resultat.getString(1));
			 }
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void RECHERCHE_vehicules_disponibles_periode() throws SQLException
	{
		area_vehicules_disponible.clear();
		
		if (!(cmbx_categorie.getSelectionModel().getSelectedItem()==null)  && !disponible_duree.getText().isEmpty() && !(disponible_quand.getValue()==null))
		{
				
			try (
				     Statement s = estconnecté.createStatement()) {
				 
				    try {
				        s.executeUpdate("begin dbms_output.enable(); end;");
						String formattedDate = disponible_quand.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
				        s.executeUpdate("begin VehiculesDisponibles('"+cmbx_categorie.getSelectionModel().getSelectedItem().toString()+"','"+formattedDate+"','"+disponible_duree.getText()+"'); end;");
				       
				        
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
				                
				                area_vehicules_disponible.setWrapText(true);
				                String message = "";
				                
				                /**
				                 * J'ai mis stringArray.length-1 pour enlever le "null" qui vient à la fin de la liste.
				                 * Par défaut: stringArray.length;
				                 */
				                for (int i = 0; i < stringArray.length-1; i++) {
				                	
				                	message = message + stringArray[i]+"\n";
								}
			                	area_vehicules_disponible.setText(message);
				                
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
		else if ((cmbx_categorie.getSelectionModel().getSelectedItem()==null))
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Recherche véhicule");
			alert.setHeaderText("Catégorie non sélectionnée");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}
		else if (disponible_duree.getText().isEmpty())
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Recherche véhicule");
			alert.setHeaderText("Durée non saisie");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}
		
		else if (disponible_quand.getValue()==null)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Recherche véhicule");
			alert.setHeaderText("Date non sélectionnée");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}

	}
	
	
	/*
	public void SAUVEGARDE_RECHERCHE_vehicules_disponibles_periode() throws SQLException
	{
		String query = "{call VEHICULESDISPONIBLES(?,?,?)}"; 
		CallableStatement statement = null;
		
		try {
			statement = estconnecté.prepareCall(query);
			statement.setString(1, cmbx_categorie.getSelectionModel().getSelectedItem().toString());   
			String formattedDate = disponible_quand.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
			statement.setString(2, formattedDate);
			statement.setString(3, disponible_duree.getText());

			statement.registerOutParameter(4, Types.VARCHAR);
			statement.execute();
			System.out.println(statement.getString(4));
			
		} catch (SQLException e) {
			System.out.println("Impossible d'effectuer la recherche");
			message_erreur.setWrapText(true);
			message_erreur.setText("Impossible d'effectuer une recherche \n : " +e.getMessage().toString());
			e.printStackTrace();
		}  
		

	}
	*/
	

	public Stage ajoutervehicule() throws IOException
	{
	   	   FXMLLoader loader = new FXMLLoader(
	   	   	     getClass().getResource(
	   	   	       "/Vue/AjouterVehicule.fxml"
	   	   	     )
	   	   	   );
	   	   	   
	   	   	  
	   	   	  Stage NouveauStage = new Stage();
	   	   	  //Stage stage;
	   	   	  //stage = (Stage) titre_projet_rmi.getScene().getWindow();
	   	   	  
	   	   	  Scene scene = new Scene(loader.load());
	   	   	  NouveauStage.setScene(scene);
	   	   	  NouveauStage.setResizable(false);
	   	   	  NouveauStage.show();
	   	   	  //stage.setScene(scene);
	   	   	  //stage.show();
	   	   	  
	   	  	   Ctrl_AjouterVehicule controller = loader.getController();
	   	  	   controller.SessionConnectionOracle(estconnecté);
	   	  	   controller.InitCombobox_Modele();
	   	  	   
	   	   	   return NouveauStage;
	}
	
	public Stage louervehicule() throws IOException
	{
	   	   FXMLLoader loader = new FXMLLoader(
		   	   	     getClass().getResource(
		   	   	       "/Vue/LouerVehicule.fxml"
		   	   	     )
		   	   	   );
		   	   	   
	   	   		  Stage NouveauStage = new Stage();	
		   	   	  //Stage stage;
		   	   	  //stage = (Stage) titre_projet_rmi.getScene().getWindow();
		   	   	  
		   	   	  Scene scene = new Scene(loader.load());
		   	 	  NouveauStage.setScene(scene);
		   	 	  NouveauStage.setResizable(false);
		   	   	  NouveauStage.show();
		   	   	  //stage.setScene(scene);
		   	   	  //stage.show();
		   	   	  
		   	  	   Ctrl_LouerVehicule controller = loader.getController();
		   	  	   controller.SessionConnectionOracle(estconnecté);
		   	  	   controller.InitCombobox_Formules_Location();
		   	  	   
		   	   	   return NouveauStage;
	}

    
	public void setPrimaryStage(Stage s) {
		this.primaryStage = s;
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
	 primaryStage.setIconified(true);
	}

	
	
}

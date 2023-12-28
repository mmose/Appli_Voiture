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
import java.sql.SQLWarning;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;



public class Ctrl_LouerVehicule {


	@FXML
	private VBox VBox_client;
	@FXML
	private Label titre_projet_rmi;
	@FXML
	private TextField input_numero;
	@FXML
	private DatePicker input_date_depart;
	@FXML
	private ComboBox<String> cmbx_formule_location;

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
	
	public void InitCombobox_Formules_Location()
	{
		Requete r1 = new Requete();
		ResultSet resultat;
		try {
			r1.requete_select(estconnecté, "SELECT formule FROM FORMULESLOCATION ORDER BY NBJOURS ASC");
			resultat = r1.getResulSet();
			
			 while (resultat.next())
			 {
				 cmbx_formule_location.getItems().add(resultat.getString(1));
			 }
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void louervehicule() throws SQLException
	{
		message_erreur.clear();
		
		if (!(cmbx_formule_location.getSelectionModel().getSelectedItem()==null)  && !input_numero.getText().isEmpty() && !(input_date_depart.getValue()==null))
		{
		
				try (
					     Statement s = estconnecté.createStatement()) {
					 
					    try {
					        s.executeUpdate("begin dbms_output.enable(); end;");
					        String formattedDate = input_date_depart.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
					        s.executeUpdate("begin LouerVehicule('"+input_numero.getText()+"','"+cmbx_formule_location.getSelectionModel().getSelectedItem().toString()+"','"+formattedDate+"'); end;");
					       
					        
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
		else if (input_numero.getText().isEmpty())
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Louer véhicule");
			alert.setHeaderText("Numéro du véhicule non saisie");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}
		
		else if (input_date_depart.getValue()==null)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Louer véhicule");
			alert.setHeaderText("Date non sélectionnée");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}

		else if ((cmbx_formule_location.getSelectionModel().getSelectedItem()==null))
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Louer véhicule");
			alert.setHeaderText("Formule non sélectionnée");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}
	}
	
/**
	public void louervehiculeOLD() throws SQLException
	{	
		message_erreur.clear();
		
		if (!(cmbx_formule_location.getSelectionModel().getSelectedItem()==null)  && !input_numero.getText().isEmpty() && !(input_date_depart.getValue()==null))
		{
		
			String query = "{call LouerVehicule(?,?,?)}"; 
			CallableStatement statement = null;
			
			try {
				statement = estconnecté.prepareCall(query);
				statement.setString(1, input_numero.getText());  
				statement.setString(2, cmbx_formule_location.getSelectionModel().getSelectedItem().toString());  
				String formattedDate = input_date_depart.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
				statement.setString(3, formattedDate);  
				statement.execute(); 
				System.out.println("Véhicule loué");
				message_erreur.setText("Véhicule loué");
			
			} catch (SQLException e) {
				System.out.println("Impossible de louer le véhicule");
				message_erreur.setWrapText(true);
				message_erreur.setText("Impossible de louer le véhicule \n : " +e.getMessage().toString());
				e.printStackTrace();
			}  
		
		}
		else if (input_date_depart.getValue()==null)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Louer véhicule");
			alert.setHeaderText("Date non sélectionné");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}
		else if (input_numero.getText().isEmpty())
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Louer véhicule");
			alert.setHeaderText("Numéro du véhicule non saisie");
			alert.setContentText(estconnecté.toString());
			alert.showAndWait();
		}
		
		else if ((cmbx_formule_location.getSelectionModel().getSelectedItem()==null))
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur - Louer véhicule");
			alert.setHeaderText("Formule non sélectionné");
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

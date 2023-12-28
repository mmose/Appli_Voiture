package Vue;

import java.net.URL;

import Controleur.Ctrl_Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Client extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		try {
			//CHARGEMENT DU FXML 
			URL fxmlURL=getClass().getResource("client.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
			Node root = fxmlLoader.load();
			Scene scene = new Scene((Pane) root);
			
			//CHARGEMENT DU CONTROLEUR
			Ctrl_Client ctrl = fxmlLoader.getController();
			ctrl.setPrimaryStage(primaryStage);
			ctrl.NouvelleConnection();
			ctrl.init_cmbx_categorie();
			
			//CHARGEMENT DE l'ICONE DE l'APPLICATION
			//Image icone = new Image("/icones/avance_client.PNG");
			
			//AFFECTATIONS DES PARAMETRES DE LA FENETRE
			primaryStage.setTitle("Application location de voiture");
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			//primaryStage.initStyle(StageStyle.UNDECORATED); // SA VEUT DIRE QU'ON ENLEVE LA FENETRE WINDOWS PAR DEFAUT  (fermer,agrandir,reduire)
			//primaryStage.getIcons().add(icone);
			primaryStage.show();
	
		} catch(Exception e) {
			e.printStackTrace();
		}	
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}



}

package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alertas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class TelaPrincipalController implements Initializable{
	//@FXML para SceneBuilder reconhecer os menus
	@FXML
	private MenuItem menuItemCliente;
	
	@FXML
	private MenuItem menuItemAutomovel;
	
	@FXML
	private MenuItem menuItemAbout;
	
	//Declaração dos metodos de ações dos atributos de menus
	@FXML
	public void onMenuItemClienteAction() {
		System.out.println("onMenuItemClienteAction");
	}
	
	@FXML
	public void onMenuItemAutomovelAction() {
		loadView("/gui/AutomovelList.fxml");
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml");
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		
		
	}
	
	/*Função que abre/carrega outra tela
	 * synchronized a sincronização de todas as telas durante o multitreding
	 */
	private synchronized void loadView(String absoluteName) {
		
		//Carrega a View dentro da tela principal
		try {
			//Manipulação de acesso as janelas a partir da janela principal(ScrollPane)
			FXMLLoader loader= new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
		}
		catch (IOException exception) {
			Alertas.showAlert("IO Exception", "Erro ao carregar a página", exception.getMessage(), AlertType.ERROR);
		}
	}
}
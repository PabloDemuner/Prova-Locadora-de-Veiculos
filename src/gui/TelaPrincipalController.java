package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
import modelo.services.AutomovelService;

public class TelaPrincipalController implements Initializable {
	// @FXML para SceneBuilder reconhecer os menus
	@FXML
	private MenuItem menuItemCliente;

	@FXML
	private MenuItem menuItemAutomovel;

	@FXML
	private MenuItem menuItemAbout;

	// Declara��o dos metodos de a��es dos atributos de menus
	@FXML
	public void onMenuItemClienteAction() {
		System.out.println("onMenuItemClienteAction");
	}

	@FXML
	public void onMenuItemAutomovelAction() {
		// express�o lambda para inicializar o AutomovelListController
		loadView("/gui/AutomovelList.fxml", (AutomovelListController automovelListController) -> {
			automovelListController.setAutomovelService(new AutomovelService());
			automovelListController.updateTableView();
		});
	}

	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {
		});
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

	/*
	 * Fun��o que abre/carrega outra tela synchronized a sincroniza��o de todas as
	 * telas durante o multitreding
	 */
	private synchronized <T> void loadView(String absoluteName, Consumer<T> acaoInicializacaoTela) {

		// Carrega a View dentro da tela principal
		try {
			// Manipula��o de acesso as janelas a partir da janela principal(ScrollPane)
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();

			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());

			T automovelListController = loader.getController();
			acaoInicializacaoTela.accept(automovelListController);
		} catch (IOException exception) {
			Alertas.showAlert("IO Exception", "Erro ao carregar a p�gina", exception.getMessage(), AlertType.ERROR);
		}
	}
}
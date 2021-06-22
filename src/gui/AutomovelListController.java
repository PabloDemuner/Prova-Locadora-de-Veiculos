package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import modelo.entidades.Automovel;

public class AutomovelListController implements Initializable{
	//@FXML para SceneBuilder reconhecer os menus
	//Declaração da tabela Automovel
	@FXML
	private TableView<Automovel> tableViewAutomovel;

	//Declaração das colunas da tabela
	@FXML
	private TableColumn<Automovel, Integer> tableColumId;
	
	@FXML
	private TableColumn<Automovel, String> tableColumNome;
	
	@FXML
	private TableColumn<Automovel, String> tableColumMarca;
	
	@FXML
	private Button buttonNew;
	
	//Tratamento de eventos do botão
	@FXML
	public void onBtAction() {
		System.out.println("onBtAction");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		
		InitializeNodes();
	}

	private void InitializeNodes() {
		//padrão do JavaFX para iniciar o comportamento das colunas 
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
		
		//Metodo para a tabela Automovel ocupar toda a janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewAutomovel.prefHeightProperty().bind(stage.heightProperty());
	}
}
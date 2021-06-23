package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import modelo.entidades.Automovel;
import modelo.services.AutomovelService;

public class AutomovelListController implements Initializable {

	private AutomovelService automovelService;

	// @FXML para SceneBuilder reconhecer os menus
	// Declaração da tabela Automovel
	@FXML
	private TableView<Automovel> tableViewAutomovel;

	// Declaração das colunas da tabela
	@FXML
	private TableColumn<Automovel, Integer> tableColumId;

	@FXML
	private TableColumn<Automovel, String> tableColumNome;

	@FXML
	private TableColumn<Automovel, String> tableColumMarca;

	@FXML
	private Button buttonNew;

	private ObservableList<Automovel> obsList;

	// Tratamento de eventos do botão
	@FXML
	public void onBtAction() {
		System.out.println("onBtAction");
	}

	// Injeção de dependencia do AutomovelService
	public void setAutomovelService(AutomovelService automovelService) {
		this.automovelService = automovelService;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		InitializeNodes();
	}

	private void InitializeNodes() {
		// padrão do JavaFX para iniciar o comportamento das colunas
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));

		// Metodo para a tabela Automovel ocupar toda a janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewAutomovel.prefHeightProperty().bind(stage.heightProperty());
	}

	// Metodo responsável por acessar o AutomovelService e carregar no
	// ObservableList
	public void updateTableView() {
		if (automovelService == null) {
			throw new IllegalStateException("AutomovelService esta nulo ");
		}
		List<Automovel> list = automovelService.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewAutomovel.setItems(obsList);
	}
}
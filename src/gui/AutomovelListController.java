package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alertas;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.entidades.Automovel;
import modelo.services.AutomovelService;

public class AutomovelListController implements Initializable, DataChangeListener {

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
	
	//Coluna de Edição de Automovel
	@FXML
	private TableColumn<Automovel, Automovel> tableColumEdit;

	//Coluna de Deleção de um Autmovel
	@FXML
	private TableColumn<Automovel, Automovel> tableColumDelete;
	
	@FXML
	private Button buttonNew;

	private ObservableList<Automovel> obsList;

	// Tratamento de eventos do botão
	@FXML
	public void onBtAction(ActionEvent event) {
		Stage parentStage = Utils.paucoAtual(event);
		Automovel obj = new Automovel();
		createDialogForm(obj, "/gui/AutomovelForm.fxml", parentStage);
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
			throw new IllegalStateException("AutomovelService esta vazio ");
		}
		List<Automovel> list = automovelService.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewAutomovel.setItems(obsList);
		//Acrecenta o botão edit na janela
		editaAutomovel();
		deletaAutomovel();
	}

	// Metodo implementado para instanciar janela de dialogos
	private void createDialogForm(Automovel obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			AutomovelFormController automovelFormController = loader.getController();
			automovelFormController.setAutomovel(obj);
			//Injeção do AutomovelService
			automovelFormController.setAutomovelService(new AutomovelService());
			//Observer
			automovelFormController.atualizaDadosDaTela(this);
			automovelFormController.atualizarAutomovel();

			Stage dialogStage = new Stage();

			dialogStage.setTitle("Digite os dados do Automóvel");
			dialogStage.setScene(new Scene(pane));
			// Janela não pode ser redimencionada
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			// Para a janela ficar travada
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alertas.showAlert("IO Exception", "Erro ao carregar a janela", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void disparaAtualizacaoEventos() {
		updateTableView();
	}
	/*https://stackoverflow.com/questions/32282230/fxml-javafx-8-tableview-make-a-delete-button-in-each-row-and-delete-the-row-a
	 * Codigo que instancia e configura o botão de editar os automoveis
	 */
	private void editaAutomovel() {
		tableColumEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumEdit.setCellFactory(param -> new TableCell<Automovel, Automovel>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Automovel obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/AutomovelForm.fxml", Utils.paucoAtual(event)));
			}
		});
	}
	//Codigo que instancia e configura o botão de deletar os automoveis
	private void deletaAutomovel() {
		tableColumDelete.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumDelete.setCellFactory(param -> new TableCell<Automovel, Automovel>() {
			private final Button button = new Button("deletar");

			@Override
			protected void updateItem(Automovel obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> deleteEntity(obj));
			}
		});
	}
		//Metodo para remover uma entidade
	private void deleteEntity(Automovel obj) {
		Optional<ButtonType> result = Alertas.showConfirmation("Confirmação", "Deseja realmente deletar?");
		
		if(result.get() == ButtonType.OK) {
			if (automovelService == null) {
				throw new IllegalStateException("AutomovelService esta vazio ");
			}
			try {
				automovelService.delete(obj);
				updateTableView();
			}
			catch (DbIntegrityException exception) {
				Alertas.showAlert("Erro ao remover o Automovel", null, exception.getMessage(), AlertType.ERROR);
			}
		}
	}
}
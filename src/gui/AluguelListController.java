package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import modelo.entidades.Aluguel;
import modelo.services.AluguelService;
import modelo.services.AutomovelService;
import modelo.services.ClienteService;

public class AluguelListController implements Initializable, DataChangeListener {

	private AluguelService aluguelService;
	
	private ClienteService clienteService;
	
	private AutomovelService automovelService;

	// @FXML para SceneBuilder reconhecer os menus
	// Declaração da tabela Aluguel
	@FXML
	private TableView<Aluguel> tableViewAluguel;

	// Declaração das colunas da tabela
	@FXML
	private TableColumn<Aluguel, Integer> tableColumId;
	
	@FXML
	private TableColumn<Aluguel, Date> tableColumDataInicio;
	
	@FXML
	private TableColumn<Aluguel, Date> tableColumDataFim;
	
	@FXML
	private TableColumn<Aluguel, String> tableColumNomeCliente;
	
	@FXML
	private TableColumn<Aluguel, String> tableColumNomeAutomovel;
	
	//Coluna de Edição de Aluguel
	@FXML
	private TableColumn<Aluguel, Aluguel> tableColumEdit;

	//Coluna de Deleção de um Autmovel
	@FXML
	private TableColumn<Aluguel, Aluguel> tableColumDelete;
	
	@FXML
	private Button buttonNew;

	private ObservableList<Aluguel> obsList;

	// Tratamento de eventos do botão
	@FXML
	public void onBtAction(ActionEvent event) {
		Stage parentStage = Utils.paucoAtual(event);
		Aluguel obj = new Aluguel();
		createDialogForm(obj, "/gui/AluguelForm.fxml", parentStage);
	}

	// Injeção de dependencia do AluguelService
	public void setAluguelService(AluguelService aluguelService) {
		this.aluguelService = aluguelService;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		InitializeNodes();
	}

	private void InitializeNodes() {
		// padrão do JavaFX para iniciar o comportamento das colunas
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumDataInicio.setCellValueFactory(new PropertyValueFactory<>("dataInicio"));
		Utils.formatTableColumnDate(tableColumDataInicio, "dd/MM/yyyy");
		tableColumDataFim.setCellValueFactory(new PropertyValueFactory<>("dataFim"));
		Utils.formatTableColumnDate(tableColumDataFim, "dd/MM/yyyy");
		tableColumNomeCliente.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumNomeAutomovel.setCellValueFactory(new PropertyValueFactory<>("id"));
		

		// Metodo para a tabela Aluguel ocupar toda a janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewAluguel.prefHeightProperty().bind(stage.heightProperty());
	}

	// Metodo responsável por acessar o AluguelService e carregar no
	// ObservableList
	public void updateTableView() {
		if (aluguelService == null) {
			throw new IllegalStateException("AluguelService esta vazio ");
		}
		List<Aluguel> list = aluguelService.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewAluguel.setItems(obsList);
		//Acrecenta o botão editar na janela
		editaAluguel();
		//Acrecenta o botão deletar na janela
		deletaAluguel();
	}

	// Metodo implementado para instanciar janela de dialogos
	private void createDialogForm(Aluguel obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			AluguelFormController aluguelFormController = loader.getController();
			aluguelFormController.setAluguel(obj);
			//Injeção dos Services
			aluguelFormController.setServices(new AluguelService(), new ClienteService(), new AutomovelService());
			aluguelFormController.carregaComboBoxCliente();
			aluguelFormController.carregaComboBoxAutomovel();
			//Observer
			aluguelFormController.atualizaDadosDaTela(this);
			aluguelFormController.atualizarAluguel();

			Stage dialogStage = new Stage();

			dialogStage.setTitle("Digite os dados do Aluguel");
			dialogStage.setScene(new Scene(pane));
			// Janela não pode ser redimencionada
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			// Para a janela ficar travada
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
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
	private void editaAluguel() {
		tableColumEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumEdit.setCellFactory(param -> new TableCell<Aluguel, Aluguel>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Aluguel obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/AluguelForm.fxml", Utils.paucoAtual(event)));
			}
		});
	}
	//Codigo que instancia e configura o botão de deletar os automoveis
	private void deletaAluguel() {
		tableColumDelete.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumDelete.setCellFactory(param -> new TableCell<Aluguel, Aluguel>() {
			private final Button button = new Button("deletar");

			@Override
			protected void updateItem(Aluguel obj, boolean empty) {
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
	private void deleteEntity(Aluguel obj) {
		Optional<ButtonType> result = Alertas.showConfirmation("Confirmação", "Deseja realmente deletar?");
		
		if(result.get() == ButtonType.OK) {
			if (aluguelService == null) {
				throw new IllegalStateException("AluguelService esta vazio ");
			}
			try {
				aluguelService.delete(obj);
				updateTableView();
			}
			catch (DbIntegrityException exception) {
				Alertas.showAlert("Erro ao remover o Aluguel", null, exception.getMessage(), AlertType.ERROR);
			}
		}
	}
}
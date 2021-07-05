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
import modelo.entidades.Cliente;
import modelo.services.ClienteService;

public class ClienteListController implements Initializable, DataChangeListener {

	private ClienteService clienteService;

	// @FXML para SceneBuilder reconhecer os menus
	// Declara��o da tabela Cliente
	@FXML
	private TableView<Cliente> tableViewCliente;

	// Declara��o das colunas da tabela
	@FXML
	private TableColumn<Cliente, Integer> tableColumId;

	@FXML
	private TableColumn<Cliente, String> tableColumNome;

	@FXML
	private TableColumn<Cliente, String> tableColumEmail;
	
	@FXML
	private TableColumn<Cliente, Date> tableColumDataNasc;
	
	//Coluna de Edi��o de Cliente
	@FXML
	private TableColumn<Cliente, Cliente> tableColumEdit;

	//Coluna de Dele��o de um Autmovel
	@FXML
	private TableColumn<Cliente, Cliente> tableColumDelete;
	
	@FXML
	private Button buttonNew;

	private ObservableList<Cliente> obsList;

	// Tratamento de eventos do bot�o
	@FXML
	public void onBtAction(ActionEvent event) {
		Stage parentStage = Utils.paucoAtual(event);
		Cliente obj = new Cliente();
		createDialogForm(obj, "/gui/ClienteForm.fxml", parentStage);
	}

	// Inje��o de dependencia do ClienteService
	public void setClienteService(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		InitializeNodes();
	}

	private void InitializeNodes() {
		// padr�o do JavaFX para iniciar o comportamento das colunas
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumDataNasc.setCellValueFactory(new PropertyValueFactory<>("dataNasc"));
		Utils.formatTableColumnDate(tableColumDataNasc, "dd/MM/yyyy");
		

		// Metodo para a tabela Cliente ocupar toda a janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewCliente.prefHeightProperty().bind(stage.heightProperty());
	}

	// Metodo respons�vel por acessar o ClienteService e carregar no
	// ObservableList
	public void updateTableView() {
		if (clienteService == null) {
			throw new IllegalStateException("ClienteService esta vazio ");
		}
		List<Cliente> list = clienteService.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewCliente.setItems(obsList);
		//Acrecenta o bot�o editar na janela
		editaCliente();
		//Acrecenta o bot�o deletar na janela
		deletaCliente();
	}

	// Metodo implementado para instanciar janela de dialogos
	private void createDialogForm(Cliente obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			ClienteFormController clienteFormController = loader.getController();
			clienteFormController.setCliente(obj);
			//Inje��o do ClienteService
			clienteFormController.setClienteService(new ClienteService());
			//Observer
			clienteFormController.atualizaDadosDaTela(this);
			clienteFormController.atualizarCliente();

			Stage dialogStage = new Stage();

			dialogStage.setTitle("Digite os dados do Cliente");
			dialogStage.setScene(new Scene(pane));
			// Janela n�o pode ser redimencionada
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
	 * Codigo que instancia e configura o bot�o de editar os automoveis
	 */
	private void editaCliente() {
		tableColumEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumEdit.setCellFactory(param -> new TableCell<Cliente, Cliente>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Cliente obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/ClienteForm.fxml", Utils.paucoAtual(event)));
			}
		});
	}
	//Codigo que instancia e configura o bot�o de deletar os automoveis
	private void deletaCliente() {
		tableColumDelete.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumDelete.setCellFactory(param -> new TableCell<Cliente, Cliente>() {
			private final Button button = new Button("deletar");

			@Override
			protected void updateItem(Cliente obj, boolean empty) {
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
	private void deleteEntity(Cliente obj) {
		Optional<ButtonType> result = Alertas.showConfirmation("Confirma��o", "Deseja realmente deletar?");
		
		if(result.get() == ButtonType.OK) {
			if (clienteService == null) {
				throw new IllegalStateException("ClienteService esta vazio ");
			}
			try {
				clienteService.delete(obj);
				updateTableView();
			}
			catch (DbIntegrityException exception) {
				Alertas.showAlert("Erro ao remover o Cliente", null, exception.getMessage(), AlertType.ERROR);
			}
		}
	}
}
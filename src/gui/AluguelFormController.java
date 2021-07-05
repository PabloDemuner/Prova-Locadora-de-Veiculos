package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alertas;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import modelo.entidades.Aluguel;
import modelo.entidades.Automovel;
import modelo.entidades.Cliente;
import modelo.services.AluguelService;
import modelo.services.AutomovelService;
import modelo.services.ClienteService;

public class AluguelFormController implements Initializable {

	private Aluguel aluguel;

	private AluguelService aluguelService;

	private ClienteService clienteService;

	private AutomovelService automovelService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	// Efetua a busca dos clientes e automoveis para a combobox de Aluguel
	private ObservableList<Cliente> obsListCliente;
	private ObservableList<Automovel> obsListAutomovel;

	@FXML
	private ComboBox<Cliente> comboBoxCliente;

	@FXML
	private ComboBox<Automovel> comboBoxAutomovel;

	@FXML
	private TextField txtId;

	@FXML
	private DatePicker dateDataInicio;

	@FXML
	private DatePicker dateDataFim;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	@FXML
	public void onBtSave(ActionEvent event) {
		if (aluguel == null) {
			throw new IllegalStateException("aluguel esta vazio ");
		}
		if (aluguelService == null) {
			throw new IllegalStateException("AluguelService esta vazio ");
		}
		try {
			aluguel = getSalvaAluguel();
			aluguelService.saveOrUpdate(aluguel);
			// Atualiza os dados da tela principal ao clicar em salvar
			notificaDadosDaTela();
			// Para fechar a janela
			Utils.paucoAtual(event).close();
		} catch (DbException exception) {
			Alertas.showAlert("Erro ao salvar os dados do aluguel", null, exception.getMessage(), AlertType.ERROR);
		}
	}

	@FXML
	public void OnBtCancel(ActionEvent event) {
		// Para fechar a janela
		Utils.paucoAtual(event).close();
	}

	public void setAluguel(Aluguel aluguel) {
		this.aluguel = aluguel;
	}

	public void setServices(AluguelService aluguelService, ClienteService clienteService,
			AutomovelService automovelService) {
		this.aluguelService = aluguelService;
		this.clienteService = clienteService;
		this.automovelService = automovelService;
	}

	public void atualizaDadosDaTela(DataChangeListener dataChangeListener) {
		dataChangeListeners.add(dataChangeListener);
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Utils.formatDate(dateDataInicio, "dd/MM/YYYY");
		Utils.formatDate(dateDataFim, "dd/MM/YYYY");
		
		inicializaComboBoxCliente();
		inicializaComboBoxAutomovel();
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		initializeNodes();
	}

	public void atualizarAluguel() {
		if (aluguel == null) {
			throw new IllegalStateException("O campo está vazio");
		}

		txtId.setText(String.valueOf(aluguel.getId()));

		if (aluguel.getDataInicio() != null) {
			dateDataInicio.setValue(LocalDate.ofInstant(aluguel.getDataInicio().toInstant(), ZoneId.systemDefault()));
		}

		if (aluguel.getDataFim() != null) {
			dateDataFim.setValue(LocalDate.ofInstant(aluguel.getDataFim().toInstant(), ZoneId.systemDefault()));
		}
		
		if(aluguel.getCliente() == null) {
			comboBoxCliente.getSelectionModel().selectFirst();
		}
		else {
			comboBoxCliente.setValue(aluguel.getCliente());
		}
		
		if(aluguel.getAutomovel() == null) {
			comboBoxAutomovel.getSelectionModel().selectFirst();
		}
		else {
			comboBoxAutomovel.setValue(aluguel.getAutomovel());
		}
	}

	private Aluguel getSalvaAluguel() {
		Aluguel obj = new Aluguel();

		// tryParseToInt faz a conversão de String para Int
		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (dateDataInicio.getValue() == null) {
			Alertas.showAlert("Campo Data de Inicio do aluguel não pode ser vazio", null, null, AlertType.ERROR);
		} else {
			Instant instant = Instant.from(dateDataInicio.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setDataInicio(Date.from(instant));
		}

		if (dateDataFim.getValue() == null) {
			Alertas.showAlert("Campo Data de fim do aluguel não pode ser vazio", null, null, AlertType.ERROR);
		} else {
			Instant instant = Instant.from(dateDataFim.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setDataFim(Date.from(instant));
		}
		
		//Salva os valores das ComboBoxs
		obj.setCliente(comboBoxCliente.getValue());
		obj.setAutomovel(comboBoxAutomovel.getValue());

		return obj;
	}

	// Subjects
	private void notificaDadosDaTela() {
		for (DataChangeListener dataChangeListener : dataChangeListeners) {
			dataChangeListener.disparaAtualizacaoEventos();
		}
	}

	// Carrega os objetos associados aos combobox de cliente.
	public void carregaComboBoxCliente() {
		if (clienteService == null) {
			throw new IllegalStateException("A Combobox cliente esta vazia");
		}
		List<Cliente> list = clienteService.findAll();
		obsListCliente = FXCollections.observableArrayList(list);
		comboBoxCliente.setItems(obsListCliente);
	}

	// Carrega os objetos associados aos combobox de Automovel.
	public void carregaComboBoxAutomovel() {
		if (automovelService == null) {
			throw new IllegalStateException("A Combobox automovel esta vazia");
		}
		List<Automovel> list = automovelService.findAll();
		obsListAutomovel = FXCollections.observableArrayList(list);
		comboBoxAutomovel.setItems(obsListAutomovel);
	}

	private void inicializaComboBoxCliente() {
		Callback<ListView<Cliente>, ListCell<Cliente>> factory = lv -> new ListCell<Cliente>() {
			@Override
			protected void updateItem(Cliente item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxCliente.setCellFactory(factory);
		comboBoxCliente.setButtonCell(factory.call(null));
	}
	
	private void inicializaComboBoxAutomovel() {
		Callback<ListView<Automovel>, ListCell<Automovel>> factory = lv -> new ListCell<Automovel>() {
			@Override
			protected void updateItem(Automovel item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxAutomovel.setCellFactory(factory);
		comboBoxAutomovel.setButtonCell(factory.call(null));
	}
}
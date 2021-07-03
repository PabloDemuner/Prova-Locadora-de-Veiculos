package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alertas;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.entidades.Cliente;
import modelo.services.ClienteService;

public class ClienteFormController implements Initializable {

	private Cliente cliente;

	private ClienteService clienteService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dateDataNasc;

	@FXML
	private Label labelErroNome;

	@FXML
	private Label labelErroEmail;

	@FXML
	private Label labelErrodateDataNasc;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	@FXML
	public void onBtSave(ActionEvent event) {
		if (cliente == null) {
			throw new IllegalStateException("cliente esta vazio ");
		}
		if (clienteService == null) {
			throw new IllegalStateException("ClienteService esta vazio ");
		}
		try {
			cliente = getSalvaCliente();
			clienteService.saveOrUpdate(cliente);
			// Atualiza os dados da tela prnicipal ao clicar em salvar
			notificaDadosDaTela();
			// Para fechar a janela
			Utils.paucoAtual(event).close();
		} catch (DbException exception) {
			Alertas.showAlert("Erro ao salvar os dados do cliente", null, exception.getMessage(), AlertType.ERROR);
		}
	}

	@FXML
	public void OnBtCancel(ActionEvent event) {
		// Para fechar a janela
		Utils.paucoAtual(event).close();
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public void setClienteService(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	public void atualizaDadosDaTela(DataChangeListener dataChangeListener) {
		dataChangeListeners.add(dataChangeListener);
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 100);
		Constraints.setTextFieldMaxLength(txtEmail, 100);
		Utils.formatDate(dateDataNasc, "dd/MM/YYYY");
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		initializeNodes();
	}

	public void atualizarCliente() {
		if (cliente == null) {
			throw new IllegalStateException("O campo está vazio");
		}

		txtId.setText(String.valueOf(cliente.getId()));
		txtNome.setText(cliente.getNome());
		txtEmail.setText(cliente.getEmail());
		if (cliente.getDataNasc() != null) {
			dateDataNasc.setValue(LocalDate.ofInstant(cliente.getDataNasc().toInstant(), ZoneId.systemDefault()));
		}
	}

	private Cliente getSalvaCliente() {
		Cliente obj = new Cliente();
		// tryParseToInt faz a conversão de String para Int
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setNome(txtNome.getText());
		// obj.setMarca(txtMarca.getText());

		return obj;
	}

	// Subjects
	private void notificaDadosDaTela() {
		for (DataChangeListener dataChangeListener : dataChangeListeners) {
			dataChangeListener.disparaAtualizacaoEventos();
		}
	}
}
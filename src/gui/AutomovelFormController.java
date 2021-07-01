package gui;

import java.net.URL;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.entidades.Automovel;
import modelo.services.AutomovelService;

public class AutomovelFormController implements Initializable {

	private Automovel automovel;
	
	private AutomovelService automovelService;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtMarca;

	@FXML
	private Label labelErroNome;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	@FXML
	public void onBtSave(ActionEvent event) {
		if (automovel == null) {
			throw new IllegalStateException("automovel esta vazio ");
		}
		if (automovelService == null) {
			throw new IllegalStateException("AutomovelService esta vazio ");
		}
		try {
			automovel = getSalvaAutomovel();
			automovelService.saveOrUpdate(automovel);
			//Atualiza os dados da tela prnicipal ao clicar em salvar
			notificaDadosDaTela();
			//Para fechar a janela
			Utils.paucoAtual(event).close();
		} 
		catch (DbException exception) {
			Alertas.showAlert("Erro ao salvar os dados do automovel", null, exception.getMessage(), AlertType.ERROR);
		}
	}

	@FXML
	public void OnBtCancel(ActionEvent event) {
		//Para fechar a janela
		Utils.paucoAtual(event).close();
	}

	public void setAutomovel(Automovel automovel) {
		this.automovel = automovel;
	}
	
	public void setAutomovelService(AutomovelService automovelService) {
		this.automovelService = automovelService;
	}
	
	public void atualizaDadosDaTela(DataChangeListener dataChangeListener) {
		dataChangeListeners.add(dataChangeListener);
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 60);
		Constraints.setTextFieldMaxLength(txtMarca, 60);
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		initializeNodes();
	}

	public void atualizarAutomovel() {
		if (automovel == null) {
			throw new IllegalStateException("O campo está vazio");
		}

		txtId.setText(String.valueOf(automovel.getId()));
		txtNome.setText(automovel.getNome());
		txtMarca.setText(automovel.getMarca());
	}
	
	private Automovel getSalvaAutomovel() {
		Automovel obj = new Automovel();
		//tryParseToInt faz a conversão de String para Int
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setNome(txtNome.getText());
		obj.setMarca(txtMarca.getText());
		
		return obj;
	}
	
	private void notificaDadosDaTela() {
		for (DataChangeListener dataChangeListener : dataChangeListeners) {
			dataChangeListener.disparaAtualizacaoEventos();
		}
	}
}
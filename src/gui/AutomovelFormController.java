package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.entidades.Automovel;

public class AutomovelFormController implements Initializable{

	private Automovel automovel;
	
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
	public void onBtSave() {
		System.out.println("onBtSave");
	}
	
	@FXML
	public void OnBtCancel() {
		System.out.println("OnBtCancel");
	}
	
	public void setAutomovel(Automovel automovel) {
		this.automovel = automovel;
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
}
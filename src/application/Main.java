package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TelaPrincipal.fxml"));
			ScrollPane scrollPane = loader.load();
			
			//Comandos para deixar a barra de menu ocupando 100% da tela
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			Scene mainScene = new Scene(scrollPane);
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("POO Locadora de Veiculos");
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

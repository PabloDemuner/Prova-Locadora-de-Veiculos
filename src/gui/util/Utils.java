package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
/*Classe para ver o estado atual de um botão qualquer 
 * e a partir desse estado abrir uma janela
 */
public class Utils {

	public static Stage paucoAtual(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow(); 
	}
	
	public static Integer tryParseToInt(String string) {
		try {
			return Integer.parseInt(string);
		}
		catch (NumberFormatException exception) {
			return null;
		}
	}
}

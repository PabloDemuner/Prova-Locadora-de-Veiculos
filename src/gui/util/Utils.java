package gui.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.util.StringConverter;
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
	
	/*https://stackoverflow.com/questions/47484280/format-of-date-in-the-javafx-tableview
	 * Efetua a formatação da data de cadastro
	 */
	public static <T> void formatTableColumnDate(TableColumn<T, Date> tableColumn, String format) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Date> cell = new TableCell<T, Date>() {
				private SimpleDateFormat sdf = new SimpleDateFormat(format);

				@Override
				protected void updateItem(Date item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						setText(sdf.format(item));
					}
				}
			};
			return cell;
		});
	}

	/*https://stackoverflow.com/questions/47484280/format-of-date-in-the-javafx-tableview
	 * Efetua a formatação de numeros com ponto flutuante
	 */
	public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, int decimalPlaces) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Double> cell = new TableCell<T, Double>() {
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						Locale.setDefault(Locale.US);
						setText(String.format("%." + decimalPlaces + "f", item));
					}
				}
			};
			return cell;
		});
	}
	
	/*https://stackoverflow.com/questions/26831978/javafx-datepicker-getvalue-in-a-specific-format
	 * Metodo que faz a formatção da data para a data atual
	 */
	public static void formatDate(DatePicker datePicker, String format) {
		datePicker.setConverter(new StringConverter<LocalDate>() {

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);
			{
				datePicker.setPromptText(format.toLowerCase());
			}

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		});
	}
}

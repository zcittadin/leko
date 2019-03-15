package zct.sistemas.leko.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class AlertUtil {

	public static void makeInfo(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.showAndWait();
	}

	public static void makeNone(String title, String message) {
		Alert alert = new Alert(AlertType.NONE);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.showAndWait();
	}

	public static void makeWarning(String title, String message) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.showAndWait();
	}

	public static void makeError(String title, String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.showAndWait();
	}

	public static Optional<ButtonType> makeConfirm(String title, String message) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(message);
		Optional<ButtonType> result = alert.showAndWait();
		return result;
	}
}

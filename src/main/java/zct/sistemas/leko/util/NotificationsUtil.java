package zct.sistemas.leko.util;

import org.controlsfx.control.Notifications;

import javafx.geometry.Pos;
import javafx.util.Duration;

public class NotificationsUtil {

	public static void makeInfo(String title, String message) {
		Notifications.create().title(title).text(message).position(Pos.BOTTOM_RIGHT).darkStyle()
				.hideAfter(new Duration(5000)).showInformation();
	}

	public static void makeWarning(String title, String message) {
		Notifications.create().title(title).text(message).position(Pos.BOTTOM_RIGHT).darkStyle()
				.hideAfter(new Duration(5000)).showWarning();
	}

	public static void makeError(String title, String message) {
		Notifications.create().title(title).text(message).position(Pos.BOTTOM_RIGHT).darkStyle()
				.hideAfter(new Duration(5000)).showError();
	}

}

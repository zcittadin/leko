package zct.sistemas.leko.control;

import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;
import zct.sistemas.leko.util.StringUtils;

/**
 * 
 * Uses a combobox tooltip as the suggestion for auto complete and updates the
 * combo box itens accordingly <br />
 * It does not work with space, space and escape cause the combobox to hide and
 * clean the filter ... Send me a PR if you want it to work with all characters
 * -> It should be a custom controller - I KNOW!
 * 
 * @author wsiqueir
 *
 * @param <T>
 */
public class ComboBoxAutoComplete<T> {

	private ComboBox<T> cmb;
	String filter = "";
	private ObservableList<T> originalItems;

	public ComboBoxAutoComplete(ComboBox<T> cmb) {
		this.cmb = cmb;
		originalItems = FXCollections.observableArrayList(cmb.getItems());
		Tooltip tp = new Tooltip();
		tp.setStyle(
				"-fx-background-color: rgba(0,0,0,0.75),\r\n" + "                           rgba(255,255,255,0.75),\r\n"
						+ "                           linear-gradient(to bottom,#aaaaaa 0%,#eeeeee 100%);\r\n"
						+ "     -fx-background-insets: 0 1 1 0, 1 0 0 1, 1;\r\n" + "     -fx-padding: 7px;\r\n"
						+ "     -fx-background-radius: 3px;\r\n" + "     -fx-border-radius: 3px;\r\n"
						+ "     -fx-font: 12px \"Arial\";\r\n" + "     -fx-text-fill: \"black\";");
		cmb.setTooltip(tp);
		cmb.setOnKeyPressed(this::handleOnKeyPressed);
		cmb.setOnHidden(this::handleOnHiding);
	}

	public void handleOnKeyPressed(KeyEvent e) {
		ObservableList<T> filteredList = FXCollections.observableArrayList();
		KeyCode code = e.getCode();

		if (code.isLetterKey()) {
			filter += StringUtils.unaccent(e.getText());
		}
		if (code == KeyCode.BACK_SPACE && filter.length() > 0) {
			filter = filter.substring(0, filter.length() - 1);
			cmb.getItems().setAll(originalItems);
		}
		if (code == KeyCode.ESCAPE) {
			filter = "";
		}
		if (filter.length() == 0) {
			filteredList = originalItems;
			cmb.getTooltip().hide();
		} else {
			Stream<T> itens = cmb.getItems().stream();
			String txtUsr = filter.toString().toLowerCase();
			itens.filter(el -> StringUtils.unaccent(el.toString()).toLowerCase().contains(txtUsr))
					.forEach(filteredList::add);
			cmb.getTooltip().setText(txtUsr);
			Window stage = cmb.getScene().getWindow();
			double posX = stage.getX() + cmb.localToScene(cmb.getBoundsInLocal()).getMinX();
			double posY = stage.getY() + cmb.localToScene(cmb.getBoundsInLocal()).getMinY();
			cmb.getTooltip().show(stage, posX, posY);
			cmb.show();
		}
		cmb.getItems().setAll(filteredList);
	}

	public void handleOnHiding(Event e) {
		filter = "";
		cmb.getTooltip().hide();
		T s = cmb.getSelectionModel().getSelectedItem();
		cmb.getItems().setAll(originalItems);
		cmb.getSelectionModel().select(s);
	}

}
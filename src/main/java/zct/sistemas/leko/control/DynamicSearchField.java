package zct.sistemas.leko.control;

import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import zct.sistemas.leko.util.StringUtils;

public class DynamicSearchField<T> {

	TableView<T> tableView;
	String filter = "";
	private ObservableList<T> originalItems;

	public DynamicSearchField(TextField txtSearch, TableView<T> tableView) {
		this.tableView = tableView;
		originalItems = FXCollections.observableArrayList(tableView.getItems());
		txtSearch.setOnKeyPressed(this::handleOnKeyPressed);
	}

	public void handleOnKeyPressed(KeyEvent e) {
		ObservableList<T> filteredList = FXCollections.observableArrayList();
		KeyCode code = e.getCode();

		if (e.isShortcutDown()) {
			filter = "";
		}
		if (code.isLetterKey() || code.isKeypadKey()) {
			filter += StringUtils.unaccent(e.getText());
		}
		if (code == KeyCode.BACK_SPACE && filter.length() > 0) {
			filter = filter.substring(0, filter.length() - 1);
			this.tableView.getItems().setAll(originalItems);
		}
		if (code == KeyCode.ESCAPE) {
			filter = "";
		}
		if (filter.length() == 0) {
			filteredList = originalItems;
		} else {
			Stream<T> itens = this.tableView.getItems().stream();
			String txtUsr = filter.toString().toLowerCase();
			itens.filter(el -> StringUtils.unaccent(el.toString()).toLowerCase().contains(txtUsr))
					.forEach(filteredList::add);
		}
		this.tableView.getItems().setAll(filteredList);
	}

	public void filter(String predicate) {
		ObservableList<T> filteredList = FXCollections.observableArrayList();
		Stream<T> itens = this.tableView.getItems().stream();
		itens.filter(el -> el.toString().toLowerCase().contains(predicate.toLowerCase())).forEach(filteredList::add);
		tableView.refresh();
	}

}

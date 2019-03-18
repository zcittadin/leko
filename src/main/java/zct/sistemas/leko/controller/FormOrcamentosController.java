package zct.sistemas.leko.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import teste.ComboBoxAutoComplete;
import zct.sistemas.leko.model.Item;

public class FormOrcamentosController implements Initializable {

	@FXML
	private ComboBox<Item> comboItens;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}
	
	public void setContext(List<Item> itens) {
		Item[] itensArr = new Item[itens.size()];
		itensArr = itens.toArray(itensArr);
		comboItens.getItems().addAll(itensArr);
		comboItens.setTooltip(new Tooltip());
		new ComboBoxAutoComplete<Item>(comboItens);
	}

}

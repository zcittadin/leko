package zct.sistemas.leko.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import zct.sistemas.leko.constants.Unidades;

public class ItensController implements Initializable {

	@FXML
	private ComboBox<Unidades> cmbUnidades;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cmbUnidades.getItems().setAll(Unidades.values());
	}

}

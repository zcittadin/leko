package zct.sistemas.leko.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HomeController implements Initializable {
	
	@FXML
	private Tab tabOrcamentos;
	@FXML
	private Tab tabItens;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tabOrcamentos.setGraphic(new ImageView(new Image(getClass().getResource("/icons/orcamento_32.png").toExternalForm())));
		tabItens.setGraphic(new ImageView(new Image(getClass().getResource("/icons/stock_32.png").toExternalForm())));
	}

}

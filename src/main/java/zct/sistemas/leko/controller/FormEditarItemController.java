package zct.sistemas.leko.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import zct.fx.currency.CurrencyField;
import zct.sistemas.leko.constants.Unidades;
import zct.sistemas.leko.dao.ItensDAO;
import zct.sistemas.leko.model.Item;
import zct.sistemas.leko.util.AlertUtil;
import zct.sistemas.leko.util.NotificationsUtil;

public class FormEditarItemController implements Initializable {

	private Item item;

	@FXML
	private ComboBox<Unidades> comboUnidades;
	@FXML
	private CurrencyField txtValor;
	@FXML
	private TextField txtDescricao;
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;

	private ItensDAO itensDAO = new ItensDAO();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btSave.setGraphic(new ImageView(new Image(getClass().getResource("/icons/save.png").toExternalForm())));
		btCancel.setGraphic(new ImageView(new Image(getClass().getResource("/icons/cancel.png").toExternalForm())));
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtDescricao.requestFocus();
			}
		});
	}

	public void setContext(Item it) {
		this.item = it;
		txtDescricao.setText(it.getDescricao());
		txtValor.setAmount(it.getValorUnitario().doubleValue());
		comboUnidades.getItems().setAll(Unidades.values());
		for (int i = 0; i < Unidades.values().length; i++) {
			if (Unidades.values()[i].getSimbolo().equals(it.getUnidade())) {
				comboUnidades.getSelectionModel().select(Unidades.values()[i]);
			}
		}
	}

	@FXML
	private void save() {
		if ((txtDescricao.getText() == null || "".equals(txtDescricao.getText().trim()))) {
			AlertUtil.makeWarning("Atenção", "Informe a descrição do ítem.");
			txtDescricao.requestFocus();
			return;
		}
		if (txtValor.getAmount() <= 0) {
			AlertUtil.makeWarning("Atenção", "Informe o valor do ítem.");
			txtValor.requestFocus();
			return;
		}
		if (comboUnidades.getValue() == null)

		{
			AlertUtil.makeWarning("Atenção", "Informe a unidade de medida do ítem.");
			comboUnidades.requestFocus();
			return;
		}
		this.item.setDescricao(txtDescricao.getText());
		this.item.setValorUnitario(new BigDecimal(txtValor.getAmount()));
		this.item.setUnidade(comboUnidades.getValue().getSimbolo());
		Task<Void> saveTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				itensDAO.updateItem(item);
				return null;
			}
		};
		saveTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				Stage stage = (Stage) txtDescricao.getScene().getWindow();
				stage.close();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						NotificationsUtil.makeInfo("Edição de ítem", "Dados editados com sucesso.");
					}
				});
			}
		});
		saveTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				AlertUtil.makeError("Erro", "Ocorreu um erro ao tentar gravar o item.");
				Stage stage = (Stage) txtDescricao.getScene().getWindow();
				stage.close();
			}

		});
		new Thread(saveTask).run();
	}

	@FXML
	private void cancel() {
		Stage stage = (Stage) txtDescricao.getScene().getWindow();
		stage.close();
	}

}

package zct.sistemas.leko.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import zct.fx.currency.CurrencyField;
import zct.sistemas.leko.constants.Unidades;
import zct.sistemas.leko.dao.ItensDAO;
import zct.sistemas.leko.model.Item;
import zct.sistemas.leko.model.OrcamentoItem;
import zct.sistemas.leko.util.AlertUtil;
import zct.sistemas.leko.util.NotificationsUtil;

@SuppressWarnings("rawtypes")
public class FormItensController implements Initializable {

	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	@FXML
	private Button btAdd;
	@FXML
	private ComboBox<Unidades> cmbUnidades;
	@FXML
	private TextField txtDescricao;
	@FXML
	private CurrencyField txtValor;
	@FXML
	private TableView<Item> tblItens;
	@FXML
	private TableColumn colDescricao;
	@FXML
	private TableColumn colValor;
	@FXML
	private TableColumn colUnidade;
	@FXML
	private TableColumn colExcluir;

	private ItensDAO itensDAO = new ItensDAO();

	private ObservableList<Item> obsItens = FXCollections.observableArrayList();

	private final String CENTER_COLUMN = "-fx-alignment: CENTER;";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btSave.setGraphic(new ImageView(new Image(getClass().getResource("/icons/save.png").toExternalForm())));
		btCancel.setGraphic(new ImageView(new Image(getClass().getResource("/icons/cancel.png").toExternalForm())));
		btAdd.setGraphic(new ImageView(new Image(getClass().getResource("/icons/add.png").toExternalForm())));
		cmbUnidades.getItems().setAll(Unidades.values());
		prepareTableView();
		tblItens.setItems(obsItens);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtDescricao.requestFocus();
			}
		});
	}

	@FXML
	private void save() {
		if (obsItens.isEmpty()) {
			AlertUtil.makeWarning("Atenção", "Não há ítens listados para serem salvos.");
			txtDescricao.requestFocus();
			return;
		}
		Task<Void> saveTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				obsItens.forEach(it -> {
					itensDAO.saveItem(it);
				});
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
						NotificationsUtil.makeInfo("Cadastro de itens", "Itens cadastrados com sucesso.");
					}
				});
			}
		});
		saveTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent workerStateEvent) {
				AlertUtil.makeError("Erro", "Ocorreu um erro ao tentar gravar o item.");
				Stage stage = (Stage) txtDescricao.getScene().getWindow();
				stage.close();
			}
		});
		new Thread(saveTask).run();
	}

	@FXML
	private void addItem() {
		if ((txtDescricao.getText() == null || "".equals(txtDescricao.getText().trim()))) {
			AlertUtil.makeWarning("Atenção", "Informe a descrição do item.");
			txtDescricao.requestFocus();
			return;
		}
		if (txtValor.getAmount() <= 0) {
			AlertUtil.makeWarning("Atenção", "Informe o valor do item.");
			txtValor.requestFocus();
			return;
		}
		if (cmbUnidades.getValue() == null) {
			AlertUtil.makeWarning("Atenção", "Informe a unidade de medida do item.");
			cmbUnidades.requestFocus();
			return;
		}
		obsItens.add(new Item(null, txtDescricao.getText(), new BigDecimal(txtValor.getAmount()),
				cmbUnidades.getValue().getSimbolo()));
		tblItens.refresh();
		txtDescricao.clear();
		txtValor.setAmount(0.0);
		;
		cmbUnidades.getItems().clear();
		cmbUnidades.getItems().setAll(Unidades.values());
		txtDescricao.requestFocus();
	}

	@FXML
	private void cancel() {
		if (obsItens.size() > 0) {
			Optional<ButtonType> result = AlertUtil.makeConfirm("Atenção",
					"Existem ítens que ainda não foram salvos. Ao continuar os dados serão perdidos. Deseja continuar?");
			if (result.get() == ButtonType.OK) {
				Stage stage = (Stage) txtDescricao.getScene().getWindow();
				stage.close();
			}
		} else {
			Stage stage = (Stage) txtDescricao.getScene().getWindow();
			stage.close();
		}
	}

	@SuppressWarnings("unchecked")
	private void prepareTableView() {
		colDescricao.setCellValueFactory(new PropertyValueFactory<Item, String>("descricao"));
		Callback<TableColumn<Item, BigDecimal>, TableCell<Item, BigDecimal>> cellValorFactory = //
				new Callback<TableColumn<Item, BigDecimal>, TableCell<Item, BigDecimal>>() {
					@Override
					public TableCell<Item, BigDecimal> call(final TableColumn<Item, BigDecimal> param) {
						final TableCell<Item, BigDecimal> cell = new TableCell<Item, BigDecimal>() {
							@Override
							public void updateItem(BigDecimal item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
									setText(null);
								} else {
									Item it = getTableView().getItems().get(getIndex());
									setText(NumberFormat.getCurrencyInstance().format(it.getValorUnitario()));
								}
							}
						};
						return cell;
					}
				};
		colValor.setCellFactory(cellValorFactory);
		colUnidade.setCellValueFactory(new PropertyValueFactory<OrcamentoItem, String>("unidade"));
		Callback<TableColumn<Item, Object>, TableCell<Item, Object>> cellExcluirFactory = //
				new Callback<TableColumn<Item, Object>, TableCell<Item, Object>>() {
					@Override
					public TableCell call(final TableColumn<Item, Object> param) {
						final TableCell<Item, Object> cell = new TableCell<Item, Object>() {
							final Button btn = new Button();

							@Override
							public void updateItem(Object item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
									setText(null);
								} else {
									btn.setOnAction(event -> {
										Optional<ButtonType> result = AlertUtil.makeConfirm("Exclusão",
												"Deseja realmente remover este item?");
										if (result.get() == ButtonType.OK) {
											Item it = getTableView().getItems().get(getIndex());
											obsItens.remove(it);
										}
									});
									btn.setStyle("-fx-graphic: url('/icons/trash.png');");
									btn.setCursor(Cursor.HAND);
									setGraphic(btn);
									setText(null);
								}
							}
						};
						return cell;
					}
				};
		colExcluir.setCellFactory(cellExcluirFactory);
		colDescricao.setStyle(CENTER_COLUMN);
		colValor.setStyle(CENTER_COLUMN);
		colUnidade.setStyle(CENTER_COLUMN);
		colExcluir.setStyle(CENTER_COLUMN);
	}

}

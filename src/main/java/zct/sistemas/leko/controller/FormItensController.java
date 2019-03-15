package zct.sistemas.leko.controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import zct.sistemas.leko.constants.Unidades;
import zct.sistemas.leko.dao.ItensDAO;
import zct.sistemas.leko.model.Item;
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
	private TextField txtValor;
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
				NotificationsUtil.makeInfo("Cadastro de itens", "Item cadastrado com sucesso.");
				txtDescricao.clear();
				txtValor.clear();
				cmbUnidades.getItems().setAll(Unidades.values());
				txtDescricao.requestFocus();
			}

		});
		saveTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent workerStateEvent) {
				AlertUtil.makeError("Erro", "Ocorreu um erro ao tentar gravar o item.");
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

		if ((txtValor.getText() == null || "".equals(txtValor.getText().trim()))) {
			AlertUtil.makeWarning("Atenção", "Informe o valor do item.");
			txtValor.requestFocus();
			return;
		}
		if (cmbUnidades.getValue() == null) {
			AlertUtil.makeWarning("Atenção", "Informe a unidade de medida do item.");
			cmbUnidades.requestFocus();
			return;
		}
		obsItens.add(new Item(null, txtDescricao.getText(), new Double(txtValor.getText()),
				cmbUnidades.getValue().getSimbolo()));
		tblItens.refresh();
		txtDescricao.clear();
		txtValor.clear();
		cmbUnidades.getItems().clear();
		cmbUnidades.getItems().setAll(Unidades.values());
		txtDescricao.requestFocus();
	}

	@FXML
	private void cancel() {
		Stage stage = (Stage) txtDescricao.getScene().getWindow();
		stage.close();
	}

	@SuppressWarnings("unchecked")
	private void prepareTableView() {
		colDescricao.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<Item, String> cell) {
						final Item it = cell.getValue();
						final SimpleObjectProperty<String> simpleObject;
						simpleObject = new SimpleObjectProperty<String>(it.getDescricao());
						return simpleObject;
					}
				});
		colValor.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Item, Double>, ObservableValue<Double>>() {
					public ObservableValue<Double> call(CellDataFeatures<Item, Double> cell) {
						final Item it = cell.getValue();
						final SimpleObjectProperty<Double> simpleObject;
						simpleObject = new SimpleObjectProperty<Double>(it.getValorUnitario());
						return simpleObject;
					}
				});
		colUnidade.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<Item, String> cell) {
						final Item it = cell.getValue();
						final SimpleObjectProperty<String> simpleObject;
						simpleObject = new SimpleObjectProperty<String>(it.getUnidade());
						return simpleObject;
					}
				});
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

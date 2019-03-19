package zct.sistemas.leko.controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import teste.ComboBoxAutoComplete;
import zct.sistemas.leko.model.Item;
import zct.sistemas.leko.model.OrcamentoItem;
import zct.sistemas.leko.shared.ItensShared;
import zct.sistemas.leko.util.AlertUtil;

@SuppressWarnings("rawtypes")
public class OrcamentosController implements Initializable {

	@FXML
	private ComboBox<Item> comboItens;
	@FXML
	private CheckBox checkMaoDeObra;
	@FXML
	private TextField txtMaoDeObra;
	@FXML
	private TextArea txtServicos;
	@FXML
	private TextField txtQuantidade;
	@FXML
	private Button btGenerate;
	@FXML
	private Button btAddItem;
	@FXML
	private TableView tblItens;
	@FXML
	private TableColumn colQuantidade;
	@FXML
	private TableColumn colUnidade;
	@FXML
	private TableColumn colDescricao;
	@FXML
	private TableColumn colValorUnitario;
	@FXML
	private TableColumn colSubTotal;
	@FXML
	private TableColumn colExcluir;
	@FXML
	private Label lblValorTotal;

	private ObservableList<OrcamentoItem> obsOrcamentoItens = FXCollections.observableArrayList();
	private ObservableList<Item> obsItens = FXCollections.observableArrayList();

	private Double valorMaoDeObra = 0.0;
	private Double valorTotal = 0.0;

	private final String CENTER_COLUMN = "-fx-alignment: CENTER;";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btGenerate
				.setGraphic(new ImageView(new Image(getClass().getResource("/icons/orcamento.png").toExternalForm())));
		btAddItem.setGraphic(new ImageView(new Image(getClass().getResource("/icons/add.png").toExternalForm())));
		prepareTableView();
		txtMaoDeObra.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
					txtMaoDeObra.setText(oldValue);
				}
			}
		});
		txtQuantidade.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
					txtQuantidade.setText(oldValue);
				}
			}
		});

		checkMaoDeObra.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue == true) {
					if (txtMaoDeObra.getText() == null || "".equals(txtMaoDeObra.getText().trim())) {
						AlertUtil.makeWarning("Atenção", "Informe o valor da mão-de-obra.");
						txtMaoDeObra.requestFocus();
						checkMaoDeObra.setSelected(false);
						return;
					}
					valorMaoDeObra = new Double(txtMaoDeObra.getText());
					sumMaoDeObra();
				} else {
					if (valorMaoDeObra > 0.0) {
						subtractMaoDeObra();
					}
				}
			}

		});

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtServicos.requestFocus();
			}
		});

		ItensShared.itensProperty.addListener((observable, oldList, newList) -> {
			newList.forEach(it -> {
				obsItens = newList;
				Item[] itensArr = new Item[obsItens.size()];
				itensArr = obsItens.toArray(itensArr);
				comboItens.getItems().clear();
				comboItens.getItems().addAll(itensArr);
				comboItens.setTooltip(new Tooltip());
				new ComboBoxAutoComplete<Item>(comboItens);
			});
		});
	}

	@FXML
	private void generate() {

	}

	@FXML
	private void addItem() {
		if ((txtQuantidade.getText() == null || "".equals(txtQuantidade.getText().trim()))) {
			AlertUtil.makeWarning("Atenção", "Informe a quantidade do ítem selecionado.");
			txtQuantidade.requestFocus();
			return;
		}
		if (comboItens.getSelectionModel().isEmpty()) {
			AlertUtil.makeWarning("Atenção", "Informe o ítem selecionado.");
			comboItens.requestFocus();
			return;
		}
		Double subtotal = calculateSubTotal();
		valorTotal = valorTotal + subtotal;
		lblValorTotal.setText(valorTotal.toString());
		OrcamentoItem orcamento = new OrcamentoItem(txtQuantidade.getText(), comboItens.getValue().getUnidade(),
				comboItens.getValue().getDescricao(), comboItens.getValue().getValorUnitario().toString(),
				subtotal.toString());
		obsOrcamentoItens.add(orcamento);
		txtQuantidade.clear();

	}

	private void sumMaoDeObra() {
		Double valor = new Double(txtMaoDeObra.getText());
		valorTotal = valorTotal + valor;
		lblValorTotal.setText(valorTotal.toString());
	}

	private void subtractMaoDeObra() {
		valorTotal = valorTotal - valorMaoDeObra;
		valorMaoDeObra = 0.0;
		lblValorTotal.setText(valorTotal.toString());
	}

	private Double calculateSubTotal() {
		Double qtde = new Double(txtQuantidade.getText());
		Double valorUnitario = comboItens.getValue().getValorUnitario();
		return qtde * valorUnitario;
	}

	@SuppressWarnings("unchecked")
	private void prepareTableView() {
		colQuantidade.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<OrcamentoItem, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<OrcamentoItem, String> cell) {
						final OrcamentoItem o = cell.getValue();
						final SimpleObjectProperty<String> simpleObject;
						simpleObject = new SimpleObjectProperty<String>(o.getQuantidade());
						return simpleObject;
					}
				});
		colUnidade.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<OrcamentoItem, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<OrcamentoItem, String> cell) {
						final OrcamentoItem o = cell.getValue();
						final SimpleObjectProperty<String> simpleObject;
						simpleObject = new SimpleObjectProperty<String>(o.getUnidade());
						return simpleObject;
					}
				});
		colDescricao.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<OrcamentoItem, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<OrcamentoItem, String> cell) {
						final OrcamentoItem o = cell.getValue();
						final SimpleObjectProperty<String> simpleObject;
						simpleObject = new SimpleObjectProperty<String>(o.getDescricao());
						return simpleObject;
					}
				});
		colValorUnitario.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<OrcamentoItem, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<OrcamentoItem, String> cell) {
						final OrcamentoItem o = cell.getValue();
						final SimpleObjectProperty<String> simpleObject;
						simpleObject = new SimpleObjectProperty<String>(o.getValorUnitario());
						return simpleObject;
					}
				});
		colSubTotal.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<OrcamentoItem, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<OrcamentoItem, String> cell) {
						final OrcamentoItem o = cell.getValue();
						final SimpleObjectProperty<String> simpleObject;
						simpleObject = new SimpleObjectProperty<String>(o.getValorTotal());
						return simpleObject;
					}
				});

		Callback<TableColumn<OrcamentoItem, Object>, TableCell<OrcamentoItem, Object>> cellExcluirFactory = //
				new Callback<TableColumn<OrcamentoItem, Object>, TableCell<OrcamentoItem, Object>>() {
					@Override
					public TableCell call(final TableColumn<OrcamentoItem, Object> param) {
						final TableCell<OrcamentoItem, Object> cell = new TableCell<OrcamentoItem, Object>() {
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
											OrcamentoItem oi = getTableView().getItems().get(getIndex());
											Double sub = new Double(oi.getValorTotal());
											valorTotal = valorTotal - sub;
											lblValorTotal.setText(valorTotal.toString());
											obsOrcamentoItens.remove(oi);
										}
									});
//									Tooltip.install(btn, tooltipDelete);
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

		colQuantidade.setStyle(CENTER_COLUMN);
		colUnidade.setStyle(CENTER_COLUMN);
		colDescricao.setStyle(CENTER_COLUMN);
		colValorUnitario.setStyle(CENTER_COLUMN);
		colSubTotal.setStyle(CENTER_COLUMN);
		colExcluir.setStyle(CENTER_COLUMN);

		tblItens.setItems(obsOrcamentoItens);

	}

}

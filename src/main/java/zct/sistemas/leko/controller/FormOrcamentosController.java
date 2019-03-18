package zct.sistemas.leko.controller;

import java.net.URL;
import java.util.List;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import teste.ComboBoxAutoComplete;
import zct.sistemas.leko.model.Item;
import zct.sistemas.leko.model.OrcamentoItem;
import zct.sistemas.leko.util.AlertUtil;

@SuppressWarnings("rawtypes")
public class FormOrcamentosController implements Initializable {

	@FXML
	private ComboBox<Item> comboItens;
	@FXML
	private TextField txtMaoDeObra;
	@FXML
	private TextArea txtServicos;
	@FXML
	private TextField txtQuantidade;
	@FXML
	private Button btGenerate;
	@FXML
	private Button btCancel;
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

	private ObservableList<OrcamentoItem> obsOrcamentos = FXCollections.observableArrayList();

	private final String CENTER_COLUMN = "-fx-alignment: CENTER;";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btGenerate
				.setGraphic(new ImageView(new Image(getClass().getResource("/icons/orcamento.png").toExternalForm())));
		btCancel.setGraphic(new ImageView(new Image(getClass().getResource("/icons/cancel.png").toExternalForm())));
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
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtMaoDeObra.requestFocus();
			}
		});
	}

	@FXML
	private void generate() {

	}

	@FXML
	private void addItem() {
		OrcamentoItem orcamento = new OrcamentoItem(txtQuantidade.getText(), comboItens.getValue().getUnidade(),
				comboItens.getValue().getDescricao(), comboItens.getValue().getValorUnitario().toString(), "99");
		String s = calculateSubTotal(comboItens.getValue().getValorUnitario().toString(), txtQuantidade.getText());
//		Double subTotal = new Double(txtQuantidade.getText());
		obsOrcamentos.add(orcamento);

	}

	@FXML
	private void cancel() {
		Stage stage = (Stage) txtServicos.getScene().getWindow();
		stage.close();
	}

	private String calculateSubTotal(String valorUnitario, String quantidade) {
		Double vu = new Double(valorUnitario);
		Double qtde = new Double(quantidade);
		Double result = vu * qtde;
		System.out.println(result);
		return null;
	}

	public void setContext(List<Item> itens) {
		Item[] itensArr = new Item[itens.size()];
		itensArr = itens.toArray(itensArr);
		comboItens.getItems().addAll(itensArr);
		comboItens.setTooltip(new Tooltip());
		new ComboBoxAutoComplete<Item>(comboItens);
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

		tblItens.setItems(obsOrcamentos);

	}

}

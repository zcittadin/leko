package zct.sistemas.leko.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.MaskerPane;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import teste.ComboBoxAutoComplete;
import zct.fx.currency.CurrencyField;
import zct.sistemas.leko.dao.DadosHeaderDAO;
import zct.sistemas.leko.model.DadosHeader;
import zct.sistemas.leko.model.Item;
import zct.sistemas.leko.model.Orcamento;
import zct.sistemas.leko.model.OrcamentoItem;
import zct.sistemas.leko.report.OrcamentoReport;
import zct.sistemas.leko.shared.DadosHeaderShared;
import zct.sistemas.leko.shared.ItensShared;
import zct.sistemas.leko.util.AlertUtil;

@SuppressWarnings("rawtypes")
public class OrcamentosController implements Initializable {

	@FXML
	private AnchorPane mainPane;
	@FXML
	private ComboBox<Item> comboItens;
	@FXML
	private CheckBox checkMaoDeObra;
	@FXML
	private CurrencyField txtMaoDeObra;
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

	private MaskerPane maskerPane = new MaskerPane();

	private DadosHeaderDAO dadosHeaderDAO = new DadosHeaderDAO();

	private ObservableList<OrcamentoItem> obsOrcamentoItens = FXCollections.observableArrayList();
	private ObservableList<Item> obsItens = FXCollections.observableArrayList();

	private BigDecimal valorMaoDeObra = new BigDecimal(0);
	private BigDecimal valorTotal = new BigDecimal(0);

	private final String CENTER_COLUMN = "-fx-alignment: CENTER;";

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		configMaskerPane();

		Task<List<DadosHeader>> findTask = new Task<List<DadosHeader>>() {
			@Override
			protected List<DadosHeader> call() throws Exception {
				return dadosHeaderDAO.findDados();
			}
		};
		findTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				DadosHeaderShared.setDadosHeader(findTask.getValue().get(0));
			}
		});
		findTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				AlertUtil.makeError("Erro", "Ocorreu uma falha ao buscar os dados.");
			}
		});
		new Thread(findTask).run();

		btGenerate
				.setGraphic(new ImageView(new Image(getClass().getResource("/icons/orcamento_24.png").toExternalForm())));
		btAddItem.setGraphic(new ImageView(new Image(getClass().getResource("/icons/add.png").toExternalForm())));
		lblValorTotal.setText(NumberFormat.getCurrencyInstance().format(new BigDecimal(0)));
		prepareTableView();
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
					if (txtMaoDeObra.getAmount() <= 0) {
						AlertUtil.makeWarning("Atenção", "Informe o valor da mão-de-obra.");
						txtMaoDeObra.requestFocus();
						checkMaoDeObra.setSelected(false);
						return;
					}
					valorMaoDeObra = new BigDecimal(txtMaoDeObra.getAmount());
					sumMaoDeObra();
					txtMaoDeObra.setDisable(true);
				} else {
					if (valorMaoDeObra.doubleValue() > 0.0) {
						subtractMaoDeObra();
					}
					txtMaoDeObra.setDisable(false);
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
		if (txtMaoDeObra.getAmount() <= 0 || checkMaoDeObra.isSelected() == false) {
			AlertUtil.makeWarning("Atenção", "Informe o valor da mão-de-obra");
			txtMaoDeObra.requestFocus();
			return;
		}
		if (obsOrcamentoItens.isEmpty()) {
			AlertUtil.makeWarning("Atenção", "É necessário no mínimo um ítem para compôr o orçamento.");
			comboItens.requestFocus();
			return;
		}
		Stage stage = new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("PDF Files", "*.pdf"));
		fileChooser.setTitle("Salvar orçamento");
		fileChooser.setInitialFileName("ORÇAMENTO.pdf");
		File savedFile = fileChooser.showSaveDialog(stage);
		if (savedFile != null) {
			Task<Integer> reportTask = new Task<Integer>() {
				@Override
				protected Integer call() throws Exception {
					maskerPane.setVisible(true);
					Orcamento orcamento = new Orcamento(valorMaoDeObra.toString(), valorTotal.toString(),
							txtServicos.getText(), obsOrcamentoItens);
					int result = OrcamentoReport.generatePdfReport(savedFile.getAbsolutePath(), orcamento,
							DadosHeaderShared.getDadosHeader());
					return new Integer(result);
				}
			};
			reportTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					maskerPane.setVisible(false);
					int r = reportTask.getValue();
					if (r == 1)
						try {
							Desktop.getDesktop().open(savedFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					else
						AlertUtil.makeError("Erro!", "Ocorreu uma falha ao emitir o orçamento.");
				}
			});
			new Thread(reportTask).start();
		}
	}

	@FXML
	private void addItem() {
		if (txtMaoDeObra.getAmount() <= 0 || checkMaoDeObra.isSelected() == false) {
			AlertUtil.makeWarning("Atenção", "Informe o valor da mão-de-obra");
			txtMaoDeObra.requestFocus();
			return;
		}
		if (comboItens.getSelectionModel().isEmpty()) {
			AlertUtil.makeWarning("Atenção", "Selecione um ítem para adicionar.");
			comboItens.requestFocus();
			return;
		}
		if ((txtQuantidade.getText() == null || "".equals(txtQuantidade.getText().trim()))) {
			AlertUtil.makeWarning("Atenção", "Informe a quantidade do ítem selecionado.");
			txtQuantidade.requestFocus();
			return;
		}
		BigDecimal subtotal = calculateSubTotal();
		valorTotal = valorTotal.add(subtotal);
		lblValorTotal.setText(NumberFormat.getCurrencyInstance().format(new BigDecimal(valorTotal.toString())));
		OrcamentoItem orcamento = new OrcamentoItem(txtQuantidade.getText(), comboItens.getValue().getUnidade(),
				comboItens.getValue().getDescricao(), comboItens.getValue().getValorUnitario().toString(),
				subtotal.toString());
		obsOrcamentoItens.add(orcamento);
		txtQuantidade.clear();

	}

	private void sumMaoDeObra() {
		BigDecimal valor = new BigDecimal(txtMaoDeObra.getAmount());
		valorTotal = valorTotal.add(valor);
		lblValorTotal.setText(NumberFormat.getCurrencyInstance().format(new BigDecimal(valorTotal.toString())));
	}

	private void subtractMaoDeObra() {
		valorTotal = valorTotal.subtract(valorMaoDeObra);
		valorMaoDeObra = new BigDecimal(0);
		lblValorTotal.setText(NumberFormat.getCurrencyInstance().format(new BigDecimal(valorTotal.toString())));
	}

	private BigDecimal calculateSubTotal() {
		BigDecimal qtde = new BigDecimal(txtQuantidade.getText());
		BigDecimal valorUnitario = comboItens.getValue().getValorUnitario();
		return qtde.multiply(valorUnitario);
	}

	@SuppressWarnings("unchecked")
	private void prepareTableView() {
		colQuantidade.setCellValueFactory(new PropertyValueFactory<OrcamentoItem, String>("quantidade"));
		colQuantidade.setCellFactory(TextFieldTableCell.forTableColumn());
		colQuantidade.setOnEditCommit(new EventHandler<CellEditEvent<OrcamentoItem, String>>() {
			@Override
			public void handle(CellEditEvent<OrcamentoItem, String> t) {
				((OrcamentoItem) t.getTableView().getItems().get(t.getTablePosition().getRow()))
						.setQuantidade(t.getNewValue());
			}
		});

		colUnidade.setCellValueFactory(new PropertyValueFactory<OrcamentoItem, String>("unidade"));
		colDescricao.setCellValueFactory(new PropertyValueFactory<OrcamentoItem, String>("descricao"));

		Callback<TableColumn<OrcamentoItem, String>, TableCell<OrcamentoItem, String>> cellValorFactory = //
				new Callback<TableColumn<OrcamentoItem, String>, TableCell<OrcamentoItem, String>>() {
					@Override
					public TableCell<OrcamentoItem, String> call(final TableColumn<OrcamentoItem, String> param) {
						final TableCell<OrcamentoItem, String> cell = new TableCell<OrcamentoItem, String>() {
							@Override
							public void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
									setText(null);
								} else {
									OrcamentoItem it = getTableView().getItems().get(getIndex());
									setText(NumberFormat.getCurrencyInstance()
											.format(new BigDecimal(it.getValorUnitario())));
								}
							}
						};
						return cell;
					}
				};
		colValorUnitario.setCellFactory(cellValorFactory);

//		colSubTotal.setCellValueFactory(new PropertyValueFactory<OrcamentoItem, String>("subtotal"));
		Callback<TableColumn<OrcamentoItem, String>, TableCell<OrcamentoItem, String>> cellSubTotalFactory = //
				new Callback<TableColumn<OrcamentoItem, String>, TableCell<OrcamentoItem, String>>() {
					@Override
					public TableCell<OrcamentoItem, String> call(final TableColumn<OrcamentoItem, String> param) {
						final TableCell<OrcamentoItem, String> cell = new TableCell<OrcamentoItem, String>() {
							@Override
							public void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
									setText(null);
								} else {
									OrcamentoItem it = getTableView().getItems().get(getIndex());
									setText(NumberFormat.getCurrencyInstance()
											.format(new BigDecimal(it.getSubtotal())));
								}
							}
						};
						return cell;
					}
				};
		colSubTotal.setCellFactory(cellSubTotalFactory);

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
											BigDecimal sub = new BigDecimal(oi.getSubtotal());
											valorTotal = valorTotal.subtract(sub);
											lblValorTotal.setText(NumberFormat.getCurrencyInstance()
													.format(new BigDecimal(valorTotal.toString())));
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

	private void configMaskerPane() {
		mainPane.getChildren().add(maskerPane);
		AnchorPane.setBottomAnchor(maskerPane, 0.0);
		AnchorPane.setTopAnchor(maskerPane, 0.0);
		AnchorPane.setLeftAnchor(maskerPane, 0.0);
		AnchorPane.setRightAnchor(maskerPane, 0.0);
		maskerPane.setText("Processando dados...");
		maskerPane.setVisible(false);
	}

}

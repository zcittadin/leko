package zct.sistemas.leko.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import teste.ComboBoxAutoComplete;
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

	private DadosHeaderDAO dadosHeaderDAO = new DadosHeaderDAO();

	private ObservableList<OrcamentoItem> obsOrcamentoItens = FXCollections.observableArrayList();
	private ObservableList<Item> obsItens = FXCollections.observableArrayList();

	private Double valorMaoDeObra = 0.0;
	private Double valorTotal = 0.0;

	private final String CENTER_COLUMN = "-fx-alignment: CENTER;";

	@Override
	public void initialize(URL location, ResourceBundle resources) {

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
					txtMaoDeObra.setDisable(true);
				} else {
					if (valorMaoDeObra > 0.0) {
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
		Stage stage = new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("PDF Files", "*.pdf"));
		fileChooser.setTitle("Salvar relatório de viagem");
		fileChooser.setInitialFileName("ORÇAMETO.pdf");
		File savedFile = fileChooser.showSaveDialog(stage);
		if (savedFile != null) {
			Task<Integer> reportTask = new Task<Integer>() {
				@Override
				protected Integer call() throws Exception {
					Orcamento orcamento = new Orcamento(valorMaoDeObra.toString(), txtServicos.getText(),
							obsOrcamentoItens);
					int result = OrcamentoReport.generatePdfReport(savedFile.getAbsolutePath(), orcamento,
							DadosHeaderShared.getDadosHeader());
					return new Integer(result);
				}
			};
			reportTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
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
		colValorUnitario.setCellValueFactory(new PropertyValueFactory<OrcamentoItem, String>("valorUnitario"));
		colSubTotal.setCellValueFactory(new PropertyValueFactory<OrcamentoItem, String>("subtotal"));
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
											Double sub = new Double(oi.getSubTotal());
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

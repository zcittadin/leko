package zct.sistemas.leko.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.MaskerPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import zct.sistemas.leko.control.DynamicSearchField;
import zct.sistemas.leko.dao.ItensDAO;
import zct.sistemas.leko.model.Item;
import zct.sistemas.leko.shared.ItensShared;
import zct.sistemas.leko.util.AlertUtil;
import zct.sistemas.leko.util.NotificationsUtil;

@SuppressWarnings("rawtypes")
public class ItensController implements Initializable {

	@FXML
	private AnchorPane mainPane;
	@FXML
	private Button btNovo;
	@FXML
	private Button btDadosHeader;
	@FXML
	private TableView<Item> tblItens;
	@FXML
	private TableColumn colDescricao;
	@FXML
	private TableColumn colValor;
	@FXML
	private TableColumn colUnidade;
	@FXML
	private TableColumn colEditar;
	@FXML
	private TableColumn colExcluir;
	@FXML
	private StackPane paneTable;
	@FXML
	private TextField txtSearch;
	@FXML
	private Label lblQtdeItens;

	private Tooltip tooltipEditar = new Tooltip("Editar este ítem");
	private Tooltip tooltipExcluir = new Tooltip("Excluir este ítem");

	private MaskerPane maskerPane = new MaskerPane();

	private ItensDAO itensDAO = new ItensDAO();
	private DynamicSearchField<Item> dsf;

	private List<Item> itens;
	private static ObservableList<Item> obsItens = FXCollections.observableArrayList();

	private final String CENTER_COLUMN = "-fx-alignment: CENTER;";
	private static String TOOLTIP_CSS = "-fx-font-size: 8pt; -fx-font-weight: bold; -fx-background-color: #008000;";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btNovo.setGraphic(
				new ImageView(new Image(getClass().getResource("/icons/shopping_cart_24.png").toExternalForm())));
		btDadosHeader
				.setGraphic(new ImageView(new Image(getClass().getResource("/icons/header_24.png").toExternalForm())));
		configMaskerPane();
		prepareTableView();
		configToolTips();
		retrieveItens();
	}

	@FXML
	private void addItem() {
		try {
			Stage stage;
			Parent root;
			stage = new Stage();
			URL url = getClass().getResource("/fxml/FormItens.fxml");
			FXMLLoader fxmlloader = new FXMLLoader();
			fxmlloader.setLocation(url);
			fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
			root = (Parent) fxmlloader.load(url.openStream());
			stage.setScene(new Scene(root));
			stage.getIcons().add(new Image("/icons/tomada.png"));
			stage.setTitle("Cadastro de ítens");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(tblItens.getScene().getWindow());
			stage.setResizable(false);
			stage.showAndWait();
			retrieveItens();
			tblItens.refresh();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void editItem(Item it) {
		try {
			Stage stage;
			Parent root;
			stage = new Stage();
			URL url = getClass().getResource("/fxml/FormEditarItem.fxml");
			FXMLLoader fxmlloader = new FXMLLoader();
			fxmlloader.setLocation(url);
			fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
			root = (Parent) fxmlloader.load(url.openStream());
			stage.setScene(new Scene(root));
			((FormEditarItemController) fxmlloader.getController()).setContext(it);
			stage.getIcons().add(new Image("/icons/tomada.png"));
			stage.setTitle("Editar ítem");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(tblItens.getScene().getWindow());
			stage.setResizable(false);
			stage.showAndWait();
			if (!"".equals(txtSearch.getText().trim()) && txtSearch.getText() != null) {
				dsf.filter(txtSearch.getText());
			} else {
				retrieveItens();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void editHeader() {
		try {
			Stage stage;
			Parent root;
			stage = new Stage();
			URL url = getClass().getResource("/fxml/DadosHeader.fxml");
			FXMLLoader fxmlloader = new FXMLLoader();
			fxmlloader.setLocation(url);
			fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
			root = (Parent) fxmlloader.load(url.openStream());
			stage.setScene(new Scene(root));
			stage.getIcons().add(new Image("/icons/tomada.png"));
			stage.setTitle("Editar dados de cabeçalho");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(tblItens.getScene().getWindow());
			stage.setResizable(false);
			stage.showAndWait();
			retrieveItens();
			tblItens.refresh();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void retrieveItens() {
		Task<Void> searchTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				maskerPane.setVisible(true);
				itens = itensDAO.findItens();
				return null;
			}
		};
		searchTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent workerStateEvent) {
				maskerPane.setVisible(false);
				obsItens.clear();
				obsItens = FXCollections.observableArrayList(itens);
				ItensShared.setItens(obsItens);
				tblItens.setItems(obsItens);
				dsf = new DynamicSearchField<Item>(txtSearch, tblItens);
				lblQtdeItens.setText(obsItens.size() + " ítens cadastrados");
			}
		});
		searchTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent workerStateEvent) {
				maskerPane.setVisible(false);
				AlertUtil.makeError("Erro", "Ocorreu um erro ao consultar a frota de veículos.");
			}
		});
		new Thread(searchTask).run();
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

		colUnidade.setCellValueFactory(new PropertyValueFactory<Item, String>("unidade"));

		Callback<TableColumn<Item, Object>, TableCell<Item, Object>> cellEditFactory = //
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
										Item it = getTableView().getItems().get(getIndex());
										editItem(it);
									});
									Tooltip.install(btn, tooltipEditar);
									btn.setStyle("-fx-graphic: url('/icons/pencil.png');");
									btn.setCursor(Cursor.HAND);
									setGraphic(btn);
									setText(null);
								}
							}
						};
						return cell;
					}
				};
		colEditar.setCellFactory(cellEditFactory);

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
											Task<Void> exclusionTask = new Task<Void>() {
												@Override
												protected Void call() throws Exception {
													maskerPane.setVisible(true);
													itensDAO.removeItem(it);
													return null;
												}
											};
											exclusionTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
												@Override
												public void handle(WorkerStateEvent arg0) {
													NotificationsUtil.makeInfo("Cadastro de ítens",
															"Item removido com sucesso");
													retrieveItens();
													tblItens.refresh();
													maskerPane.setVisible(false);
												}
											});
											exclusionTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
												@Override
												public void handle(WorkerStateEvent arg0) {
													maskerPane.setVisible(false);
													AlertUtil.makeError("Erro", "Ocorreu um erro ao remover o ítem.");
												}
											});
											new Thread(exclusionTask).run();
										}
									});
									Tooltip.install(btn, tooltipExcluir);
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
		colEditar.setStyle(CENTER_COLUMN);
		colExcluir.setStyle(CENTER_COLUMN);

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

	private void configToolTips() {
		tooltipEditar.setStyle(TOOLTIP_CSS);
		tooltipExcluir.setStyle(TOOLTIP_CSS);
	}
}

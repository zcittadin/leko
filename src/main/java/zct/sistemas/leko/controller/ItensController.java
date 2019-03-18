package zct.sistemas.leko.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import zct.sistemas.leko.dao.ItensDAO;
import zct.sistemas.leko.model.Item;
import zct.sistemas.leko.util.AlertUtil;

@SuppressWarnings("rawtypes")
public class ItensController implements Initializable {

	@FXML
	private Button btNovo;
	@FXML
	private Button btOrcamento;
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

	private ItensDAO itensDAO = new ItensDAO();

	private List<Item> itens;
	private static ObservableList<Item> obsItens = FXCollections.observableArrayList();

	private Pagination pagination;

	private final String CENTER_COLUMN = "-fx-alignment: CENTER;";
	private final static int ROWS_PER_PAGE = 17;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btNovo.setGraphic(new ImageView(new Image(getClass().getResource("/icons/add.png").toExternalForm())));
		btOrcamento.setGraphic(new ImageView(new Image(getClass().getResource("/icons/money.png").toExternalForm())));
		prepareTableView();
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

	@FXML
	private void createOrcamento() {
		try {
			Stage stage;
			Parent root;
			stage = new Stage();
			URL url = getClass().getResource("/fxml/FormOrcamentos.fxml");
			FXMLLoader fxmlloader = new FXMLLoader();
			fxmlloader.setLocation(url);
			fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
			root = (Parent) fxmlloader.load(url.openStream());
			stage.setScene(new Scene(root));
			((FormOrcamentosController) fxmlloader.getController()).setContext(itens);
			stage.getIcons().add(new Image("/icons/tomada.png"));
			stage.setTitle("Novo orçamento");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(tblItens.getScene().getWindow());
			stage.setResizable(false);
			stage.showAndWait();
//			tblItens.refresh();
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
//				if (maskerPane.isVisible() == false)
//					maskerPane.setVisible(true);
				itens = itensDAO.findItens();
				return null;
			}
		};
		searchTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(WorkerStateEvent workerStateEvent) {
//				maskerPane.setVisible(false);

				obsItens.clear();
				obsItens = FXCollections.observableArrayList(itens);
//				tblItens.setItems(obsItens);
				pagination = new Pagination((obsItens.size() / ROWS_PER_PAGE + 1), 0);
				pagination.setPageFactory(new Callback<Integer, Node>() {
					@Override
					public Node call(Integer pageIndex) {
						return createPage(pageIndex);
					}
				});
				paneTable.getChildren().add(pagination);
				updateTableView();
			}
		});
		searchTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent workerStateEvent) {
//				maskerPane.setVisible(false);
				AlertUtil.makeError("Erro", "Ocorreu um erro ao consultar a frota de veículos.");
			}
		});
		new Thread(searchTask).run();
	}

	@SuppressWarnings("unchecked")
	private Node createPage(int pageIndex) {
		int fromIndex = pageIndex * ROWS_PER_PAGE;
		int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, obsItens.size());
		tblItens.setItems(FXCollections.observableArrayList(obsItens.subList(fromIndex, toIndex)));
		return new BorderPane(tblItens);
	}

	@SuppressWarnings("unchecked")
	private void updateTableView() {
		int fromIndex = 0 * ROWS_PER_PAGE;
		int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, obsItens.size());
		int minIndex = Math.min(toIndex, obsItens.size());
		int totalPage = (int) (Math.ceil(obsItens.size() * 1.0 / ROWS_PER_PAGE));
		pagination.setPageCount(totalPage);
		SortedList<Item> sortedData = new SortedList<>(
				FXCollections.observableArrayList(obsItens.subList(Math.min(fromIndex, minIndex), minIndex)));
		sortedData.comparatorProperty().bind(tblItens.comparatorProperty());
		tblItens.setItems(sortedData);
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
									// Tooltip.install(btn, tooltipEdit);
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
												"Deseja realmente remover este veículo?");
										if (result.get() == ButtonType.OK) {
											Item it = getTableView().getItems().get(getIndex());
											Task<Void> exclusionTask = new Task<Void>() {
												@Override
												protected Void call() throws Exception {
//													maskerPane.setVisible(true);
//													veiculoDAO.removeVeiculo(it);
													return null;
												}
											};
											exclusionTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
												@Override
												public void handle(WorkerStateEvent arg0) {
//													NotificationsUtil.makeInfo("Cadastro de veículos",
//															"Veículo removido com sucesso");

//													veiculosItens.remove(it);
//													tblVeiculos.refresh();
//													maskerPane.setVisible(false);
												}
											});
											exclusionTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
												@Override
												public void handle(WorkerStateEvent arg0) {
//													maskerPane.setVisible(false);
													AlertUtil.makeError("Erro",
															"Ocorreu um erro ao remover o veículo.");
												}
											});
											new Thread(exclusionTask).run();
										}
									});
//									Tooltip.install(btn, tooltipDelete);
									btn.setStyle("-fx-graphic: url('/icons/cancel.png');");
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
}

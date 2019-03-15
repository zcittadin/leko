package zct.sistemas.leko.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
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
			stage.setTitle("Adicionar veículo");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(tblItens.getScene().getWindow());
			stage.setResizable(false);
			stage.showAndWait();
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
		colDescricao.setStyle(CENTER_COLUMN);
		colValor.setStyle(CENTER_COLUMN);
		colUnidade.setStyle(CENTER_COLUMN);

	}
}

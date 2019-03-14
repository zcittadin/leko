package teste;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.*;
import javafx.concurrent.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;

/**
 * Example of displaying a splash page for a standalone JavaFX application
 */
public class FadeApp extends Application {
	public static final String APPLICATION_ICON = "http://cdn1.iconfinder.com/data/icons/Copenhagen/PNG/32/people.png";
	public static final String SPLASH_IMAGE = "zct/sistemas/leko/main/logo3_coopermaq.png";
//	public static final String SPLASH_IMAGE = "http://fxexperience.com/wp-content/uploads/2010/06/logo.png";

	private Pane splashLayout;
	private ProgressBar loadProgress;
	private Label progressText;
	private Label info;
	private Stage mainStage;
	private static final int SPLASH_WIDTH = 380;
	private static final int SPLASH_HEIGHT = 227;

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void init() {
		ImageView splash = new ImageView(new Image(SPLASH_IMAGE));
		loadProgress = new ProgressBar();
		loadProgress.setPrefWidth(SPLASH_WIDTH - 20);
		progressText = new Label("Will find friends for peanuts . . .");
		info = new Label("Prime Supervisor 1.0");
		splashLayout = new VBox();
		splashLayout.getChildren().addAll(splash, loadProgress, progressText);
		progressText.setAlignment(Pos.CENTER);
		info.setMinWidth(SPLASH_WIDTH);
		splashLayout.setStyle("-fx-padding: 5; " + "-fx-background-color: #DBEAFF; " + "-fx-border-width:2; "
				+ "-fx-border-color: #000080;");
//		splashLayout.setStyle(
//				"-fx-padding: 5; " + "-fx-background-color: #DBEAFF; " + "-fx-border-width:5; " + "-fx-border-color: "
//						+ "linear-gradient(" + "to bottom, " + "chocolate, " + "derive(chocolate, 50%)" + ");");
		splashLayout.setEffect(new DropShadow());
	}

	@Override
	public void start(final Stage initStage) throws Exception {
		final Task<ObservableList<String>> initialTask = new Task<ObservableList<String>>() {
			@Override
			protected ObservableList<String> call() throws InterruptedException {
				ObservableList<String> foundResources = FXCollections.<String>observableArrayList();
				ObservableList<String> availableResources = FXCollections.observableArrayList("Viagens", "Registros",
						"Coordenadas", "Dados de medições", "Parâmetros programados", "Dados de coleta", "Veículos",
						"Dados de relatórios");

				updateMessage("Prime - Carregando módulos . . .");
				for (int i = 0; i < availableResources.size(); i++) {
					Thread.sleep(1000);
					updateProgress(i + 1, availableResources.size());
					String nextResource = availableResources.get(i);
					foundResources.add(nextResource);
					updateMessage("Prime - Carregando módulos . . . ok " + nextResource);
				}
				Thread.sleep(1000);
				updateMessage("Módulos carregados.");

				return foundResources;
			}
		};

		showSplash(initStage, initialTask, () -> showMainStage(initialTask.valueProperty()));
		new Thread(initialTask).start();
	}

	private void showMainStage(ReadOnlyObjectProperty<ObservableList<String>> friends) {
		mainStage = new Stage(StageStyle.DECORATED);
		mainStage.setTitle("My Friends");
		mainStage.getIcons().add(new Image(APPLICATION_ICON));

		final ListView<String> resourcesView = new ListView<>();
		resourcesView.itemsProperty().bind(friends);

		mainStage.setScene(new Scene(resourcesView));
		mainStage.show();
	}

	private void showSplash(final Stage initStage, Task<?> task, InitCompletionHandler initCompletionHandler) {
		progressText.textProperty().bind(task.messageProperty());
		loadProgress.progressProperty().bind(task.progressProperty());
		task.stateProperty().addListener((observableValue, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				loadProgress.progressProperty().unbind();
				loadProgress.setProgress(1);
				initStage.toFront();
				FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
				fadeSplash.setFromValue(1.0);
				fadeSplash.setToValue(0.0);
				fadeSplash.setOnFinished(actionEvent -> initStage.hide());
				fadeSplash.play();

				initCompletionHandler.complete();
			} // todo add code to gracefully handle other task states.
		});

		Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
		final Rectangle2D bounds = Screen.getPrimary().getBounds();
		initStage.setScene(splashScene);
		initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
		initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
		initStage.initStyle(StageStyle.TRANSPARENT);
		initStage.setAlwaysOnTop(true);
		initStage.show();
	}

	public interface InitCompletionHandler {
		void complete();
	}
}
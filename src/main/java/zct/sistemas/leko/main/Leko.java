package zct.sistemas.leko.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import zct.sistemas.leko.util.HibernateUtil;

public class Leko extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
//		loader.setResources(new ResourceWrapper());
		loader.setLocation(getClass().getResource("/fxml/Item.fxml"));
		loader.load();
		Scene sc = new Scene((Parent) loader.getRoot());

		primaryStage.getIcons().add(new Image("/icons/tomada.png"));
		primaryStage.setTitle("Eletro Leko");
		primaryStage.setMinHeight(400);
		primaryStage.setMinWidth(700);
		primaryStage.setScene(sc);
		primaryStage.setMaximized(true);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void stop() throws Exception {
		HibernateUtil.closeSessionFactory();
		System.exit(0);
	}

//	private static class ResourceWrapper extends ListResourceBundle {
//		@Override
//		protected Object[][] getContents() {
//			return new Object[0][];
//		}
//	}
}

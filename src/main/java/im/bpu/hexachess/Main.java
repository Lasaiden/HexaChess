package im.bpu.hexachess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader mainWindowLoader = new FXMLLoader(getClass().getResource("ui/mainWindow.fxml"));
		mainWindowLoader.setController(new MainWindow());
		Parent root = mainWindowLoader.load();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("ui/style.css").toExternalForm());
		stage.setTitle("HexaChess");
		stage.setScene(scene);
		stage.show();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
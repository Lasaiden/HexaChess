package im.bpu.hexachess;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

public class StartWindow {
	@FXML private Button loginButton;
	@FXML
	private void openLogin() {
		loadWindow("ui/loginWindow.fxml", new LoginWindow());
	}
	@FXML
	private void openRegister() {
		loadWindow("ui/registerWindow.fxml", new RegisterWindow());
	}
	private void loadWindow(String path, Object controller) {
		try {
			FXMLLoader windowLoader = new FXMLLoader(getClass().getResource(path));
			windowLoader.setController(controller);
			Parent root = windowLoader.load();
			loginButton.getScene().setRoot(root);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
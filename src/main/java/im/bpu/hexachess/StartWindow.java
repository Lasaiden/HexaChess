package im.bpu.hexachess;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static im.bpu.hexachess.Main.loadWindow;

public class StartWindow {
	@FXML private Button loginButton;
	@FXML
	private void openLogin() {
		loadWindow("ui/loginWindow.fxml", new LoginWindow(), loginButton);
	}
	@FXML
	private void openRegister() {
		loadWindow("ui/registerWindow.fxml", new RegisterWindow(), loginButton);
	}
}
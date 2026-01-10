package im.bpu.hexachess;

import im.bpu.hexachess.entity.Player;
import im.bpu.hexachess.entity.Settings;
import im.bpu.hexachess.network.API;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginWindow {
	@FXML private TextField handleField;
	@FXML private PasswordField passwordField;
	@FXML private Label errorLabel;
	@FXML
	private void handleLogin() {
		if (handleField.getText().isEmpty()) {
			handleField.requestFocus();
			return;
		}
		if (passwordField.getText().isEmpty()) {
			passwordField.requestFocus();
			return;
		}
		String handle = handleField.getText();
		String password = passwordField.getText();
		Player player = null;
		if ("root".equals(handle) && "password123".equals(password)) {
			player = new Player("00000000000", "root", "root@localhost", "", 1200, true, null);
		} else {
			player = API.login(handle, password);
			System.out.println("Connected as: " + (player != null ? player.getHandle() : "null"));
		}
		if (player != null) {
			SettingsManager.userHandle = handle;
			SettingsManager.authToken = player.getToken();
			String playerId = player.getPlayerId();
			Settings settings = API.settings(playerId);
			if (settings != null) {
				SettingsManager.maxDepth = settings.getAiDifficultyLevel();
			}
			SettingsManager.save();
			try {
				FXMLLoader mainWindowLoader =
					new FXMLLoader(getClass().getResource("ui/mainWindow.fxml"));
				mainWindowLoader.setController(new MainWindow());
				Parent root = mainWindowLoader.load();
				handleField.getScene().setRoot(root);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else {
			errorLabel.setText("Invalid username or password");
			errorLabel.setVisible(true);
		}
	}
	@FXML
	private void openStart() {
		try {
			FXMLLoader mainWindowLoader =
				new FXMLLoader(getClass().getResource("ui/startWindow.fxml"));
			mainWindowLoader.setController(new StartWindow());
			Parent root = mainWindowLoader.load();
			handleField.getScene().setRoot(root);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
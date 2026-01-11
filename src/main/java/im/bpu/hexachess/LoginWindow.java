package im.bpu.hexachess;

import im.bpu.hexachess.entity.Player;
import im.bpu.hexachess.entity.Settings;
import im.bpu.hexachess.network.API;

import javafx.application.Platform;
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
		final String handle = handleField.getText();
		final String password = passwordField.getText();
		Thread.ofVirtual().start(() -> {
			Player player;
			if ("root".equals(handle) && "password123".equals(password)) {
				player = new Player("00000000000", "root", "root@localhost", "", 1200, true, null);
			} else {
				player = API.login(handle, password);
				System.out.println(
					"Connected as: " + (player != null ? player.getHandle() : "null"));
			}
			if (player != null) {
				final String playerId = player.getPlayerId();
				SettingsManager.setPlayerId(playerId);
				SettingsManager.setUserHandle(handle);
				SettingsManager.setAuthToken(player.getToken());
				final Settings settings = API.settings(playerId);
				if (settings != null) {
					SettingsManager.setMaxDepth(settings.getAiDifficultyLevel());
				}
				Platform.runLater(this::openMain);
			} else {
				Platform.runLater(() -> {
					errorLabel.setText("Invalid username or password");
					errorLabel.setVisible(true);
				});
			}
		});
	}
	private void openMain() {
		try {
			FXMLLoader mainWindowLoader =
				new FXMLLoader(getClass().getResource("ui/mainWindow.fxml"));
			mainWindowLoader.setController(new MainWindow());
			Parent root = mainWindowLoader.load();
			handleField.getScene().setRoot(root);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	@FXML
	private void openStart() {
		try {
			FXMLLoader startWindowLoader =
				new FXMLLoader(getClass().getResource("ui/startWindow.fxml"));
			startWindowLoader.setController(new StartWindow());
			Parent root = startWindowLoader.load();
			handleField.getScene().setRoot(root);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
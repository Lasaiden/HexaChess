package im.bpu.hexachess;

import im.bpu.hexachess.entity.Player;
import im.bpu.hexachess.network.API;

import java.security.SecureRandom;
import java.util.Base64;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterWindow {
	@FXML private TextField handleField;
	@FXML private TextField emailField;
	@FXML private PasswordField passwordField;
	@FXML private Label statusLabel;
	@FXML
	private void handleRegister() {
		if (handleField.getText().isEmpty()) {
			handleField.requestFocus();
			return;
		}
		if (emailField.getText().isEmpty()) {
			emailField.requestFocus();
			return;
		}
		if (passwordField.getText().isEmpty()) {
			passwordField.requestFocus();
			return;
		}
		byte[] bytes = new byte[9];
		SecureRandom rand = new SecureRandom();
		rand.nextBytes(bytes);
		String playerId =
			Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, 11);
		String handle = handleField.getText();
		Player player = new Player(
			playerId, handle, emailField.getText(), passwordField.getText(), 1200, false, null);
		boolean registerSuccess = API.register(player);
		if (registerSuccess) {
			SettingsManager.userHandle = handle;
			SettingsManager.save();
			openMain();
		} else {
			statusLabel.setText("Error (Username taken or server error)");
			statusLabel.setVisible(true);
		}
	}
	@FXML
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
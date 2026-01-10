package im.bpu.hexachess;

import im.bpu.hexachess.entity.Settings;
import im.bpu.hexachess.network.API;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class SettingsWindow {
	@FXML private ComboBox<String> maxDepthComboBox;
	@FXML private Button backButton;
	@FXML
	private void initialize() {
		maxDepthComboBox.getItems().addAll("Fast", "Default", "Slowest");
		switch (SettingsManager.maxDepth) {
			case 1 -> maxDepthComboBox.getSelectionModel().select("Fast");
			case 3 -> maxDepthComboBox.getSelectionModel().select("Default");
			case 5 -> maxDepthComboBox.getSelectionModel().select("Slowest");
			default -> maxDepthComboBox.getSelectionModel().select("Default");
		}
	}
	@FXML
	private void openMain() {
		String selected = maxDepthComboBox.getValue();
		if (selected != null) {
			switch (selected) {
				case "Fast" -> SettingsManager.maxDepth = 1;
				case "Default" -> SettingsManager.maxDepth = 3;
				case "Slowest" -> SettingsManager.maxDepth = 5;
			}
			Settings settings = new Settings(
				SettingsManager.playerId, "default", true, false, SettingsManager.maxDepth);
			API.settings(settings);
			SettingsManager.save();
		}
		try {
			FXMLLoader mainWindowLoader =
				new FXMLLoader(getClass().getResource("ui/mainWindow.fxml"));
			mainWindowLoader.setController(new MainWindow());
			Parent root = mainWindowLoader.load();
			backButton.getScene().setRoot(root);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	@FXML
	private void openStart() {
		SettingsManager.playerId = null;
		SettingsManager.userHandle = null;
		SettingsManager.authToken = null;
		SettingsManager.save();
		try {
			FXMLLoader startWindowLoader =
				new FXMLLoader(getClass().getResource("ui/startWindow.fxml"));
			startWindowLoader.setController(new StartWindow());
			Parent root = startWindowLoader.load();
			backButton.getScene().setRoot(root);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
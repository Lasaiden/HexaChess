package im.bpu.hexachess;

import im.bpu.hexachess.entity.Settings;
import im.bpu.hexachess.network.API;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import static im.bpu.hexachess.Main.loadWindow;

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
		final String selected = maxDepthComboBox.getValue();
		if (selected != null) {
			final int depth = switch (selected) {
				case "Fast" -> 1;
				case "Slowest" -> 5;
				default -> 3;
			};
			SettingsManager.setMaxDepth(depth);
			Thread.ofVirtual().start(() -> {
				final Settings settings = new Settings(
					SettingsManager.playerId, "default", true, false, SettingsManager.maxDepth);
				API.settings(settings);
			});
		}
		loadWindow("ui/mainWindow.fxml", new MainWindow(), backButton);
	}
	@FXML
	private void openStart() {
		SettingsManager.setPlayerId(null);
		SettingsManager.setUserHandle(null);
		SettingsManager.setAuthToken(null);
		loadWindow("ui/startWindow.fxml", new StartWindow(), backButton);
	}
}
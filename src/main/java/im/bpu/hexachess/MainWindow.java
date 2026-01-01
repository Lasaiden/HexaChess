package im.bpu.hexachess;

import im.bpu.hexachess.ui.HexPanel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;

public class MainWindow {
	private HexPanel hexPanel;
	@FXML private Button settingsHelpButton;
	@FXML private Canvas canvas;
	@FXML
	private void initialize() {
		hexPanel = new HexPanel(canvas, State.getState());
	}
	@FXML
	private void restart() {
		hexPanel.restart();
	}
	@FXML
	private void rewind() {
		hexPanel.rewind();
	}
	@FXML
	private void openSettings() {
		try {
			FXMLLoader settingsWindowLoader =
				new FXMLLoader(getClass().getResource("ui/settingsWindow.fxml"));
			settingsWindowLoader.setController(new SettingsWindow());
			Parent root = settingsWindowLoader.load();
			settingsHelpButton.getScene().setRoot(root);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
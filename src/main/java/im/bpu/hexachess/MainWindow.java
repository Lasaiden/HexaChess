package im.bpu.hexachess;

import im.bpu.hexachess.ui.HexPanel;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainWindow {
	private HexPanel hexPanel;
	@FXML private Button settingsHelpButton;
	@FXML private VBox sidebar;
	@FXML private Canvas canvas;
	@FXML
	private void initialize() {
		hexPanel = new HexPanel(canvas, State.getState());
		sidebar.setTranslateX(-160);
		sidebar.setVisible(false);
	}
	@FXML
	private void toggleSidebar() {
		boolean isClosed = !sidebar.isVisible();
		Duration duration = Duration.millis(160);
		TranslateTransition transition = new TranslateTransition(duration, sidebar);
		if (isClosed) {
			transition.setToX(0);
			sidebar.setVisible(true);
		} else {
			transition.setToX(-160);
			transition.setOnFinished(event -> sidebar.setVisible(false));
		}
		transition.play();
	}
	@FXML
	private void onBoardClicked() {
		if (sidebar.isVisible()) {
			toggleSidebar();
		}
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
	@FXML
	private void openHelpSettings() {
		ContextMenu menu = new ContextMenu();
		MenuItem settingsItem = new MenuItem("Settings");
		MenuItem helpItem = new MenuItem("Help");
		settingsItem.setOnAction(event -> openSettings());
		helpItem.setOnAction(event -> openSettings());
		menu.getItems().addAll(settingsItem, helpItem);
		menu.show(settingsHelpButton, Side.BOTTOM, 0, 0);
	}
	@FXML
	private void openSearch() {
		try {
			FXMLLoader searchWindowLoader =
				new FXMLLoader(getClass().getResource("ui/searchWindow.fxml"));
			searchWindowLoader.setController(new SearchWindow());
			Parent root = searchWindowLoader.load();
			settingsHelpButton.getScene().setRoot(root);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	@FXML
	private void openProfile() {
		try {
			FXMLLoader profileWindowLoader =
				new FXMLLoader(getClass().getResource("ui/profileWindow.fxml"));
			profileWindowLoader.setController(new ProfileWindow());
			Parent root = profileWindowLoader.load();
			settingsHelpButton.getScene().setRoot(root);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
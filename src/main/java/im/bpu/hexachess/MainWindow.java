package im.bpu.hexachess;

import im.bpu.hexachess.entity.Player;
import im.bpu.hexachess.network.API;
import im.bpu.hexachess.ui.HexPanel;

import javafx.animation.TranslateTransition;
// import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
// import javafx.scene.text.Font;
import javafx.util.Duration;

public class MainWindow {
	private static final String BASE_URL =
		"https://www.chess.com/bundles/web/images/noavatar_l.gif";
	private HexPanel hexPanel;
	@FXML private Button settingsHelpButton;
	@FXML private VBox sidebar;
	@FXML private Canvas canvas;
	@FXML private Button restartButton;
	@FXML private Button rewindButton;
	@FXML private ImageView avatarIcon;
	@FXML private Label handleLabel;
	@FXML private Region countryFlagIcon;
	@FXML private Label ratingLabel;
	@FXML private ImageView opponentAvatarIcon;
	@FXML private Label opponentHandleLabel;
	@FXML private Region opponentCountryFlagIcon;
	@FXML private Label opponentRatingLabel;
	/*
	@FXML private Label fontFamilyLabel;
	@FXML private Label fontNameLabel;
	*/
	@FXML
	private void initialize() {
		State state = State.getState();
		hexPanel = new HexPanel(canvas, state);
		sidebar.setTranslateX(-160);
		sidebar.setVisible(false);
		loadPlayerItem();
		loadOpponentItem();
		if (state.isMultiplayer) {
			restartButton.setManaged(false);
			restartButton.setVisible(false);
			rewindButton.setManaged(false);
			rewindButton.setVisible(false);
		}
		/*
		Platform.runLater(() -> {
			settingsHelpButton.applyCss();
			Font font = settingsHelpButton.getFont();
			fontFamilyLabel.setText("Family: " + font.getFamily());
			fontNameLabel.setText("Name: " + font.getName());
		});
		*/
	}
	private void loadPlayerItem() {
		String handle = SettingsManager.userHandle;
		Player player = API.profile(handle);
		if (player == null)
			return;
		int rating = player.getRating();
		String location = player.getLocation();
		String avatarUrl = (player.getAvatar() != null && !player.getAvatar().isEmpty())
			? player.getAvatar()
			: BASE_URL;
		avatarIcon.setImage(new Image(avatarUrl, true));
		handleLabel.setText(handle);
		ratingLabel.setText("Rating: " + rating);
		if (location != null && !location.isEmpty()) {
			countryFlagIcon.getStyleClass().add("country-" + location);
		} else {
			countryFlagIcon.setManaged(false);
			countryFlagIcon.setVisible(false);
		}
	}
	private void loadOpponentItem() {
		State state = State.getState();
		String handle = "Computer";
		int rating = ((SettingsManager.maxDepth - 1) / 2 % 3 + 1) * 1200;
		String location = null;
		String avatarUrl = BASE_URL;
		if (state.isMultiplayer) {
			if (state.opponentHandle != null) {
				handle = state.opponentHandle;
				Player opponent = API.profile(handle);
				if (opponent != null) {
					rating = opponent.getRating();
					location = opponent.getLocation();
					avatarUrl = (opponent.getAvatar() != null && !opponent.getAvatar().isEmpty())
						? opponent.getAvatar()
						: BASE_URL;
				}
			}
		}
		opponentAvatarIcon.setImage(new Image(avatarUrl, true));
		opponentHandleLabel.setText(handle);
		opponentRatingLabel.setText("Rating: " + rating);
		if (location != null && !location.isEmpty()) {
			opponentCountryFlagIcon.getStyleClass().add("country-" + location);
		} else {
			opponentCountryFlagIcon.setManaged(false);
			opponentCountryFlagIcon.setVisible(false);
		}
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
		if (State.getState().isMultiplayer)
			return;
		hexPanel.restart();
	}
	@FXML
	private void rewind() {
		if (State.getState().isMultiplayer)
			return;
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
			ProfileWindow.targetHandle = SettingsManager.userHandle;
			FXMLLoader profileWindowLoader =
				new FXMLLoader(getClass().getResource("ui/profileWindow.fxml"));
			profileWindowLoader.setController(new ProfileWindow());
			Parent root = profileWindowLoader.load();
			settingsHelpButton.getScene().setRoot(root);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	@FXML
	private void openTournaments() {
		try {
			FXMLLoader tournamentsWindowLoader =
				new FXMLLoader(getClass().getResource("ui/tournamentsWindow.fxml"));
			tournamentsWindowLoader.setController(new TournamentsWindow());
			Parent root = tournamentsWindowLoader.load();
			settingsHelpButton.getScene().setRoot(root);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
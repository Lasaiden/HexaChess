package im.bpu.hexachess;

import im.bpu.hexachess.entity.Player;
import im.bpu.hexachess.network.API;

import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import static im.bpu.hexachess.Main.getAspectRatio;

public class SearchWindow {
	private static final String BASE_URL =
		"https://www.chess.com/bundles/web/images/noavatar_l.gif";
	@FXML private TextField searchField;
	@FXML private ScrollPane searchPane;
	@FXML private VBox playerContainer;
	@FXML private Button backButton;
	@FXML
	private void initialize() {
		if (getAspectRatio() < 1.5) {
			searchPane.setStyle(
				"-fx-pref-width: 400px; -fx-max-width: 400px;"); // CSS instead of JavaFX's
																 // setPrefWidth/setMaxWidth due to
																 // parsing precedence
		}
	}
	@FXML
	private void handleSearch() {
		playerContainer.getChildren().clear();
		String query = searchField.getText();
		if (query.isEmpty())
			return;
		List<Player> players = API.search(query);
		for (Player player : players) {
			try {
				FXMLLoader playerItemLoader =
					new FXMLLoader(getClass().getResource("ui/playerItem.fxml"));
				HBox playerItem = playerItemLoader.load();
				String handle = player.getHandle();
				int rating = player.getRating();
				String location = player.getLocation();
				String avatarUrl = (player.getAvatar() != null && !player.getAvatar().isEmpty())
					? player.getAvatar()
					: BASE_URL;
				ImageView avatarIcon = (ImageView) playerItem.lookup("#avatarIcon");
				Label handleLabel = (Label) playerItem.lookup("#handleLabel");
				Region countryFlagIcon = (Region) playerItem.lookup("#countryFlagIcon");
				Label ratingLabel = (Label) playerItem.lookup("#ratingLabel");
				Button challengeButton = (Button) playerItem.lookup("#challengeButton");
				avatarIcon.setImage(new Image(avatarUrl, true));
				handleLabel.setText(handle);
				ratingLabel.setText("Rating: " + rating);
				if (location != null && !location.isEmpty()) {
					countryFlagIcon.getStyleClass().add("country-" + location);
				} else {
					countryFlagIcon.setManaged(false);
					countryFlagIcon.setVisible(false);
				}
				playerItem.setOnMouseClicked(event -> openProfile(handle));
				challengeButton.setOnAction(event -> startMatchmaking(handle));
				playerContainer.getChildren().add(playerItem);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
	private void startMatchmaking(String target) {
		new Thread(() -> {
			String handle = Settings.userHandle;
			while (true) {
				String resp = API.challenge(handle, target);
				if (resp != null && !resp.equals("Pending")) {
					Platform.runLater(() -> {
						State state = State.getState();
						state.clear();
						state.isMultiplayer = true;
						state.gameId = resp;
						state.opponentHandle = target;
						state.isWhitePlayer = handle.compareTo(target) < 0;
						openMain();
					});
					break;
				}
				try {
					Thread.sleep(2000);
				} catch (Exception ignored) { // high-frequency polling operation
				}
			}
		}).start();
	}
	private void openProfile(String handle) {
		try {
			ProfileWindow.targetHandle = handle;
			FXMLLoader profileWindowLoader =
				new FXMLLoader(getClass().getResource("ui/profileWindow.fxml"));
			profileWindowLoader.setController(new ProfileWindow());
			Parent root = profileWindowLoader.load();
			backButton.getScene().setRoot(root);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	@FXML
	private void openMain() {
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
}
package im.bpu.hexachess;

import im.bpu.hexachess.entity.Player;
import im.bpu.hexachess.network.API;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import static im.bpu.hexachess.Main.loadWindow;

public class ProfileWindow {
	private static final String BASE_URL =
		"https://www.chess.com/bundles/web/images/noavatar_l.gif";
	private static final Map<String, String> COUNTRIES = new HashMap<>();
	private static final DateTimeFormatter DATE_TIME_FORMATTER =
		DateTimeFormatter.ofPattern("MMM d yyyy");
	static {
		COUNTRIES.put("cn", "China");
		COUNTRIES.put("de", "Germany");
		COUNTRIES.put("es", "Spain");
		COUNTRIES.put("fr", "France");
		COUNTRIES.put("it", "Italy");
		COUNTRIES.put("jp", "Japan");
		COUNTRIES.put("kr", "South Korea");
		COUNTRIES.put("pl", "Poland");
		COUNTRIES.put("ro", "Romania");
		COUNTRIES.put("ru", "Russia");
		COUNTRIES.put("ua", "Ukraine");
		COUNTRIES.put("us", "United States");
	}
	public static String targetHandle;
	@FXML private ImageView avatarIcon;
	@FXML private Label handleLabel;
	@FXML private Region countryFlagIcon;
	@FXML private Label ratingLabel;
	@FXML private Label locationLabel;
	@FXML private Label joinedAtLabel;
	@FXML private Button backButton;
	@FXML
	private void initialize() {
		final String handle = targetHandle != null ? targetHandle : SettingsManager.userHandle;
		final Player player = API.profile(handle);
		if (player == null) {
			final String offline = "Offline";
			avatarIcon.setImage(new Image(BASE_URL, true));
			handleLabel.setText(handle);
			ratingLabel.setText("Rating: " + offline);
			locationLabel.setText(offline);
			joinedAtLabel.setText("Joined: " + offline);
			countryFlagIcon.setManaged(false);
			countryFlagIcon.setVisible(false);
			return;
		}
		final int rating = player.getRating();
		final String location = player.getLocation();
		final LocalDateTime joinedAt = player.getJoinedAt();
		final String avatarUrl = (player.getAvatar() != null && !player.getAvatar().isEmpty())
			? player.getAvatar()
			: BASE_URL;
		avatarIcon.setImage(new Image(avatarUrl, true));
		handleLabel.setText(handle);
		ratingLabel.setText("Rating: " + rating);
		if (location != null && !location.isEmpty()) {
			final String country = COUNTRIES.getOrDefault(location, location);
			locationLabel.setText(country);
			countryFlagIcon.getStyleClass().add("country-" + location);
		} else {
			countryFlagIcon.setManaged(false);
			countryFlagIcon.setVisible(false);
		}
		if (joinedAt != null) {
			joinedAtLabel.setText("Joined: " + joinedAt.format(DATE_TIME_FORMATTER));
		}
	}
	@FXML
	private void openMain() {
		loadWindow("ui/mainWindow.fxml", new MainWindow(), backButton);
	}
}
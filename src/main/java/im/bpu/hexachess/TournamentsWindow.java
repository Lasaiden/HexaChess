package im.bpu.hexachess;

import im.bpu.hexachess.entity.Tournament;
import im.bpu.hexachess.network.API;

import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import static im.bpu.hexachess.Main.getAspectRatio;

public class TournamentsWindow {
	@FXML private ScrollPane tournamentsPane;
	@FXML private VBox tournamentContainer;
	@FXML private Button backButton;
	@FXML private Label statusLabel = new Label();
	@FXML private Label nameLabel = new Label();
	@FXML private Label dateLabel = new Label();
	@FXML private Label descLabel = new Label();
	@FXML
	private void initialize() {
		if (getAspectRatio() < 1.5) {
			tournamentsPane.setStyle(
				"-fx-pref-width: 400px; -fx-max-width: 400px;"); // CSS instead of JavaFX's
																 // setPrefWidth/setMaxWidth due to
																 // parsing precedence
		}
		List<Tournament> tournaments = API.tournaments();
		if (tournaments.isEmpty()) {
			statusLabel.setText("No tournaments found.");
			tournamentContainer.getChildren().add(statusLabel);
		} else {
			for (Tournament tournament : tournaments) {
				VBox card = new VBox(8);
				card.getStyleClass().add("player-item");
				HBox header = new HBox(10);
				nameLabel.setText(tournament.getName());
				nameLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
				Region spacer = new Region();
				HBox.setHgrow(spacer, Priority.ALWAYS);
				String dateStr = (tournament.getStartTime() != null)
					? tournament.getStartTime().format(
						  DateTimeFormatter.ofPattern("MMM d yyyy HH:mm"))
					: "TBD";
				dateLabel.setText(dateStr);
				dateLabel.setStyle("-fx-font-size: 12px;");
				header.getChildren().addAll(nameLabel, spacer, dateLabel);
				descLabel.setText(tournament.getDescription());
				descLabel.setWrapText(true);
				descLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 14px;");
				if (tournament.getWinnerId() != null) {
					statusLabel.setText("Winner ID: " + tournament.getWinnerId());
					statusLabel.setStyle("-fx-text-fill: #2E8B57;");
				} else {
					statusLabel.setText("Status: Ongoing / Open");
					statusLabel.setStyle("-fx-text-fill: rgb(94, 15, 8);");
				}
				card.getChildren().addAll(header, descLabel, statusLabel);
				tournamentContainer.getChildren().add(card);
			}
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
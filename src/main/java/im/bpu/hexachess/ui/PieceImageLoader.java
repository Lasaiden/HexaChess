package im.bpu.hexachess.ui;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;

class PieceImageLoader {
	private static final String BASE_URL =
		"https://images.chesscomfiles.com/chess-themes/pieces/classic/300/";
	private static final int TOTAL_IMAGES = 12;
	private static final Map<String, Image> images = new HashMap<>();
	private static boolean loaded = false;
	private static void loadImage(String key, int[] loadedCount, Runnable onload) {
		try {
			Image img = new Image(BASE_URL + key + ".png", true);
			img.progressProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.doubleValue() == 1.0) {
					loadedCount[0]++;
					if (loadedCount[0] == TOTAL_IMAGES) {
						loaded = true;
						if (onload != null)
							onload.run();
					}
				}
			});
			images.put(key, img);
		} catch (Exception exception) {
			System.err.println("Exception: " + exception.getMessage());
		}
	}
	static void loadImages(Runnable onload) {
		loaded = false;
		int[] loadedCount = {0};
		for (String c : new String[] {"w", "b"})
			for (String t : new String[] {"p", "r", "n", "b", "q", "k"})
				loadImage(c + t, loadedCount, onload);
	}
	static Image get(String key) {
		return images.get(key);
	}
	static boolean isLoaded() {
		return loaded;
	}
}
package im.bpu.hexachess;

import java.util.prefs.Preferences;

public class SettingsManager {
	private static final Preferences prefs = Preferences.userNodeForPackage(SettingsManager.class);
	public static int maxDepth = prefs.getInt("maxDepth", 3);
	public static String playerId = prefs.get("playerId", null);
	public static String userHandle = prefs.get("userHandle", null);
	public static String authToken = prefs.get("authToken", null);
	public static void setMaxDepth(int value) {
		if (maxDepth != value) {
			maxDepth = value;
			prefs.putInt("maxDepth", value);
		}
	}
	public static void setPlayerId(String value) {
		if (playerId != value) {
			playerId = value;
			update("playerId", value);
		}
	}
	public static void setUserHandle(String value) {
		if (userHandle != value) {
			userHandle = value;
			update("userHandle", value);
		}
	}
	public static void setAuthToken(String value) {
		if (authToken != value) {
			authToken = value;
			update("authToken", value);
		}
	}
	private static void update(String key, String value) {
		if (value != null)
			prefs.put(key, value);
		else
			prefs.remove(key);
	}
}
package im.bpu.hexachess;

import java.util.prefs.Preferences;

public class SettingsManager {
	private static final Preferences prefs = Preferences.userNodeForPackage(SettingsManager.class);
	public static int maxDepth = prefs.getInt("maxDepth", 3);
	public static String playerId = prefs.get("playerId", null);
	public static String userHandle = prefs.get("userHandle", null);
	public static String authToken = prefs.get("authToken", null);
	public static void save() {
		prefs.putInt("maxDepth", maxDepth);
		if (playerId != null)
			prefs.put("playerId", playerId);
		else
			prefs.remove("playerId");
		if (userHandle != null)
			prefs.put("userHandle", userHandle);
		else
			prefs.remove("userHandle");
		if (authToken != null)
			prefs.put("authToken", authToken);
		else
			prefs.remove("authToken");
	}
}
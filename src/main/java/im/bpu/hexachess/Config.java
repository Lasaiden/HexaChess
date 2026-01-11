package im.bpu.hexachess;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
	private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();
	public static String get(final String key, final String defaultValue) {
		final String environmentValue = System.getenv(key);
		if (environmentValue != null) {
			return environmentValue;
		}
		final String dotenvValue = DOTENV.get(key);
		if (dotenvValue != null) {
			return dotenvValue;
		}
		return defaultValue;
	}
}
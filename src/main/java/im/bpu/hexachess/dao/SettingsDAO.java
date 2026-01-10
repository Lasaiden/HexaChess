package im.bpu.hexachess.dao;

import im.bpu.hexachess.entity.Settings;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsDAO extends DAO<Settings> {
	private static final String CREATE =
		"INSERT INTO settings (player_id, theme, show_legal_moves, auto_promote_queen, "
		+ "ai_difficulty_level) VALUES(?, ?, ?, ?, ?)";
	private static final String UPDATE =
		"UPDATE settings SET theme = ?, show_legal_moves = ?, auto_promote_queen = ?, "
		+ "ai_difficulty_level = ? WHERE player_id = ?";
	private static final String DELETE = "DELETE FROM settings WHERE player_id = ?";
	private static final String READ = "SELECT * FROM settings WHERE player_id = ?";
	@Override
	public Settings create(Settings settings) {
		try (PreparedStatement pstmt = connect.prepareStatement(CREATE)) {
			pstmt.setString(1, settings.getPlayerId());
			pstmt.setString(2, settings.getTheme());
			pstmt.setBoolean(3, settings.isShowLegalMoves());
			pstmt.setBoolean(4, settings.isAutoPromoteQueen());
			pstmt.setInt(5, settings.getAiDifficultyLevel());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return settings;
	}
	@Override
	public Settings update(Settings settings) {
		try (PreparedStatement pstmt = connect.prepareStatement(UPDATE)) {
			pstmt.setString(1, settings.getTheme());
			pstmt.setBoolean(2, settings.isShowLegalMoves());
			pstmt.setBoolean(3, settings.isAutoPromoteQueen());
			pstmt.setInt(4, settings.getAiDifficultyLevel());
			pstmt.setString(5, settings.getPlayerId());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return settings;
	}
	@Override
	public void delete(Settings settings) {
		try (PreparedStatement pstmt = connect.prepareStatement(DELETE)) {
			pstmt.setString(1, settings.getPlayerId());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}
	private Settings resultSetToSettings(ResultSet rs) throws SQLException {
		Settings settings = new Settings(rs.getString("player_id"), rs.getString("theme"),
			rs.getBoolean("show_legal_moves"), rs.getBoolean("auto_promote_queen"),
			rs.getInt("ai_difficulty_level"));
		return settings;
	}
	public Settings read(String playerId) {
		Settings settings = null;
		try (PreparedStatement pstmt = connect.prepareStatement(READ)) {
			pstmt.setString(1, playerId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					settings = resultSetToSettings(rs);
				}
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return settings;
	}
}
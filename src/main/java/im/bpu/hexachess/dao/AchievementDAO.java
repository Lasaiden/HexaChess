package im.bpu.hexachess.dao;

import im.bpu.hexachess.entity.Achievement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AchievementDAO extends DAO<Achievement> {
	private static final String CREATE =
		"INSERT INTO achievements (achievement_id, name, description) VALUES(?, ?, ?)";
	private static final String UPDATE =
		"UPDATE achievements SET name = ?, description = ? WHERE achievement_id = ?";
	private static final String DELETE = "DELETE FROM achievements WHERE achievement_id = ?";
	private static final String READ = "SELECT * FROM achievements WHERE achievement_id = ?";
	private static final String READ_ALL = "SELECT * FROM achievements";
	@Override
	public Achievement create(Achievement achievement) {
		try (PreparedStatement pstmt = connect.prepareStatement(CREATE)) {
			pstmt.setString(1, achievement.getAchievementId());
			pstmt.setString(2, achievement.getName());
			pstmt.setString(3, achievement.getDescription());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return achievement;
	}
	@Override
	public Achievement update(Achievement achievement) {
		try (PreparedStatement pstmt = connect.prepareStatement(UPDATE)) {
			pstmt.setString(1, achievement.getName());
			pstmt.setString(2, achievement.getDescription());
			pstmt.setString(3, achievement.getAchievementId());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return achievement;
	}
	@Override
	public void delete(Achievement achievement) {
		try (PreparedStatement pstmt = connect.prepareStatement(DELETE)) {
			pstmt.setString(1, achievement.getAchievementId());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}
	private Achievement resultSetToAchievement(ResultSet rs) throws SQLException {
		Achievement achievement = new Achievement(
			rs.getString("achievement_id"), rs.getString("name"), rs.getString("description"));
		return achievement;
	}
	public Achievement read(String achievementId) {
		Achievement achievement = null;
		try (PreparedStatement pstmt = connect.prepareStatement(READ)) {
			pstmt.setString(1, achievementId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					achievement = resultSetToAchievement(rs);
				}
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return achievement;
	}
	public ArrayList<Achievement> readAll() {
		ArrayList<Achievement> achievements = new ArrayList<>();
		try (Statement stmt = connect.createStatement();
			ResultSet rs = stmt.executeQuery(READ_ALL)) {
			while (rs.next()) {
				achievements.add(resultSetToAchievement(rs));
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return achievements;
	}
}
package im.bpu.hexachess.dao;

import im.bpu.hexachess.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class PlayerDAO extends DAO<Player> {
	@Override
	public Player create(Player obj) {
		String request =
			"INSERT INTO players (player_id, handle, email, password_hash, avatar, rating, "
			+ "location, is_verified, joined_at) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement pstmt = connect.prepareStatement(request);
			pstmt.setString(1, obj.getPlayerId());
			pstmt.setString(2, obj.getHandle());
			pstmt.setString(3, obj.getEmail());
			pstmt.setString(4, obj.getPasswordHash());
			pstmt.setString(5, obj.getAvatar());
			pstmt.setInt(6, obj.getRating());
			pstmt.setString(7, obj.getLocation());
			pstmt.setBoolean(8, obj.isVerified());
			if (obj.getJoinedAt() != null)
				pstmt.setTimestamp(9, Timestamp.valueOf(obj.getJoinedAt()));
			else
				pstmt.setTimestamp(9, null);
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return obj;
	}
	@Override
	public Player update(Player obj) {
		String request = "UPDATE players SET handle = ?, email = ?, password_hash = ?, avatar = ?, "
			+ "rating = ?, location = ?, is_verified = ? WHERE player_id = ?";
		try {
			PreparedStatement pstmt = connect.prepareStatement(request);
			pstmt.setString(1, obj.getHandle());
			pstmt.setString(2, obj.getEmail());
			pstmt.setString(3, obj.getPasswordHash());
			pstmt.setString(4, obj.getAvatar());
			pstmt.setInt(5, obj.getRating());
			pstmt.setString(6, obj.getLocation());
			pstmt.setBoolean(7, obj.isVerified());
			pstmt.setString(8, obj.getPlayerId());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return obj;
	}
	@Override
	public void delete(Player obj) {
		String request = "DELETE FROM players WHERE player_id = ?";
		try {
			PreparedStatement pstmt = connect.prepareStatement(request);
			pstmt.setString(1, obj.getPlayerId());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}
	private Player resultSetToPlayer(ResultSet rs) throws SQLException {
		Player player = new Player(rs.getString("player_id"), rs.getString("handle"),
			rs.getString("email"), rs.getString("password_hash"), rs.getInt("rating"),
			rs.getBoolean("is_verified"),
			rs.getTimestamp("joined_at") != null ? rs.getTimestamp("joined_at").toLocalDateTime()
												 : null);
		player.setAvatar(rs.getString("avatar"));
		player.setLocation(rs.getString("location"));
		return player;
	}
	public Player read(String id) {
		Player player = null;
		String request = "SELECT * FROM players WHERE player_id = ?";
		try {
			PreparedStatement pstmt = connect.prepareStatement(request);
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				player = resultSetToPlayer(rs);
			}
			rs.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return player;
	}
	public ArrayList<Player> readAll() {
		ArrayList<Player> list = new ArrayList<>();
		String request = "SELECT * FROM players";
		try {
			ResultSet rs = stmt.executeQuery(request);
			while (rs.next()) {
				list.add(resultSetToPlayer(rs));
			}
			rs.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return list;
	}
	public Player login(String handle, String password) {
		Player player = null;
		String request = "SELECT * FROM players WHERE handle = ? AND password_hash = ?";
		try {
			PreparedStatement pstmt = connect.prepareStatement(request);
			pstmt.setString(1, handle);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				player = resultSetToPlayer(rs);
			}
			rs.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return player;
	}
	public Player getPlayerByHandle(String handle) {
		Player player = null;
		String request = "SELECT * FROM players WHERE handle = ?";
		try {
			PreparedStatement pstmt = connect.prepareStatement(request);
			pstmt.setString(1, handle);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				player = resultSetToPlayer(rs);
			}
			rs.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return player;
	}
	public ArrayList<Player> searchPlayers(String query) {
		ArrayList<Player> players = new ArrayList<>();
		String request = "SELECT * FROM players WHERE handle LIKE ?";
		try {
			PreparedStatement pstmt = connect.prepareStatement(request);
			pstmt.setString(1, "%" + query + "%");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				players.add(resultSetToPlayer(rs));
			}
			rs.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return players;
	}
}
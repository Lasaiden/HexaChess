package im.bpu.hexachess.dao;

import im.bpu.hexachess.entity.Tournament;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TournamentDAO extends DAO<Tournament> {
	@Override
	public Tournament create(Tournament obj) {
		String request = "INSERT INTO tournaments (tournament_id, name, description, start_time, "
			+ "end_time, winner_id) VALUES(?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement pstmt = connect.prepareStatement(request);
			pstmt.setString(1, obj.getTournamentId());
			pstmt.setString(2, obj.getName());
			pstmt.setString(3, obj.getDescription());
			// Gestion des dates pouvant être nulles
			if (obj.getStartTime() != null)
				pstmt.setTimestamp(4, Timestamp.valueOf(obj.getStartTime()));
			else
				pstmt.setTimestamp(4, null);
			if (obj.getEndTime() != null)
				pstmt.setTimestamp(5, Timestamp.valueOf(obj.getEndTime()));
			else
				pstmt.setTimestamp(5, null);
			pstmt.setString(6, obj.getWinnerId());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return obj;
	}
	@Override
	public Tournament update(Tournament obj) {
		String request = "UPDATE tournaments SET name = ?, description = ?, start_time = ?, "
			+ "end_time = ?, winner_id = ? WHERE tournament_id = ?";
		try {
			PreparedStatement pstmt = connect.prepareStatement(request);
			pstmt.setString(1, obj.getName());
			pstmt.setString(2, obj.getDescription());
			if (obj.getStartTime() != null)
				pstmt.setTimestamp(3, Timestamp.valueOf(obj.getStartTime()));
			else
				pstmt.setTimestamp(3, null);
			if (obj.getEndTime() != null)
				pstmt.setTimestamp(4, Timestamp.valueOf(obj.getEndTime()));
			else
				pstmt.setTimestamp(4, null);
			pstmt.setString(5, obj.getWinnerId());
			pstmt.setString(6, obj.getTournamentId());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return obj;
	}
	@Override
	public void delete(Tournament obj) {
		String request = "DELETE FROM tournaments WHERE tournament_id = ?";
		try {
			PreparedStatement pstmt = connect.prepareStatement(request);
			pstmt.setString(1, obj.getTournamentId());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}
	public Tournament read(String id) {
		Tournament tournament = null;
		String request = "SELECT * FROM tournaments WHERE tournament_id = ?";
		try {
			PreparedStatement pstmt = connect.prepareStatement(request);
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				tournament = new Tournament(rs.getString("tournament_id"), rs.getString("name"),
					rs.getString("description"),
					rs.getTimestamp("start_time") != null
						? rs.getTimestamp("start_time").toLocalDateTime()
						: null,
					rs.getTimestamp("end_time") != null
						? rs.getTimestamp("end_time").toLocalDateTime()
						: null,
					rs.getString("winner_id"));
			}
			rs.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return tournament;
	}
}
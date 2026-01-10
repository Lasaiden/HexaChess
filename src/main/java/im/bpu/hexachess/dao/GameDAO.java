package im.bpu.hexachess.dao;

import im.bpu.hexachess.entity.Game;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class GameDAO extends DAO<Game> {
	private static final String CREATE =
		"INSERT INTO games (game_id, white_player_id, black_player_id, winner_id, tournament_id, "
		+ "moves, start_time, end_time, victory_type) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE = "UPDATE games SET moves = ?, end_time = ?, winner_id = ?, "
		+ "victory_type = ? WHERE game_id = ?";
	private static final String DELETE = "DELETE FROM games WHERE game_id = ?";
	private static final String READ = "SELECT * FROM games WHERE game_id = ?";
	@Override
	public Game create(Game game) {
		try (PreparedStatement pstmt = connect.prepareStatement(CREATE)) {
			pstmt.setString(1, game.getGameId());
			pstmt.setString(2, game.getWhitePlayerId());
			pstmt.setString(3, game.getBlackPlayerId());
			pstmt.setString(4, game.getWinnerId());
			pstmt.setString(5, game.getTournamentId());
			pstmt.setString(6, game.getMoves());
			if (game.getStartTime() != null)
				pstmt.setTimestamp(7, Timestamp.valueOf(game.getStartTime()));
			else
				pstmt.setTimestamp(7, null);
			if (game.getEndTime() != null)
				pstmt.setTimestamp(8, Timestamp.valueOf(game.getEndTime()));
			else
				pstmt.setTimestamp(8, null);
			pstmt.setString(9, game.getVictoryType());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return game;
	}
	@Override
	public Game update(Game game) {
		try (PreparedStatement pstmt = connect.prepareStatement(UPDATE)) {
			pstmt.setString(1, game.getMoves());
			if (game.getEndTime() != null)
				pstmt.setTimestamp(2, Timestamp.valueOf(game.getEndTime()));
			else
				pstmt.setTimestamp(2, null);
			pstmt.setString(3, game.getWinnerId());
			pstmt.setString(4, game.getVictoryType());
			pstmt.setString(5, game.getGameId());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return game;
	}
	@Override
	public void delete(Game game) {
		try (PreparedStatement pstmt = connect.prepareStatement(DELETE)) {
			pstmt.setString(1, game.getGameId());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}
	private Game resultSetToGame(ResultSet rs) throws SQLException {
		Game game = new Game(rs.getString("game_id"), rs.getString("white_player_id"),
			rs.getString("black_player_id"), rs.getString("winner_id"),
			rs.getString("tournament_id"), rs.getString("moves"),
			rs.getTimestamp("start_time") != null ? rs.getTimestamp("start_time").toLocalDateTime()
												  : null,
			rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime()
												: null,
			rs.getString("victory_type"));
		return game;
	}
	public Game read(String gameId) {
		Game game = null;
		try (PreparedStatement pstmt = connect.prepareStatement(READ)) {
			pstmt.setString(1, gameId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					game = resultSetToGame(rs);
				}
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return game;
	}
}
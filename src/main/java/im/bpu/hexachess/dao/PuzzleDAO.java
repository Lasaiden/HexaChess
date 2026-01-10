package im.bpu.hexachess.dao;

import im.bpu.hexachess.entity.Puzzle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

public class PuzzleDAO extends DAO<Puzzle> {
	private static final String CREATE = "INSERT INTO puzzles (puzzle_id, moves, solutions, "
		+ "rating, theme, created_at) VALUES(?, ?, ?, ?, ?, ?)";
	private static final String UPDATE =
		"UPDATE puzzles SET moves = ?, solutions = ?, rating = ?, theme = ? WHERE puzzle_id = ?";
	private static final String DELETE = "DELETE FROM puzzles WHERE puzzle_id = ?";
	private static final String READ = "SELECT * FROM puzzles WHERE puzzle_id = ?";
	private static final String READ_ALL = "SELECT * FROM puzzles";
	@Override
	public Puzzle create(Puzzle puzzle) {
		try (PreparedStatement pstmt = connect.prepareStatement(CREATE)) {
			pstmt.setString(1, puzzle.getPuzzleId());
			pstmt.setString(2, puzzle.getMoves());
			pstmt.setString(3, puzzle.getSolutions());
			pstmt.setInt(4, puzzle.getRating());
			pstmt.setString(5, puzzle.getTheme());
			if (puzzle.getCreatedAt() != null)
				pstmt.setTimestamp(6, Timestamp.valueOf(puzzle.getCreatedAt()));
			else
				pstmt.setTimestamp(6, null);
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return puzzle;
	}
	@Override
	public Puzzle update(Puzzle puzzle) {
		try (PreparedStatement pstmt = connect.prepareStatement(UPDATE)) {
			pstmt.setString(1, puzzle.getMoves());
			pstmt.setString(2, puzzle.getSolutions());
			pstmt.setInt(3, puzzle.getRating());
			pstmt.setString(4, puzzle.getTheme());
			pstmt.setString(5, puzzle.getPuzzleId());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return puzzle;
	}
	@Override
	public void delete(Puzzle puzzle) {
		try (PreparedStatement pstmt = connect.prepareStatement(DELETE)) {
			pstmt.setString(1, puzzle.getPuzzleId());
			pstmt.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}
	private Puzzle resultSetToPuzzle(ResultSet rs) throws SQLException {
		Puzzle puzzle = new Puzzle(rs.getString("puzzle_id"), rs.getString("moves"),
			rs.getString("solutions"), rs.getInt("rating"), rs.getString("theme"),
			rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime()
												  : null);
		return puzzle;
	}
	public Puzzle read(String puzzleId) {
		Puzzle puzzle = null;
		try (PreparedStatement pstmt = connect.prepareStatement(READ)) {
			pstmt.setString(1, puzzleId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					puzzle = resultSetToPuzzle(rs);
				}
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return puzzle;
	}
	public ArrayList<Puzzle> readAll() {
		ArrayList<Puzzle> puzzles = new ArrayList<>();
		try (Statement stmt = connect.createStatement();
			ResultSet rs = stmt.executeQuery(READ_ALL)) {
			while (rs.next()) {
				puzzles.add(resultSetToPuzzle(rs));
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return puzzles;
	}
}
package src.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
	private int[][] rookDirections = {{1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}};
	private int[][] bishopDirections = {{1, -2}, {2, -1}, {1, 1}, {-1, 2}, {-2, 1}, {-1, -1}};
	private int[][] knightOffsets = {{1, -3}, {2, -3}, {3, -2}, {3, -1}, {2, 1}, {1, 2}, {-1, 3},
		{-2, 3}, {-3, 2}, {-3, 1}, {-2, -1}, {-1, -2}};
	private int[][] whitePawnCaptures = {{-1, 0}, {1, -1}};
	private int[][] blackPawnCaptures = {{1, 0}, {-1, 1}};
	Map<AxialCoordinate, Piece> pieces = new HashMap<>();
	public boolean isWhiteTurn = true;
	public Board() {
		int[][] kings = {{1, 4}};
		int[][] queens = {{-1, 5}};
		int[][] rooks = {{-3, 5}, {3, 2}};
		int[][] bishops = {{0, 5}, {0, 4}, {0, 3}};
		int[][] knights = {{-2, 5}, {2, 3}};
		int[][] pawns = {
			{-4, 5}, {-3, 4}, {-2, 3}, {-1, 2}, {0, 1}, {1, 1}, {2, 1}, {3, 1}, {4, 1}};
		placeSymmetricPieces(kings, PieceType.KING, PieceType.QUEEN);
		placeSymmetricPieces(queens, PieceType.QUEEN, PieceType.KING);
		placeSymmetricPieces(rooks, PieceType.ROOK, PieceType.ROOK);
		placeSymmetricPieces(bishops, PieceType.BISHOP, PieceType.BISHOP);
		placeSymmetricPieces(knights, PieceType.KNIGHT, PieceType.KNIGHT);
		placeSymmetricPieces(pawns, PieceType.PAWN, PieceType.PAWN);
	}
	public Board(Board other) {
		for (Map.Entry<AxialCoordinate, Piece> entry : other.pieces.entrySet())
			pieces.put(entry.getKey(), entry.getValue());
		isWhiteTurn = other.isWhiteTurn;
	}
	public Piece getPiece(AxialCoordinate coord) {
		return pieces.get(coord);
	}
	public void movePiece(AxialCoordinate from, AxialCoordinate to) {
		pieces.put(to, pieces.remove(from));
		isWhiteTurn = !isWhiteTurn;
	}
	private void addStepMoves(AxialCoordinate pos, Piece p, int[][] offsets, List<Move> moves) {
		for (int[] o : offsets) {
			AxialCoordinate target = pos.add(o[0], o[1]);
			if (!target.isValid())
				continue;
			Piece occupant = pieces.get(target);
			if (occupant == null || occupant.isWhite != p.isWhite)
				moves.add(new Move(pos, target));
		}
	}
	private void addSlidingMoves(
		AxialCoordinate pos, Piece p, int[][] directions, List<Move> moves) {
		for (int[] d : directions) {
			AxialCoordinate target = pos.add(d[0], d[1]);
			while (target.isValid()) {
				Piece occupant = pieces.get(target);
				if (occupant == null || occupant.isWhite != p.isWhite)
					moves.add(new Move(pos, target));
				if (occupant != null)
					break;
				target = target.add(d[0], d[1]);
			}
		}
	}
	private void addPawnMoves(AxialCoordinate pos, Piece p, List<Move> moves) {
		int forwardR = p.isWhite ? -1 : 1;
		AxialCoordinate fwd = pos.add(0, forwardR);
		if (fwd.isValid() && pieces.get(fwd) == null)
			moves.add(new Move(pos, fwd));
		for (int[] off : p.isWhite ? whitePawnCaptures : blackPawnCaptures) {
			AxialCoordinate cap = pos.add(off[0], off[1]);
			if (!cap.isValid())
				continue;
			Piece target = pieces.get(cap);
			if (target != null && target.isWhite != p.isWhite)
				moves.add(new Move(pos, cap));
		}
	}
	private List<Move> getMoves(AxialCoordinate pos, Piece p) {
		List<Move> moves = new ArrayList<>();
		switch (p.type) {
			case KING -> {
				addStepMoves(pos, p, rookDirections, moves);
				addStepMoves(pos, p, bishopDirections, moves);
			}
			case QUEEN -> {
				addSlidingMoves(pos, p, rookDirections, moves);
				addSlidingMoves(pos, p, bishopDirections, moves);
			}
			case ROOK -> addSlidingMoves(pos, p, rookDirections, moves);
			case BISHOP -> addSlidingMoves(pos, p, bishopDirections, moves);
			case KNIGHT -> addStepMoves(pos, p, knightOffsets, moves);
			case PAWN -> addPawnMoves(pos, p, moves);
		}
		return moves;
	}
	public List<Move> listMoves(boolean forWhite) {
		List<Move> moves = new ArrayList<>();
		for (Map.Entry<AxialCoordinate, Piece> entry : pieces.entrySet())
			if (entry.getValue().isWhite == forWhite)
				moves.addAll(getMoves(entry.getKey(), entry.getValue()));
		return moves;
	}
	private void placePiece(int q, int r, PieceType type, boolean isWhite) {
		pieces.put(new AxialCoordinate(q, r), new Piece(type, isWhite));
	}
	private void placeSymmetricPieces(int[][] positions, PieceType whiteType, PieceType blackType) {
		for (int[] pos : positions) {
			placePiece(pos[0], pos[1], whiteType, true);
			placePiece(-pos[0], -pos[1], blackType, false);
		}
	}
}
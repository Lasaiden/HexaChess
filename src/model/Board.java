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
	private AxialCoordinate enPassant;
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
		enPassant = other.enPassant;
	}
	public Piece getPiece(AxialCoordinate coord) {
		return pieces.get(coord);
	}
	private boolean isPromotionCell(AxialCoordinate pos, boolean isWhite) {
		return !pos.add(0, isWhite ? -1 : 1).isValid();
	}
	public void movePiece(AxialCoordinate from, AxialCoordinate to) {
		Piece p = pieces.remove(from);
		if (p.type == PieceType.PAWN) {
			if (to.equals(enPassant))
				pieces.remove(new AxialCoordinate(to.q, p.isWhite ? to.r + 1 : to.r - 1));
			int dr = to.r - from.r;
			enPassant = Math.abs(dr) == 2 ? new AxialCoordinate(from.q, from.r + dr / 2) : null;
			if (isPromotionCell(to, p.isWhite))
				p = new Piece(PieceType.QUEEN, p.isWhite);
		} else {
			enPassant = null;
		}
		pieces.put(to, p);
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
	private boolean isPawnStartCell(AxialCoordinate pos, boolean isWhite) {
		int q = pos.q, r = pos.r;
		if (isWhite)
			return q <= 0 ? r == 1 - q : r == 1;
		return q >= 0 ? r == -1 - q : r == -1;
	}
	private void addPawnMoves(AxialCoordinate pos, Piece p, List<Move> moves) {
		int direction = p.isWhite ? -1 : 1;
		AxialCoordinate fwd = pos.add(0, direction);
		if (fwd.isValid() && pieces.get(fwd) == null) {
			moves.add(new Move(pos, fwd));
			if (isPawnStartCell(pos, p.isWhite)) {
				AxialCoordinate fwd2 = fwd.add(0, direction);
				if (fwd2.isValid() && pieces.get(fwd2) == null)
					moves.add(new Move(pos, fwd2));
			}
		}
		for (int[] o : p.isWhite ? whitePawnCaptures : blackPawnCaptures) {
			AxialCoordinate cap = pos.add(o[0], o[1]);
			if (!cap.isValid())
				continue;
			Piece target = pieces.get(cap);
			if ((target != null && target.isWhite != p.isWhite) || cap.equals(enPassant))
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
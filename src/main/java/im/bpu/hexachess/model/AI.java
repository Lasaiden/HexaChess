package im.bpu.hexachess.model;

import java.util.Comparator;
import java.util.List;

public class AI {
	private static final int MAX_BETA = Integer.MAX_VALUE;
	private static final int MIN_ALPHA = Integer.MIN_VALUE;
	private int maxDepth = 3;
	// https://youtu.be/l-hh51ncgDI
	private int evaluate(final Board board) {
		int eval = 0;
		for (final Piece piece : board.pieces.values())
			eval += piece.isWhite ? piece.type.value : -piece.type.value;
		return eval;
	}
	private int minimax(
		final Board board, final int depth, int alpha, int beta, final boolean maximizingPlayer) {
		if (depth == 0)
			return -evaluate(board);
		final List<Move> moves = board.listMoves(!maximizingPlayer);
		if (moves.isEmpty())
			return maximizingPlayer ? MIN_ALPHA : MAX_BETA;
		int bestEval = maximizingPlayer ? MIN_ALPHA : MAX_BETA;
		for (final Move move : moves) {
			final Board clone = new Board(board);
			clone.movePiece(move.from, move.to);
			final int eval = minimax(clone, depth - 1, alpha, beta, !maximizingPlayer);
			if (maximizingPlayer) {
				bestEval = Math.max(bestEval, eval);
				alpha = Math.max(alpha, eval);
			} else {
				bestEval = Math.min(bestEval, eval);
				beta = Math.min(beta, eval);
			}
			if (beta <= alpha)
				break;
		}
		return bestEval;
	}
	public Move getBestMove(final Board board) {
		final List<Move> moves = board.listMoves(false);
		if (moves.isEmpty())
			return null;
		return moves.parallelStream()
			.max(Comparator.comparingInt(move -> {
				final Board clone = new Board(board);
				clone.movePiece(move.from, move.to);
				return minimax(clone, maxDepth - 1, MIN_ALPHA, MAX_BETA, false);
			}))
			.orElse(null);
	}
	public void setMaxDepth(final int maxDepth) {
		this.maxDepth = maxDepth;
	}
}
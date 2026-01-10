package im.bpu.hexachess.model;

import java.util.Comparator;
import java.util.List;

public class AI {
	private int maxDepth = 3;
	// https://youtu.be/l-hh51ncgDI
	private int evaluate(Board board) {
		int eval = 0;
		for (Piece piece : board.pieces.values())
			eval += piece.isWhite ? piece.type.value : -piece.type.value;
		return eval;
	}
	private int minimax(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {
		if (depth == 0)
			return -evaluate(board);
		List<Move> moves = board.listMoves(!maximizingPlayer);
		int bestEval = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		for (Move move : moves) {
			Board clone = new Board(board);
			clone.movePiece(move.from, move.to);
			int eval = minimax(clone, depth - 1, alpha, beta, !maximizingPlayer);
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
	public Move getBestMove(Board board) {
		Move bestMove = null;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		bestMove = board.listMoves(false)
					   .parallelStream()
					   .max(Comparator.comparingInt(move -> {
						   Board clone = new Board(board);
						   clone.movePiece(move.from, move.to);
						   return minimax(clone, maxDepth - 1, alpha, beta, false);
					   }))
					   .orElse(null);
		return bestMove;
	}
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
}
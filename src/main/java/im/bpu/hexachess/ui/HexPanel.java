package im.bpu.hexachess.ui;

import im.bpu.hexachess.model.AI;
import im.bpu.hexachess.model.AxialCoordinate;
import im.bpu.hexachess.model.Board;
import im.bpu.hexachess.model.Move;
import im.bpu.hexachess.model.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class HexPanel {
	private Board board;
	private AI ai = new AI();
	private HexGeometry geometry = new HexGeometry(32);
	private HexRenderer renderer;
	private AxialCoordinate selected;
	private List<AxialCoordinate> highlighted = new ArrayList<>();
	private Canvas canvas;
	private Stack<Board> history = new Stack<>();
	public HexPanel(Canvas canvas, Board board) {
		this.board = board;
		this.renderer = new HexRenderer(geometry, board);
		this.canvas = canvas;
		PieceImageLoader.loadImages(this::repaint);
		canvas.setOnMouseClicked(event -> handleMouseClick(event.getX(), event.getY()));
		repaint();
		// accumulate opacity to remove hex gaps
		repaint();
	}
	private void drawBoard(GraphicsContext gc, double cx, double cy) {
		for (int q = -5; q <= 5; q++)
			for (int r = -5; r <= 5; r++) {
				AxialCoordinate coord = new AxialCoordinate(q, r);
				if (coord.isValid())
					renderer.drawHex(gc, cx, cy, coord, selected, highlighted);
			}
	}
	private void repaint() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		double cx = canvas.getWidth() / 2;
		double cy = canvas.getHeight() / 2;
		drawBoard(gc, cx, cy);
		renderer.drawBoardBorder(gc, cx, cy);
	}
	private void deselect() {
		selected = null;
		highlighted.clear();
		repaint();
	}
	private void executeMove(AxialCoordinate target) {
		history.push(new Board(board));
		board.movePiece(selected, target);
		deselect();
		Move bestMove = ai.getBestMove(board);
		if (bestMove != null)
			board.movePiece(bestMove.from, bestMove.to);
		repaint();
	}
	private void selectPiece(AxialCoordinate coord) {
		selected = coord;
		highlighted.clear();
		for (Move m : board.listMoves(board.isWhiteTurn))
			if (m.from.equals(coord))
				highlighted.add(m.to);
		repaint();
	}
	private void handleMouseClick(double x, double y) {
		double cx = canvas.getWidth() / 2;
		double cy = canvas.getHeight() / 2;
		AxialCoordinate clicked = geometry.pixelToHex(x, y, cx, cy);
		if (!clicked.isValid()) {
			deselect();
			return;
		}
		if (selected != null && highlighted.contains(clicked)) {
			executeMove(clicked);
			return;
		}
		Piece p = board.getPiece(clicked);
		if (p != null && p.isWhite == board.isWhiteTurn)
			selectPiece(clicked);
		else
			deselect();
	}
	public void restart() {
		board = new Board();
		history.clear();
		renderer.setBoard(board);
		deselect();
	}
	public void rewind() {
		if (!history.isEmpty()) {
			board = history.pop();
			renderer.setBoard(board);
			deselect();
		}
	}
}
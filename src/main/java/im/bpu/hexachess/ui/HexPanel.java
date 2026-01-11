package im.bpu.hexachess.ui;

import im.bpu.hexachess.SettingsManager;
import im.bpu.hexachess.SoundManager;
import im.bpu.hexachess.State;
import im.bpu.hexachess.model.AI;
import im.bpu.hexachess.model.AxialCoordinate;
import im.bpu.hexachess.model.Board;
import im.bpu.hexachess.model.Move;
import im.bpu.hexachess.model.Piece;
import im.bpu.hexachess.network.API;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import static im.bpu.hexachess.Main.getAspectRatio;

public class HexPanel {
	private static final double ASPECT_RATIO_THRESHOLD = 1.5;
	private static final double DESKTOP_RADIUS = 32;
	private static final double MOBILE_RADIUS = 24;
	private static final long DT = 500;
	private static final long MAX_DT = 6000;
	private static final int BACKOFF_FACTOR = 2;
	private final State state;
	private final AI ai = new AI();
	private final HexGeometry geometry;
	private final HexRenderer renderer;
	private AxialCoordinate selected;
	private final List<AxialCoordinate> highlighted = new ArrayList<>();
	private final Canvas canvas;
	private boolean isLockedIn = false;
	private String lastSyncedMoveString = "";
	public HexPanel(final Canvas canvas, final State state) {
		this.state = state;
		this.ai.setMaxDepth(SettingsManager.maxDepth);
		final double radius;
		if (getAspectRatio() > ASPECT_RATIO_THRESHOLD) {
			radius = DESKTOP_RADIUS;
		} else {
			radius = MOBILE_RADIUS;
		}
		this.geometry = new HexGeometry(radius);
		this.renderer = new HexRenderer(geometry, state.board);
		this.canvas = canvas;
		PieceImageLoader.loadImages(this::repaint);
		canvas.setOnMouseClicked(event -> handleMouseClick(event.getX(), event.getY()));
		if (state.isMultiplayer && state.board.isWhiteTurn != state.isWhitePlayer)
			startPolling();
		repaint();
		// accumulate opacity to remove hex gaps
		repaint();
	}
	private void drawBoard(final GraphicsContext gc, final double cx, final double cy) {
		for (int q = -5; q <= 5; q++)
			for (int r = -5; r <= 5; r++) {
				final AxialCoordinate coord = new AxialCoordinate(q, r);
				if (coord.isValid())
					renderer.drawHex(gc, cx, cy, coord, selected, highlighted);
			}
	}
	public void repaint() {
		final GraphicsContext gc = canvas.getGraphicsContext2D();
		final double cx = canvas.getWidth() / 2;
		final double cy = canvas.getHeight() / 2;
		drawBoard(gc, cx, cy);
		renderer.drawBoardBorder(gc, cx, cy);
	}
	private void deselect() {
		selected = null;
		highlighted.clear();
		repaint();
	}
	private void executeMove(final AxialCoordinate target) {
		if (isLockedIn)
			return;
		final String moveString = selected.q + "," + selected.r + "->" + target.q + "," + target.r;
		state.history.push(new Board(state.board));
		state.board.movePiece(selected, target);
		deselect();
		isLockedIn = true;
		if (state.isMultiplayer) {
			Thread.ofVirtual().start(() -> {
				API.sendMove(state.gameId, moveString);
				lastSyncedMoveString = moveString;
				startPolling();
			});
		} else {
			Thread.ofVirtual().start(() -> {
				final Move bestMove = ai.getBestMove(state.board);
				Platform.runLater(() -> {
					if (bestMove != null)
						state.board.movePiece(bestMove.from, bestMove.to);
					isLockedIn = false;
					repaint();
				});
			});
		}
	}
	private void startPolling() {
		isLockedIn = true;
		Thread.ofVirtual().start(() -> {
			long dt = DT;
			while (true) {
				final String moveString = API.getMove(state.gameId);
				if (moveString != null && !moveString.isEmpty()
					&& !moveString.equals(lastSyncedMoveString)) {
					lastSyncedMoveString = moveString;
					final String[] moveStrings = moveString.split("->");
					final String[] fromString = moveStrings[0].split(",");
					final String[] toString = moveStrings[1].split(",");
					final AxialCoordinate from = new AxialCoordinate(
						Integer.parseInt(fromString[0]), Integer.parseInt(fromString[1]));
					final AxialCoordinate to = new AxialCoordinate(
						Integer.parseInt(toString[0]), Integer.parseInt(toString[1]));
					Platform.runLater(() -> {
						state.board.movePiece(from, to);
						isLockedIn = false;
						repaint();
					});
					break;
				}
				try {
					Thread.sleep(dt);
					dt = Math.min(MAX_DT, dt * BACKOFF_FACTOR);
				} catch (Exception ignored) { // high-frequency polling operation
				}
			}
		});
	}
	private void selectPiece(final AxialCoordinate coord) {
		selected = coord;
		highlighted.clear();
		for (final Move move : state.board.listMoves(state.board.isWhiteTurn))
			if (move.from.equals(coord))
				highlighted.add(move.to);
		repaint();
	}
	private void handleMouseClick(final double x, final double y) {
		if (isLockedIn)
			return;
		final double cx = canvas.getWidth() / 2;
		final double cy = canvas.getHeight() / 2;
		final AxialCoordinate clicked = geometry.pixelToHex(x, y, cx, cy);
		if (!clicked.isValid()) {
			deselect();
			return;
		}
		if (selected != null && highlighted.contains(clicked)) {
			executeMove(clicked);
			return;
		}
		final Piece piece = state.board.getPiece(clicked);
		if ((piece != null && piece.isWhite == state.board.isWhiteTurn)
			&& (!state.isMultiplayer || piece.isWhite == state.isWhitePlayer)) {
			SoundManager.playClick();
			selectPiece(clicked);
		} else
			deselect();
	}
	public void restart() {
		if (isLockedIn || state.isMultiplayer)
			return;
		state.clear();
		ai.setMaxDepth(SettingsManager.maxDepth);
		renderer.setBoard(state.board);
		deselect();
	}
	public void rewind() {
		if (isLockedIn || state.isMultiplayer || state.history.isEmpty())
			return;
		state.board = state.history.pop();
		renderer.setBoard(state.board);
		deselect();
	}
}
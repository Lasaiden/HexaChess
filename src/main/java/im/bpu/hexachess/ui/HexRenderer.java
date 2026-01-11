package im.bpu.hexachess.ui;

import im.bpu.hexachess.model.AxialCoordinate;
import im.bpu.hexachess.model.Board;
import im.bpu.hexachess.model.Piece;

import java.util.List;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

class HexRenderer {
	private static final Color SANDYBROWN = Color.rgb(232, 171, 111);
	private static final Color NAVAJOWHITE = Color.rgb(255, 206, 158);
	private static final Color PERU = Color.rgb(209, 139, 71);
	private static final Color LEGOYELLOW = Color.rgb(255, 215, 0, 0.63);
	private static final Color GREEN = Color.rgb(46, 218, 119, 0.63);
	private static final Color[] HEX_COLORS = {SANDYBROWN, NAVAJOWHITE, PERU};
	private static final int[][] HEX_NEIGHBOR_OFFSETS = {
		{-1, -1}, {0, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 0}};
	private final HexGeometry geometry;
	private Board board;
	HexRenderer(HexGeometry geometry, Board board) {
		this.geometry = geometry;
		this.board = board;
	}
	private void drawPieceImage(GraphicsContext gc, double x, double y, Image image) {
		double size = geometry.getHexSize() * 1.5;
		double offset = size / 2;
		gc.drawImage(image, x - offset, y - offset, size, size);
	}
	private void drawPieceFallback(GraphicsContext gc, double x, double y, Piece piece) {
		double size = geometry.getHexSize();
		double offset = size / 2;
		gc.setFill(piece.isWhite ? Color.WHITE : Color.BLACK);
		gc.fillOval(x - offset, y - offset, size, size);
		gc.setStroke(piece.isWhite ? Color.BLACK : Color.WHITE);
		gc.setLineWidth(2);
		gc.strokeOval(x - offset, y - offset, size, size);
		gc.setFill(piece.isWhite ? Color.BLACK : Color.WHITE);
		gc.setFont(Font.font(size * 0.666));
		String label = String.valueOf(Character.toUpperCase(piece.type.code));
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.fillText(label, x, y - 1);
	}
	private void drawPiece(GraphicsContext gc, double x, double y, Piece piece) {
		Image image = PieceImageLoader.get((piece.isWhite ? "w" : "b") + piece.type.code);
		if (PieceImageLoader.isLoaded())
			drawPieceImage(gc, x, y, image);
		else
			drawPieceFallback(gc, x, y, piece);
	}
	/*
	private void drawCoordinates(GraphicsContext gc, double x, double y, AxialCoordinate coord) {
		double size = geometry.getHexSize();
		gc.setFill(Color.RED);
		gc.setFont(Font.font(size * 0.666));
		String label = coord.q + "," + coord.r;
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.fillText(label, x, y - 1);
	}
	*/
	private void drawPath(GraphicsContext gc, Path path) {
		gc.beginPath();
		for (PathElement elem : path.getElements()) {
			if (elem instanceof MoveTo move)
				gc.moveTo(move.getX(), move.getY());
			else if (elem instanceof LineTo line)
				gc.lineTo(line.getX(), line.getY());
			else if (elem instanceof ClosePath)
				gc.closePath();
		}
	}
	void drawHex(GraphicsContext gc, double cx, double cy, AxialCoordinate coord,
		AxialCoordinate selected, List<AxialCoordinate> highlighted) {
		Point2D center = geometry.hexToPixel(coord.q, coord.r, cx, cy);
		Path hexPath = geometry.createHexPath(center);
		gc.setFill(HEX_COLORS[Math.floorMod(coord.q + coord.r, 3)]);
		drawPath(gc, hexPath);
		gc.fill();
		if (coord.equals(selected)) {
			gc.setFill(LEGOYELLOW);
			gc.fill();
		} else if (highlighted.contains(coord)) {
			gc.setFill(GREEN);
			gc.fill();
		}
		Piece piece = board.getPiece(coord);
		if (piece != null)
			drawPiece(gc, center.getX(), center.getY(), piece);
		// drawCoordinates(gc, center.getX(), center.getY(), coord);
	}
	private void drawCellBorder(GraphicsContext gc, double cx, double cy, int q, int r) {
		AxialCoordinate coord = new AxialCoordinate(q, r);
		if (!coord.isValid())
			return;
		Point2D center = geometry.hexToPixel(q, r, cx, cy);
		double radius = geometry.getHexSize();
		for (int i = 0; i < 6; i++) {
			AxialCoordinate neighbor =
				new AxialCoordinate(q + HEX_NEIGHBOR_OFFSETS[i][0], r + HEX_NEIGHBOR_OFFSETS[i][1]);
			if (neighbor.isValid())
				continue;
			int v1 = (i + 4) % 6;
			int v2 = (i + 5) % 6;
			double x1 = center.getX() + radius * HexGeometry.HEX_COS[v1];
			double y1 = center.getY() + radius * HexGeometry.HEX_SIN[v1];
			double x2 = center.getX() + radius * HexGeometry.HEX_COS[v2];
			double y2 = center.getY() + radius * HexGeometry.HEX_SIN[v2];
			gc.strokeLine(x1, y1, x2, y2);
		}
	}
	void drawBoardBorder(GraphicsContext gc, double cx, double cy) {
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(3);
		for (int q = -5; q <= 5; q++)
			for (int r = -5; r <= 5; r++) drawCellBorder(gc, cx, cy, q, r);
	}
	void setBoard(Board board) {
		this.board = board;
	}
}
package im.bpu.hexachess.ui;

import im.bpu.hexachess.model.AxialCoordinate;

import javafx.geometry.Point2D;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

class HexGeometry {
	static final double[] HEX_COS = new double[6];
	static final double[] HEX_SIN = new double[6];
	private static final double SQRT3 = Math.sqrt(3);
	static {
		for (int i = 0; i < 6; i++) {
			final double angle = Math.toRadians(60 * i);
			HEX_COS[i] = Math.cos(angle);
			HEX_SIN[i] = Math.sin(angle);
		}
	}
	private final double radius;
	HexGeometry(final double radius) {
		this.radius = radius;
	}
	double getHexSize() {
		return radius;
	}
	Point2D hexToPixel(final int q, final int r, final double cx, final double cy) {
		final double width = 2 * radius;
		final double height = SQRT3 * radius;
		final double quarterWidth = width * 3 / 4;
		final double halfHeight = height / 2;
		final double dx = quarterWidth * (q - r);
		final double dy = halfHeight * (q + r);
		return new Point2D(cx + dx, cy + dy);
	}
	private AxialCoordinate hexRound(final double q, final double r) {
		final double s = -q - r;
		int rq = (int) Math.round(q);
		int rr = (int) Math.round(r);
		final int rs = (int) Math.round(s);
		final double dq = Math.abs(rq - q);
		final double dr = Math.abs(rr - r);
		final double ds = Math.abs(rs - s);
		if (dq > dr && dq > ds)
			rq = -rr - rs;
		else if (dr > ds)
			rr = -rq - rs;
		return new AxialCoordinate(rq, rr);
	}
	AxialCoordinate pixelToHex(final double x, final double y, final double cx, final double cy) {
		final double dx = x - cx;
		final double dy = y - cy;
		final double width = 2 * radius;
		final double height = SQRT3 * radius;
		final double quarterWidth = width * 3 / 4;
		final double halfHeight = height / 2;
		final double q = (dy / halfHeight + dx / quarterWidth) / 2;
		final double r = (dy / halfHeight - dx / quarterWidth) / 2;
		return hexRound(q, r);
	}
	Path createHexPath(final Point2D center) {
		final Path path = new Path();
		for (int i = 0; i < 6; i++) {
			final double vx = center.getX() + radius * HEX_COS[i];
			final double vy = center.getY() + radius * HEX_SIN[i];
			if (i == 0)
				path.getElements().add(new MoveTo(vx, vy));
			else
				path.getElements().add(new LineTo(vx, vy));
		}
		path.getElements().add(new ClosePath());
		return path;
	}
}
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
			double angle = Math.toRadians(60 * i);
			HEX_COS[i] = Math.cos(angle);
			HEX_SIN[i] = Math.sin(angle);
		}
	}
	private final double radius;
	HexGeometry(double radius) {
		this.radius = radius;
	}
	double getHexSize() {
		return radius;
	}
	Point2D hexToPixel(int q, int r, double cx, double cy) {
		double width = 2 * radius;
		double height = SQRT3 * radius;
		double quarterWidth = width * 3 / 4;
		double halfHeight = height / 2;
		double dx = quarterWidth * (q - r);
		double dy = halfHeight * (q + r);
		return new Point2D(cx + dx, cy + dy);
	}
	private AxialCoordinate hexRound(double q, double r) {
		double s = -q - r;
		int rq = (int) Math.round(q);
		int rr = (int) Math.round(r);
		int rs = (int) Math.round(s);
		double dq = Math.abs(rq - q);
		double dr = Math.abs(rr - r);
		double ds = Math.abs(rs - s);
		if (dq > dr && dq > ds)
			rq = -rr - rs;
		else if (dr > ds)
			rr = -rq - rs;
		return new AxialCoordinate(rq, rr);
	}
	AxialCoordinate pixelToHex(double x, double y, double cx, double cy) {
		double dx = x - cx;
		double dy = y - cy;
		double width = 2 * radius;
		double height = SQRT3 * radius;
		double quarterWidth = width * 3 / 4;
		double halfHeight = height / 2;
		double q = (dy / halfHeight + dx / quarterWidth) / 2;
		double r = (dy / halfHeight - dx / quarterWidth) / 2;
		return hexRound(q, r);
	}
	Path createHexPath(Point2D center) {
		Path path = new Path();
		for (int i = 0; i < 6; i++) {
			double vx = center.getX() + radius * HEX_COS[i];
			double vy = center.getY() + radius * HEX_SIN[i];
			if (i == 0)
				path.getElements().add(new MoveTo(vx, vy));
			else
				path.getElements().add(new LineTo(vx, vy));
		}
		path.getElements().add(new ClosePath());
		return path;
	}
}
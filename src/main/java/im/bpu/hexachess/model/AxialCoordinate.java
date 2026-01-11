package im.bpu.hexachess.model;

import java.util.Objects;

public class AxialCoordinate {
	private static final int RADIUS = 5;
	public final int q;
	public final int r;
	public AxialCoordinate(final int q, final int r) {
		this.q = q;
		this.r = r;
	}
	public AxialCoordinate add(final int dq, final int dr) {
		return new AxialCoordinate(q + dq, r + dr);
	}
	public boolean isValid() {
		return Math.abs(q) <= RADIUS && Math.abs(r) <= RADIUS && Math.abs(q - r) <= RADIUS;
	}
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof final AxialCoordinate coord))
			return false;
		return q == coord.q && r == coord.r;
	}
	@Override
	public int hashCode() {
		return Objects.hash(q, r);
	}
}
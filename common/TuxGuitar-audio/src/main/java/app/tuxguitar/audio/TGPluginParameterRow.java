package app.tuxguitar.audio;

/**
 * A row of TuxGuitar's parameter editor, which edits a plugin without an editor of its own: one of the plugin's
 * parameters (its id, title, plain value when the editor opened), with a slider over its range (from its plain value at
 * normalized 0 to the one at 1: the first may be the larger) in its steps or, continuous, in FINE positions, and a field
 * for the plain value, taken only inside the range. Along the range the slider moves linearly in plain values.
 */
public final class TGPluginParameterRow {

	// the positions of a continuous parameter's slider
	public static final int FINE = 1000;

	private final int id;
	private final String title;
	private final double plain;
	private final double minimum;
	private final double maximum;
	private final int steps;

	public TGPluginParameterRow(int id, String title, double plain, double minimum, double maximum, int steps) {
		this.id = id;
		this.title = title;
		this.plain = plain;
		this.minimum = minimum;
		this.maximum = maximum;
		this.steps = steps;
	}

	public int getId() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}

	public double getPlain() {
		return this.plain;
	}

	// the slider's last position (its first is 0)
	public int positions() {
		return (this.steps > 0 ? this.steps : FINE);
	}

	// the slider's position of the plain value, inside the slider (a range of one value has one position: 0)
	public int position(double value) {
		long position = Math.round((value - this.minimum) / (this.maximum - this.minimum) * this.positions());
		return (int) Math.max(0, Math.min(this.positions(), position));
	}

	// the plain value at the slider's position
	public double plain(int position) {
		return this.minimum + (this.maximum - this.minimum) * position / this.positions();
	}

	// the plain value typed, or null when it is no number inside the range
	public Double parse(String text) {
		try {
			double value = Double.parseDouble(text.trim());
			return (Math.min(this.minimum, this.maximum) <= value && value <= Math.max(this.minimum, this.maximum) ? Double.valueOf(value) : null);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}

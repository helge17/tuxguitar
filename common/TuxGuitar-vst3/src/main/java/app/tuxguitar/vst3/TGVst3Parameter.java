package app.tuxguitar.vst3;

/**
 * A parameter of an open plugin: its id, title and plain value (in the plugin's own units, as its editor shows it), its
 * plain values at either end of its range (the first may be the larger) and its steps (0: continuous), and whether it
 * chooses the plugin's presets (a program change list) rather than setting one value.
 */
public final class TGVst3Parameter {

	private final int id;
	private final String title;
	private final double plain;
	private final double minimum;
	private final double maximum;
	private final int steps;
	private final boolean presets;

	public TGVst3Parameter(int id, String title, double plain, double minimum, double maximum, int steps, boolean presets) {
		this.id = id;
		this.title = title;
		this.plain = plain;
		this.minimum = minimum;
		this.maximum = maximum;
		this.steps = steps;
		this.presets = presets;
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

	// the plain value at the start of the range
	public double getMinimum() {
		return this.minimum;
	}

	// the plain value at the end of the range
	public double getMaximum() {
		return this.maximum;
	}

	// how many steps the range has (a list of 3 entries has 2), 0 when continuous
	public int getSteps() {
		return this.steps;
	}

	public boolean choosesPresets() {
		return this.presets;
	}
}

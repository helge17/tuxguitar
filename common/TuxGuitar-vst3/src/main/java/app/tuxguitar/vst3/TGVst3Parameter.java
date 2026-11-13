package app.tuxguitar.vst3;

/**
 * A parameter of an open plugin: its id, title and plain value (in the plugin's own units, as its editor shows it), and
 * whether it chooses the plugin's presets (a program change list) rather than setting one value.
 */
public final class TGVst3Parameter {

	private final int id;
	private final String title;
	private final double plain;
	private final boolean presets;

	public TGVst3Parameter(int id, String title, double plain, boolean presets) {
		this.id = id;
		this.title = title;
		this.plain = plain;
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

	public boolean choosesPresets() {
		return this.presets;
	}
}

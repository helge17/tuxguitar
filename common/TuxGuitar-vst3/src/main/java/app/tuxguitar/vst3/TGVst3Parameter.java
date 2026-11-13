package app.tuxguitar.vst3;

/**
 * A parameter of an open plugin: its id, title and plain value (in the plugin's own units, as its editor shows it).
 */
public final class TGVst3Parameter {

	private final int id;
	private final String title;
	private final double plain;

	public TGVst3Parameter(int id, String title, double plain) {
		this.id = id;
		this.title = title;
		this.plain = plain;
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
}

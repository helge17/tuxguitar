package app.tuxguitar.vst3;

/**
 * A VST3 plugin a scan found: the module (bundle) it is in, its class id in that module, its name, and whether it is an
 * instrument (else an effect).
 */
public final class TGVst3Plugin {

	private final String module;
	private final String classId;
	private final String name;
	private final boolean instrument;

	public TGVst3Plugin(String module, String classId, String name, boolean instrument) {
		this.module = module;
		this.classId = classId;
		this.name = name;
		this.instrument = instrument;
	}

	public String getModule() {
		return this.module;
	}

	public String getClassId() {
		return this.classId;
	}

	public String getName() {
		return this.name;
	}

	public boolean isInstrument() {
		return this.instrument;
	}
}

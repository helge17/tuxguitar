package app.tuxguitar.song.helpers.tuning;

import app.tuxguitar.song.models.TGClef;
import app.tuxguitar.song.models.TGTuning;

public class InstrumentTuning {
	
	private TGClef clef;
	private TGTuning tuning;

	public InstrumentTuning(TGClef clef, TGTuning tuning) {
		this.clef = clef;
		this.tuning = tuning;
	}

	public TGClef getClef() {
		return this.clef;
	}

	public TGTuning getTuning() {
		return this.tuning;
	}

}

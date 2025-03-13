package org.herac.tuxguitar.song.models;

public class TGTempoBase {
	private int base;
	private boolean dotted;

	// values of all possible tempo bases
	public static TGTempoBase[] getTempoBases() {
		TGTempoBase[] bases =  {
				new TGTempoBase(TGDuration.WHOLE, false),
				new TGTempoBase(TGDuration.HALF, true),
				new TGTempoBase(TGDuration.HALF, false),
				new TGTempoBase(TGDuration.QUARTER, true),
				new TGTempoBase(TGDuration.QUARTER, false),
				new TGTempoBase(TGDuration.EIGHTH, true),
				new TGTempoBase(TGDuration.EIGHTH, false),
				new TGTempoBase(TGDuration.SIXTEENTH, false)
		};
		return bases;
	}

	private TGTempoBase(int base, boolean dotted) {
		this.base = base;
		this.dotted = dotted;
	}

	public int getBase() {
		return this.base;
	}

	public boolean isDotted() {
		return this.dotted;
	}
}

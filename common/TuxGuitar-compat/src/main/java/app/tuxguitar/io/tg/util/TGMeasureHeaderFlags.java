package app.tuxguitar.io.tg.util;

import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGTimeSignature;

/**
 * Which optional fields a legacy .tg (1.0 - 1.5) measure header stores, as the header byte
 * of the format that owns the bits. The time signature, tempo and triplet feel are stored
 * only when they differ from the previous header; the first header always stores its time
 * signature and tempo, and its triplet feel when there is one.
 */
public class TGMeasureHeaderFlags {

	private final int timeSignature;
	private final int tempo;
	private final int repeatOpen;
	private final int repeatClose;
	private final int repeatAlternative;
	private final int marker;
	private final int tripletFeel;

	public TGMeasureHeaderFlags(int timeSignature, int tempo, int repeatOpen, int repeatClose, int repeatAlternative, int marker, int tripletFeel) {
		this.timeSignature = timeSignature;
		this.tempo = tempo;
		this.repeatOpen = repeatOpen;
		this.repeatClose = repeatClose;
		this.repeatAlternative = repeatAlternative;
		this.marker = marker;
		this.tripletFeel = tripletFeel;
	}

	/** The header byte of measureHeader; previous is null for the first header of the song. */
	public int create(TGMeasureHeader measureHeader, TGMeasureHeader previous) {
		return (this.changes(measureHeader, previous) | this.repeatsAndMarker(measureHeader));
	}

	/**
	 * Fills in what a header read with the given header byte did not store: the time signature
	 * and tempo of previous, and its triplet feel. The first header of a song (previous is null)
	 * keeps its default time signature and tempo, and has no triplet feel.
	 */
	public void inheritUnstored(int header, TGMeasureHeader measureHeader, TGMeasureHeader previous) {
		if( previous != null ) {
			if( (header & this.timeSignature) == 0 ) {
				measureHeader.getTimeSignature().copyFrom(previous.getTimeSignature());
			}
			if( (header & this.tempo) == 0 ) {
				measureHeader.getTempo().copyFrom(previous.getTempo());
			}
		}
		if( (header & this.tripletFeel) == 0 ) {
			measureHeader.setTripletFeel(previous != null ? previous.getTripletFeel() : TGMeasureHeader.TRIPLET_FEEL_NONE);
		}
	}

	private int changes(TGMeasureHeader measureHeader, TGMeasureHeader previous) {
		if( previous == null ) {
			return (this.timeSignature | this.tempo | flag(measureHeader.getTripletFeel() != TGMeasureHeader.TRIPLET_FEEL_NONE, this.tripletFeel));
		}
		return (flag(!isSameTimeSignature(measureHeader.getTimeSignature(), previous.getTimeSignature()), this.timeSignature)
				| flag(measureHeader.getTempo().getQuarterValue() != previous.getTempo().getQuarterValue(), this.tempo)
				| flag(measureHeader.getTripletFeel() != previous.getTripletFeel(), this.tripletFeel));
	}

	private int repeatsAndMarker(TGMeasureHeader measureHeader) {
		return (flag(measureHeader.isRepeatOpen(), this.repeatOpen)
				| flag(measureHeader.getRepeatClose() > 0, this.repeatClose)
				| flag(measureHeader.getRepeatAlternative() > 0, this.repeatAlternative)
				| flag(measureHeader.hasMarker(), this.marker));
	}

	private static boolean isSameTimeSignature(TGTimeSignature timeSignature, TGTimeSignature previous) {
		return (timeSignature.getNumerator() == previous.getNumerator()
				&& timeSignature.getDenominator().getValue() == previous.getDenominator().getValue());
	}

	private static int flag(boolean enabled, int bit) {
		return (enabled ? bit : 0);
	}
}

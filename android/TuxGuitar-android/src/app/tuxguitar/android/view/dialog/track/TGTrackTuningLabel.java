package org.herac.tuxguitar.android.view.dialog.track;

public final class TGTrackTuningLabel {

	public static final String[] KEY_NAMES = new String[] {
		"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"
	};

	public static String valueOf(Integer value) {
		return TGTrackTuningLabel.valueOf(value, false);
	}

	public static String valueOf(Integer value, boolean octave) {
		StringBuilder sb = new StringBuilder();
		if( value != null ) {
			sb.append(KEY_NAMES[value % KEY_NAMES.length]);

			if( octave ) {
				sb.append(value / KEY_NAMES.length);
			}
		}
		return sb.toString();
	}
}

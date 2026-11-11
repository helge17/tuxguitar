package app.tuxguitar.io.tg.util;

import java.util.List;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;

public class TGStringLimitUtil {

	public static final int MINIMUM = 4;
	public static final int MAXIMUM = 7;

	public static List<TGString> createWritableStrings(TGTrack track) {
		return createWritableStrings(track, MINIMUM, MAXIMUM);
	}

	public static List<TGString> createWritableStrings(TGTrack track, int minimum, int maximum) {
		int count = track.stringCount();
		if( isWithin(count, minimum, maximum) ) {
			return track.getStrings();
		}
		List<TGString> writableStrings = createDefaultStrings(track, Math.min(Math.max(count, minimum), maximum));
		for(TGString string : track.getStrings()) {
			if( isWithin(string.getNumber(), minimum, maximum) ) {
				copyValue(string, writableStrings);
			}
		}
		return writableStrings;
	}

	private static boolean isWithin(int value, int minimum, int maximum) {
		return (value >= minimum && value <= maximum);
	}

	/** Copies the value of string to the writable string that has the same number. */
	private static void copyValue(TGString string, List<TGString> writableStrings) {
		for(TGString writableString : writableStrings) {
			if( writableString.getNumber() == string.getNumber() ) {
				writableString.setValue(string.getValue());
			}
		}
	}

	private static List<TGString> createDefaultStrings(TGTrack track, int count) {
		TGSongManager songManager = new TGSongManager();
		if( track.isPercussion() ) {
			return  songManager.createPercussionStrings(count);
		}
		return songManager.createDefaultInstrumentStrings(count);
	}
}

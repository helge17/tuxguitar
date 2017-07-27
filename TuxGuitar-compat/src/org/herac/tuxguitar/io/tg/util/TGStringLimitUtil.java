package org.herac.tuxguitar.io.tg.util;

import java.util.List;

import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;

public class TGStringLimitUtil {
	
	public static final int MINIMUM = 4;
	public static final int MAXIMUM = 7;
	
	public static List<TGString> createWritableStrings(TGTrack track) {
		return createWritableStrings(track, MINIMUM, MAXIMUM);
	}
	
	public static List<TGString> createWritableStrings(TGTrack track, int minimum, int maximum) {
		int count = track.stringCount();
		if( count >= minimum && count <= maximum ) {
			return track.getStrings();
		}
		int writableCount = count;
		if( writableCount < minimum ) {
			writableCount = minimum;
		}
		if( writableCount > maximum ) {
			writableCount = maximum;
		}
		List<TGString> strings = track.getStrings();
		List<TGString> writableStrings = createDefaultStrings(track, writableCount);
		for(TGString string : strings) {
			if( string.getNumber() >= minimum && string.getNumber() <= maximum ) {
				for(TGString writableString : writableStrings) {
					if( writableString.getNumber() == string.getNumber() ) {
						writableString.setValue(string.getValue());
					}
				}
			}
		}
		return writableStrings;
	}
	
	private static List<TGString> createDefaultStrings(TGTrack track, int count) {
		TGSongManager songManager = new TGSongManager();
		if( songManager.isPercussionChannel(track.getSong(), track.getChannelId()) ) {
			return  songManager.createPercussionStrings(count);
		}
		return songManager.createDefaultInstrumentStrings(count);
	}
}

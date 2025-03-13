package org.herac.tuxguitar.io.musicxml;

import java.util.ArrayList;
import java.util.Arrays;

import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * MusicXMLLyricWriter is used to convert TGLyric to multiple MusicXMLMeasureLyric chunks.
 * @author Menno Vossen
 */
public class MusicXMLLyricWriter {
	/**
	 * Syllabic enum.
	 * See the MusicXML specification: <a href="https://www.w3.org/2021/06/musicxml40/musicxml-reference/elements/syllabic/">syllabic</a>
	 */
	public enum Syllabic {
		NONE,
		SINGLE,
		BEGIN,
		MIDDLE,
		END;

		@Override
		public String toString() {
			switch(this.ordinal()) {
			case 0:
				return "none"; // NOTE: not a official syllabic but used in state machine.
			case 1:
				return "single";
			case 2:
				return "begin";
			case 3:
				return "middle";
			case 4:
				return "end";
			default:
				return "single";
			}
		}
	}
	/**
	 * MusicXMLMeasureLyric contains the syllabic and text for one note.
	 * See the MusicXML specification:
	 * <a href="https://www.w3.org/2021/06/musicxml40/musicxml-reference/elements/lyric/">lyric</a>
	 */
	public class MusicXMLMeasureLyric {
		public Syllabic syllabic;
		public String text;

		/**
		 * Class constructor.
		 * @param syllabic
		 * @param text
		 */
		public MusicXMLMeasureLyric(Syllabic syllabic, String text) {
			this.syllabic = syllabic;
			this.text = text;
		}
	}
	/**
	 * Helper function.
	 * @param string
	 * @return
	 */
	private static String removeLastChar(String string) {
	    return (string == null || string.length() == 0)
	    		? null : (string.substring(0, string.length() - 1));
	}

	/**
	 * Helper function.
	 * The lyrics in TGLyric are for one track. The lyricStrings are lyric per note.
	 * @param lyricStrings		string array source
	 * @param start				start index for the slice
	 * @param number			number of strings to get
	 *
	 * @return 	The string Array is used for one measure.
	 */
	private static String[] getLyricSlice(String[] lyricStrings, int start, int number) {
		try {
			return Arrays.stream(lyricStrings, start, start+number).toArray(String[]::new);
		} catch (ArrayIndexOutOfBoundsException exception) {
			// ignore
			// more lyrics than notes!?
		}

		// we need to return a default
		String[] empty = {""};
		return empty;
	}

	/**
	 * Holds the lyrics for the complete track.
	 */
	private String[] lyrics;
	/**
	 * Used to keep track off our position in the lyrics array.
	 */
	private int lyricIndex;
	/**
	 * Used to store the start of lyrics offset in Measures.
	 */
	private int lyricFrom;
	/**
	 * Used in the generateLyricList method, which is a state machine.
	 */
	private Syllabic syllabicState;


	/**
	 * MusicXMLLyricWriter Constructor.
	 * @param track 	Holds track currently being processed.
	 */
	public MusicXMLLyricWriter(TGTrack track) {
		lyricFrom = track.getLyrics().getFrom();
		lyrics = track.getLyrics().getLyricBeats();
		lyricIndex = 0; // in lyrics (Strings)
		syllabicState = Syllabic.NONE;
	}
	/**
	 * State machine, use only sequential calls to get lyrics from track.
	 * @param measure
	 * @return returns an Array containing the text and syllabics for the passed in measure.
	 */
	public MusicXMLMeasureLyric[] generateLyricList(TGMeasure measure) {

		if (measure.getNumber() < lyricFrom) {
			return null;
		}

		// number of not empty beats
		int number = 0;
		for (TGBeat beat : measure.getBeats()) {
			if (!beat.isRestBeat()) {
				number++;
			}
		}

		ArrayList<MusicXMLMeasureLyric> measureLyrics = new ArrayList<MusicXMLMeasureLyric>();


		String[] measureLyricStrings = getLyricSlice(lyrics, lyricIndex, number);
		this.lyricIndex += number;

		Syllabic syllabic = Syllabic.NONE;

		// Turn strings in MusicXMLMeasureLyric.
		for (int i = 0; i < measureLyricStrings.length; i++) {
			switch (this.syllabicState) {
				case NONE:
					if (measureLyricStrings[i].endsWith("-")) {
						measureLyricStrings[i] = removeLastChar(measureLyricStrings[i]);
						syllabic = Syllabic.BEGIN;
						syllabicState = Syllabic.BEGIN; // new state
					} else {
						syllabic = Syllabic.SINGLE;
						syllabicState = Syllabic.NONE; // new state
					}
					break;
				case SINGLE:
					syllabic = Syllabic.END;
					syllabicState = Syllabic.NONE;
					break;
				case BEGIN:
					if (measureLyricStrings[i].endsWith("-")) {
						measureLyricStrings[i] = removeLastChar(measureLyricStrings[i]);
						syllabic = Syllabic.MIDDLE;
						syllabicState = Syllabic.MIDDLE;
					} else {
						syllabic = Syllabic.END;
						syllabicState = Syllabic.NONE;
					}
					break;
				case MIDDLE:
					if (measureLyricStrings[i].endsWith("-")) {
						measureLyricStrings[i] = removeLastChar(measureLyricStrings[i]);
						syllabic = Syllabic.MIDDLE;
						syllabicState = Syllabic.MIDDLE;
					} else {
						syllabic = Syllabic.END;
						syllabicState = Syllabic.NONE;
					}
					break;
				case END:
					syllabic = Syllabic.NONE;
					syllabicState = Syllabic.NONE;
					break;
			}

			measureLyrics.add(new MusicXMLMeasureLyric(syllabic, measureLyricStrings[i]));
		}

		MusicXMLMeasureLyric[] measureLyricArray = new MusicXMLMeasureLyric[measureLyrics.size()];
		measureLyrics.toArray(measureLyricArray);
		return 	measureLyricArray;
	}
}

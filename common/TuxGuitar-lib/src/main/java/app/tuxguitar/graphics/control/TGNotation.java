package app.tuxguitar.graphics.control;

import app.tuxguitar.util.TGMusicKeyUtils;

public class TGNotation {

	// vertical position of a note in the score
	// convention: 0 = between the 2 highest lines
	public static int computePosition(TGLayout layout, TGNoteImpl note) {
		int noteValue = layout.getSongManager().getMeasureManager().getRealNoteValue(note);

		if (note.getMeasureImpl().getTrack().isPercussion()) {
			return layout.getDrumMap().getPosition(noteValue);
		}
		int clefFirstLineNoteIndex = note.getMeasureImpl().getClef().getFirstLineNoteIndex();
		int clefFirstLineNoteOctave = note.getMeasureImpl().getClef().getFirstLineNoteOctave();
		int clefPosition = clefFirstLineNoteIndex + 7*clefFirstLineNoteOctave;
		int keySignature = note.getMeasureImpl().getKeySignature();
		int noteIndex = TGMusicKeyUtils.noteIndex(noteValue, keySignature, note.isAltEnharmonic());
		int noteOctave = TGMusicKeyUtils.noteOctave(noteValue, keySignature, note.isAltEnharmonic());
		return (clefPosition - noteIndex - 7*noteOctave - 1);
	}
}

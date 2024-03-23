package org.herac.tuxguitar.graphics.control;

import org.herac.tuxguitar.util.TGMusicKeyUtils;

public class TGNotation {
	
	// TODO: move these clef-specific attributes in a dedicated class representing Clef object
	private static final int SCORE_CLEF_OFFSETS[] = new int[]{30, 18, 22, 24};
	
	public static int computePosition(TGLayout layout, TGNoteImpl note) {
		int noteValue = layout.getSongManager().getMeasureManager().getRealNoteValue(note);
		
		if (note.getMeasureImpl().getTrack().isPercussion()) {
			return layout.getDrumMap().getPosition(noteValue);
		}
		int clefPosition = SCORE_CLEF_OFFSETS[note.getMeasureImpl().getClef() - 1];
		int keySignature = note.getMeasureImpl().getKeySignature();
		int noteIndex = TGMusicKeyUtils.noteIndex(noteValue, keySignature);
		int noteOctave = TGMusicKeyUtils.noteOctave(noteValue, keySignature);
		return (clefPosition - noteIndex - 7*noteOctave);
	}
}

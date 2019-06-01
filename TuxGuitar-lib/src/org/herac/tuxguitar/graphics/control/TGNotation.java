package org.herac.tuxguitar.graphics.control;

public class TGNotation {
	
	private static final int SCORE_CLEF_OFFSETS[] = new int[]{30, 18, 22, 24};
	private static final int SCORE_SHARP_POSITIONS[] = new int[]{7,7,6,6,5,4,4,3,3,2,2,1};
	private static final int SCORE_FLAT_POSITIONS[] = new int[]{7,6,6,5,5,4,3,3,2,2,1,1};
	
	public static int computePosition(TGLayout layout, TGNoteImpl note) {
		int noteValue = note.getRealValue();
		
		if (layout.getSongManager().isPercussionChannel(note.getMeasureImpl().getTrack().getSong(), note.getMeasureImpl().getTrack().getChannelId())) {
			return layout.getDrumMap().getPosition(noteValue);
		}
		
		int clefPosition = SCORE_CLEF_OFFSETS[note.getMeasureImpl().getClef() - 1];
		int[] positions = (note.getMeasureImpl().getKeySignature() <= 7 ? SCORE_SHARP_POSITIONS : SCORE_FLAT_POSITIONS);
		
		return (clefPosition + positions[noteValue % 12] - ((int) (7 * (noteValue / 12))));
	}
}

package org.herac.tuxguitar.app.editors.chord;

import java.util.List;

import org.herac.tuxguitar.song.models.TGChord;

public interface ChordCreatorListener {
	
	public void notifyChords(ChordCreatorUtil process, List<TGChord> chords);
	
}

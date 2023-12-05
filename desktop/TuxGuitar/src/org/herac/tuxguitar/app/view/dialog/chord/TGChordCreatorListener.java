package org.herac.tuxguitar.app.view.dialog.chord;

import java.util.List;

import org.herac.tuxguitar.song.models.TGChord;

public interface TGChordCreatorListener {
	
	public void notifyChords(TGChordCreatorUtil process, List<TGChord> chords);
	
}

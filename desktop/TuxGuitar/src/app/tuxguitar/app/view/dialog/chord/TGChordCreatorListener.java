package app.tuxguitar.app.view.dialog.chord;

import java.util.List;

import app.tuxguitar.song.models.TGChord;

public interface TGChordCreatorListener {

	public void notifyChords(TGChordCreatorUtil process, List<TGChord> chords);

}

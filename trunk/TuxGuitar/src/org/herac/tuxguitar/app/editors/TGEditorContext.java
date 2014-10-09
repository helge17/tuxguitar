package org.herac.tuxguitar.app.editors;

import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.song.managers.TGSongManager;

public class TGEditorContext {
	
	private TGSongManager songManager;
	
	public TGEditorContext() {
		this.songManager = new TGSongManager();
		this.songManager = new TGSongManager(new TGFactoryImpl());
		this.songManager.setSong(this.songManager.newSong());
	}

	public TGSongManager getSongManager() {
		return songManager;
	}
}

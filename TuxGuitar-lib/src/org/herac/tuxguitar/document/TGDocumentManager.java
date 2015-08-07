package org.herac.tuxguitar.document;

import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGDocumentManager {
	
	private TGSongManager songManager;
	private TGSong song;
	
	private TGDocumentManager() {
		this.songManager = new TGSongManager(new TGFactoryImpl());
		this.song = this.songManager.newSong();
	}
	
	public TGSongManager getSongManager() {
		return songManager;
	}

	public TGSong getSong() {
		return song;
	}
	
	public void setSong(TGSong song) {
		if( song != null ){
			this.song = song;
		}
	}

	public static TGDocumentManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGDocumentManager.class.getName(), new TGSingletonFactory<TGDocumentManager>() {
			public TGDocumentManager createInstance(TGContext context) {
				return new TGDocumentManager();
			}
		});
	}
}

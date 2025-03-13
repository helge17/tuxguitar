package app.tuxguitar.document;

import app.tuxguitar.graphics.control.TGFactoryImpl;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

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
			this.songManager.autoCompleteSilences(this.song);
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

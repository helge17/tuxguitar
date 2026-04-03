package app.tuxguitar.editor.clipboard;

import app.tuxguitar.song.helpers.TGStoredBeatList;
import app.tuxguitar.song.helpers.TGSongSegment;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGClipboard {

	private TGSongSegment segment;		// if measures are copied
	private TGStoredBeatList beats;		// if selection is copied

	private TGClipboard() {
		super();
	}

	public TGSongSegment getSegment() {
		return segment;
	}

	public TGStoredBeatList getBeats() {
		return beats;
	}

	public boolean hasContents() {
		return this.segment != null || this.beats != null;
	}

	public void setData(TGSongSegment data) {
		this.segment = data;
		this.beats = null;
	}

	public void setData(TGStoredBeatList beats) {
		this.beats = beats;
		this.segment = null;
	}

	public static TGClipboard getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGClipboard.class.getName(), new TGSingletonFactory<TGClipboard>() {
			public TGClipboard createInstance(TGContext context) {
				return new TGClipboard();
			}
		});
	}
}

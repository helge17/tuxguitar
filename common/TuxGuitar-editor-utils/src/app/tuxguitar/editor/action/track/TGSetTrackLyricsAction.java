package app.tuxguitar.editor.action.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGLyric;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGSetTrackLyricsAction extends TGActionBase {

	public static final String NAME = "action.track.set-lyric";

	public TGSetTrackLyricsAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGLyric lyric = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_LYRIC);
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);

		track.getLyrics().copyFrom(lyric);
	}
}

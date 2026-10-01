package app.tuxguitar.editor.action.file;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGSetBackingTrackAction extends TGActionBase {

	public static final String NAME = "action.file.set-backing-track";

	public static final String ATTRIBUTE_PATH = "backing-track.path";
	public static final String ATTRIBUTE_REMOVE = "backing-track.remove";
	public static final String ATTRIBUTE_VOLUME = "backing-track.volume";

	public TGSetBackingTrackAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGSong song = (TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		if( song == null ) {
			return;
		}
		if( Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_REMOVE)) ) {
			song.setBackingTrack(null);
		} else {
			String path = (String) context.getAttribute(ATTRIBUTE_PATH);
			if( path != null && !path.trim().isEmpty() ) {
				song.setBackingTrack(path);
			}
		}
		Object volumeAttribute = context.getAttribute(ATTRIBUTE_VOLUME);
		if( volumeAttribute instanceof Float ) {
			song.setBackingTrackVolume(((Float) volumeAttribute).floatValue());
		}
	}
}

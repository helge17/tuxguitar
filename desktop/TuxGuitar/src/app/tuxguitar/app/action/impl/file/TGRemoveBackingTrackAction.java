package app.tuxguitar.app.action.impl.file;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.file.TGSetBackingTrackAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGRemoveBackingTrackAction extends TGActionBase {

	public static final String NAME = "action.file.remove-backing-track";

	public TGRemoveBackingTrackAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext actionContext) {
		TGSong song = TGDocumentManager.getInstance(getContext()).getSong();
		if( song == null || song.getBackingTrack() == null ) {
			return;
		}
		TGActionProcessor tgActionProcessor = new TGActionProcessor(getContext(), TGSetBackingTrackAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGSetBackingTrackAction.ATTRIBUTE_REMOVE, Boolean.TRUE);
		tgActionProcessor.processOnNewThread();
	}
}

package app.tuxguitar.android.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionContextFactory;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGActionContextFactoryImpl implements TGActionContextFactory{

	private TGContext context;

	public TGActionContextFactoryImpl(TGContext context) {
		this.context = context;
	}

	public TGActionContext createActionContext() throws TGActionException {
		TGActionContext tgActionContext = new TGActionContextImpl();

		TGDocumentManager tgDocumentManager = TGDocumentManager.getInstance(this.context);
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, tgDocumentManager.getSongManager());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, tgDocumentManager.getSong());

		TGSongViewController tgSongView = TGSongViewController.getInstance(this.context);
		if( tgSongView != null ) {
			TGCaret tgCaret = tgSongView.getCaret();
			TGNote selectedNote = tgCaret.getSelectedNote();
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, tgCaret.getTrack());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, tgCaret.getMeasure());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, tgCaret.getMeasure().getHeader());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, tgCaret.getSelectedBeat());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, tgCaret.getSelectedVoice());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, selectedNote);
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, tgCaret.getSelectedString());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION, tgCaret.getDuration());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, tgCaret.getVelocity());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_POSITION, tgCaret.getPosition());
			if (selectedNote == null) {
				tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE, TGNoteRange.empty());
			} else {
				tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE, TGNoteRange.single(selectedNote));
			}
		}
		return tgActionContext;
	}
}

package org.herac.tuxguitar.android.action;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionContextFactory;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.android.view.tablature.TGCaret;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.util.TGContext;

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
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, tgCaret.getTrack());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, tgCaret.getMeasure());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, tgCaret.getMeasure().getHeader());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, tgCaret.getSelectedBeat());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, tgCaret.getSelectedBeat().getVoice(tgCaret.getVoice()));
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, tgCaret.getSelectedNote());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, tgCaret.getSelectedString());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION, tgCaret.getDuration());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, tgCaret.getVelocity());
			tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_POSITION, tgCaret.getPosition());
		}
		return tgActionContext;
	}
}

package org.herac.tuxguitar.app.action;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionContextFactory;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.document.TGDocumentManager;

public class TGActionContextFactoryImpl implements TGActionContextFactory{

	public TGActionContext createActionContext() throws TGActionException {
		Caret caret = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
		TGDocumentManager tgDocumentManager = TuxGuitar.instance().getDocumentManager();
		
		TGActionContext tgActionContext = new TGActionContextImpl();
		tgActionContext.setAttribute(TGDocumentManager.class.getName(), tgDocumentManager);
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, tgDocumentManager.getSongManager());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, tgDocumentManager.getSong());
		
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, caret.getTrack());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, caret.getMeasure());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, caret.getMeasure().getHeader());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, caret.getSelectedBeat());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, caret.getSelectedBeat().getVoice(caret.getVoice()));
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, caret.getSelectedNote());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, caret.getSelectedString());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION, caret.getDuration());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, caret.getVelocity());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_POSITION, caret.getPosition());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, caret.getMeasure().getHeader().getMarker());
		
		return tgActionContext;
	}
}

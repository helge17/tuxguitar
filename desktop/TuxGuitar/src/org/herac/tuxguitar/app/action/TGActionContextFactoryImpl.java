package org.herac.tuxguitar.app.action;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionContextFactory;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.document.TGDocumentManager;

public class TGActionContextFactoryImpl implements TGActionContextFactory{

	public TGActionContext createActionContext() throws TGActionException {
		Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
		Caret caret = tablature.getCaret();
		TGDocumentManager tgDocumentManager = TuxGuitar.getInstance().getDocumentManager();

		TGActionContext tgActionContext = new TGActionContextImpl();
		tgActionContext.setAttribute(TGDocumentManager.class.getName(), tgDocumentManager);
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, tgDocumentManager.getSongManager());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, tgDocumentManager.getSong());

		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, caret.getTrack());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, caret.getMeasure());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, caret.getMeasure().getHeader());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, caret.getSelectedBeat());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, caret.getSelectedVoice());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, caret.getSelectedNote());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, caret.getSelectedString());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION, caret.getDuration());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, caret.getVelocity());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_POSITION, caret.getPosition());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, caret.getMeasure().getHeader().getMarker());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE, tablature.getCurrentBeatRange());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE, tablature.getCurrentNoteRange());
		tgActionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SELECTION_IS_ACTIVE, (Boolean)tablature.getSelector().isActive());

		return tgActionContext;
	}
}

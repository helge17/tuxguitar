package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGContext;

public class TGChangeNoteAction extends TGActionBase {

	public static final String NAME = "action.note.general.change";

	public TGChangeNoteAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		Long start = (Long) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_POSITION);
		Integer fret = (Integer) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET);
		Integer velocity = (Integer) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY);
		TGDuration duration = (TGDuration) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION);
		TGVoice voice = ((TGVoice) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE));
		TGMeasure measure = (TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		TGBeat beat = (TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		TGString string = (TGString) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
		TGSongManager songManager = getSongManager(context);
		boolean freeEditionMode = songManager.isFreeEditionMode(measure);
		
		TGNote note = songManager.getFactory().newNote();
		note.setValue(fret);
		note.setVelocity(velocity);
		note.setString(string.getNumber());
		
		if (beat != null && freeEditionMode) {
			songManager.getMeasureManager().addNoteWithoutControl(beat, note, duration.clone(songManager.getFactory()), voice.getIndex());
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
		else if( measure != null && fret >= 0 ) {
			songManager.getMeasureManager().addNote(measure, start, note, duration.clone(songManager.getFactory()), voice.getIndex());
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}
	
}
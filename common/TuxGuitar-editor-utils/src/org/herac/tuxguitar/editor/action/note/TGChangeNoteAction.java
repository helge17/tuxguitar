package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
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
		TGString string = (TGString) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
		TGSongManager songManager = getSongManager(context);
		
		if( measure != null && fret >= 0 ) {
			this.addNote(songManager, measure, voice, duration, string, start, fret, velocity);
			
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}
	
	private void addNote(TGSongManager songManager, TGMeasure measure, TGVoice voice, TGDuration duration, TGString string, long start, int value, int velocity) {
		TGNote note = songManager.getFactory().newNote();
		note.setValue(value);
		note.setVelocity(velocity);
		note.setString(string.getNumber());
		
		songManager.getMeasureManager().addNote(measure, start, note, duration.clone(songManager.getFactory()), voice.getIndex());
	}
}
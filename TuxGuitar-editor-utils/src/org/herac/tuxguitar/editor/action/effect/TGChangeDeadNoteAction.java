package org.herac.tuxguitar.editor.action.effect;

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

public class TGChangeDeadNoteAction extends TGActionBase {
	
	public static final String NAME = "action.note.effect.change-dead";
	
	public TGChangeDeadNoteAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
		TGVoice voice = ((TGVoice) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE));
		TGNote note = ((TGNote) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE));
		TGDuration duration = ((TGDuration) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION));
		TGString string = ((TGString) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING));
		int velocity = ((Integer) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY)).intValue();
		
		if( note == null ){
			note = songManager.getFactory().newNote();
			note.setValue(0);
			note.setVelocity(velocity);
			note.setString(string.getNumber());
			
			TGDuration noteDuration = duration.clone(songManager.getFactory());
						
			songManager.getMeasureManager().addNote(measure, beat.getStart(), note, noteDuration, voice.getIndex());
		}
		songManager.getMeasureManager().changeDeadNote(note);
	}
}

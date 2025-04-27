package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

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
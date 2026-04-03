package app.tuxguitar.editor.action.note;

import java.util.Iterator;

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

public class TGChangeTiedNoteAction extends TGActionBase {

	public static final String NAME = "action.note.duration.tied";

	public TGChangeTiedNoteAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGNote note = ((TGNote) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE));
		TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
		TGVoice voice = ((TGVoice) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE));
		TGDuration duration = ((TGDuration) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION));
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGString string = ((TGString) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING));
		Integer velocity = ((Integer) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY));
		TGNote previousNoteForTie = null;

		if( note != null ){
			songManager.getMeasureManager().changeTieNote(note);
			previousNoteForTie = songManager.getTrackManager().getPreviousNoteForTie(note);
		} else {
			// try to create a new note
			boolean isValid = songManager.isFreeEditionMode(measure);
			if (!isValid) {
				// is there a note to be tied to?
				isValid = (songManager.getTrackManager().getPreviousNoteForTie(voice, string.getNumber(), null) != null);
			}
			if (isValid) {
				note = songManager.getFactory().newNote();
				note.setValue(0);
				note.setVelocity(velocity);
				note.setString(string.getNumber());
				note.setTiedNote(true);
	
				TGDuration noteDuration = songManager.getFactory().newDuration();
				noteDuration.copyFrom(duration);
	
				previousNoteForTie = setTiedNoteValue(songManager, measure, beat, voice, note);
	
				songManager.getMeasureManager().addNote(beat, note, noteDuration, voice.getIndex());
			}
		}
		if ((note != null) && !songManager.isFreeEditionMode(note.getVoice().getBeat().getMeasure()) && note.isTiedNote()
				&& (previousNoteForTie != null) && previousNoteForTie.getEffect().isHarmonic()) {
			note.getEffect().setHarmonic(previousNoteForTie.getEffect().getHarmonic().clone(songManager.getFactory()));
		}
	}

	private TGNote setTiedNoteValue(TGSongManager songManager, TGMeasure measure, TGBeat beat, TGVoice voice, TGNote note){
		TGVoice previousVoice = songManager.getMeasureManager().getPreviousVoice( measure.getBeats(), beat, voice.getIndex());
		while( measure != null){
			while( previousVoice != null ){
				if( previousVoice.isRestVoice() ){
					note.setValue(0);
					return null;
				}
				// Check if is there any note at same string.
				Iterator<?> it = previousVoice.getNotes().iterator();
				while( it.hasNext() ){
					TGNote current = (TGNote) it.next();
					if( current.getString() == note.getString() ){
						note.setValue( current.getValue() );
						return current;
					}
				}
				previousVoice = songManager.getMeasureManager().getPreviousVoice( measure.getBeats(), previousVoice.getBeat(), voice.getIndex());
			}
			measure = songManager.getTrackManager().getPrevMeasure(measure);
			if( measure != null ){
				previousVoice = songManager.getMeasureManager().getLastVoice( measure.getBeats() , voice.getIndex());
			}
		}
		return null;
	}
}

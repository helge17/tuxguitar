package app.tuxguitar.editor.action.effect;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGChangeDeadNoteAction extends TGActionBase {

	public static final String NAME = "action.note.effect.change-dead";

	public TGChangeDeadNoteAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGNote note = ((TGNote) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE));
		TGNoteRange noteRange = (TGNoteRange) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);

		if ((noteRange!=null) && !noteRange.isEmpty() && !track.isPercussion()) {
			boolean newValue = true;
			if (noteRange.getNotes().stream().allMatch(n -> n.getEffect().isDeadNote())) {
				newValue = false;
			}
			for (TGNote n : noteRange.getNotes()) {
				songManager.getMeasureManager().setDeadNote(n, newValue);
			}
		}
		else if( note == null && !track.isPercussion()){
			TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
			TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
			TGVoice voice = ((TGVoice) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE));
			TGDuration duration = ((TGDuration) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION));
			TGString string = ((TGString) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING));
			int velocity = ((Integer) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY)).intValue();

			note = songManager.getFactory().newNote();
			note.setValue(0);
			note.setVelocity(velocity);
			note.setString(string.getNumber());

			TGDuration noteDuration = duration.clone(songManager.getFactory());

			songManager.getMeasureManager().addNote(measure, beat.getStart(), note, noteDuration, voice.getIndex());
			songManager.getMeasureManager().changeDeadNote(note);
		}
	}
}

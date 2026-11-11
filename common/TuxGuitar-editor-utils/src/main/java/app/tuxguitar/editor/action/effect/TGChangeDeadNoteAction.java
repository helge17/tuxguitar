package app.tuxguitar.editor.action.effect;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

public class TGChangeDeadNoteAction extends TGToggleNoteEffectAction {

	public static final String NAME = "action.note.effect.change-dead";

	public TGChangeDeadNoteAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean hasEffect(TGNoteEffect effect) {
		return effect.isDeadNote();
	}

	protected void setEffect(TGMeasureManager measureManager, TGNote note, boolean value) {
		measureManager.setDeadNote(note, value);
	}

	protected void processAction(TGActionContext context){
		if (!toggleSelection(context)) {
			addDeadNoteAtCaret(context);
		}
	}

	// without a selection and without a note under the caret, a dead note of fret 0 goes on the caret string
	private void addDeadNoteAtCaret(TGActionContext context) {
		TGNote note = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		if (note != null || !appliesTo(track)) {
			return;
		}
		TGSongManager songManager = getSongManager(context);
		TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
		TGVoice voice = ((TGVoice) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE));
		TGString string = ((TGString) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING));
		int velocity = ((Integer) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY)).intValue();

		note = songManager.getFactory().newNote();
		note.setValue(0);
		note.setVelocity(velocity);
		note.setString(string.getNumber());

		songManager.getMeasureManager().addNote(beat, note, voice.getIndex());
		songManager.getMeasureManager().changeDeadNote(note);
	}
}

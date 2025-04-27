package app.tuxguitar.editor.action.note;

import java.util.Iterator;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

public class TGInsertChordAction extends TGActionBase {

	public static final String NAME = "action.beat.general.insert-chord";

	public TGInsertChordAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		TGVoice voice = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
		TGChord chord = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHORD);
		Integer velocity = (Integer) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY);

		boolean restBeat = beat.isRestBeat();
		if(!restBeat || chord.countNotes() > 0 ) {
			// Replace voice notes with chord notes in the tablature
			voice.clearNotes();
			Iterator<TGString> it = track.getStrings().iterator();
			while (it.hasNext()) {
				TGString string = (TGString) it.next();

				int value = chord.getFretValue(string.getNumber() - 1);
				if (value >= 0) {
					TGNote note = songManager.getFactory().newNote();
					note.setValue(value);
					note.setVelocity(velocity);
					note.setString(string.getNumber());

					TGDuration duration = songManager.getFactory().newDuration();
					duration.copyFrom(voice.getDuration());

					songManager.getMeasureManager().addNote(beat, note, duration, voice.getIndex());
				}
			}

			songManager.getMeasureManager().addChord(beat, chord);
		}
	}
}

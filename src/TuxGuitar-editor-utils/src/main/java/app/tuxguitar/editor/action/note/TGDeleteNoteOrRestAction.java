package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGDeleteNoteOrRestAction extends TGActionBase {

	public static final String NAME = "action.beat.general.delete-note-or-rest";

	public TGDeleteNoteOrRestAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGBeatRange beats = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		TGBeat selectedBeat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);

		if (noteRange!=null && !noteRange.isEmpty()) {
			for (TGNote note : noteRange.getNotes()) {
				TGVoice voice = note.getVoice();
				TGBeat beat = voice.getBeat();
				TGMeasure measure = beat.getMeasure();
				removeNote(context, measure, beat, voice, note.getString());
			}
		}
		else if (beats!=null && !beats.isEmpty()) {
			TGVoice voice = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
			TGString string = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
			for (TGBeat beat : beats.getBeats()) {
				removeNote(context, beat.getMeasure(), beat, voice, string.getNumber());
			}
		}
		else if (selectedBeat!=null) {
			TGVoice voice = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
			TGString string = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
			removeNote(context, selectedBeat.getMeasure(), selectedBeat, voice, string.getNumber());
		}
	}
	private void removeNote(TGActionContext context, TGMeasure measure, TGBeat beat, TGVoice voice, int string) {
		TGSongManager songManager = getSongManager(context);
		TGMeasureManager measureManager = songManager.getMeasureManager();
		if (beat.isTextBeat() && beat.isRestBeat()) {
			measureManager.removeText(beat);
		} else if (voice.isRestVoice()) {
			// in free edition mode, if this is the last beat and all voices are rest, delete the full beat
			// to ease fixing invalid measures
			if (songManager.isFreeEditionMode(measure) && beat.isRestBeat() &&
					(measureManager.getLastBeat(measure.getBeats()) == beat)) {
				measureManager.removeBeat(beat);
				}
				else {
					measureManager.removeVoice(voice, true);
				}
		} else {
			measureManager.removeNote(measure, beat.getStart(), voice.getIndex(), string);
		}
	}

}

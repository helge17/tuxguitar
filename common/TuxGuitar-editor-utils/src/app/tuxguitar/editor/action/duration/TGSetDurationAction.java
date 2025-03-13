package app.tuxguitar.editor.action.duration;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGSetDurationAction extends TGActionBase {

	public static final String NAME = "action.note.duration.set-duration";

	public TGSetDurationAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = (TGNoteRange)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGBeatRange beats = (TGBeatRange)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		TGDuration duration = (TGDuration)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION);

		TGSongManager songManager = getSongManager(context);
		TGMeasureManager measureManager = songManager.getMeasureManager();
		TGFactory factory = songManager.getFactory();

		if (noteRange!=null && !noteRange.isEmpty()) {
			for (TGNote note : noteRange.getNotes()) {
				TGVoice voice = note.getVoice();
				TGBeat beat = voice.getBeat();
				TGMeasure measure = beat.getMeasure();
				measureManager.changeDuration(measure, beat, duration.clone(factory), voice.getIndex(), true);
			}
		} else if (beats!=null && !beats.isEmpty()){
			TGVoice voice = (TGVoice)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
			for (TGBeat beat : beats.getBeats()) {
				TGMeasure measure = beat.getMeasure();
				measureManager.changeDuration(measure, beat, duration.clone(factory), voice.getIndex(), true);
			}
		}
	}
}

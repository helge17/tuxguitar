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
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

public class TGSetDurationAction extends TGActionBase {

	public static final String NAME = "action.note.duration.set-duration";

	public TGSetDurationAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGBeatRange beats = (TGBeatRange)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		TGDuration duration = (TGDuration)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION);
		TGVoice voice = (TGVoice)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
		TGBeat selectedBeat = (TGBeat)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);

		TGSongManager songManager = getSongManager(context);
		TGMeasureManager measureManager = songManager.getMeasureManager();
		TGFactory factory = songManager.getFactory();

		if (beats!=null && !beats.isEmpty()){
			for (TGBeat beat : beats.getBeats()) {
				TGMeasure measure = beat.getMeasure();
				measureManager.changeDuration(measure, beat, duration.clone(factory), voice.getIndex(), true);
			}
		}
		else if (selectedBeat != null) {
			measureManager.changeDuration(selectedBeat.getMeasure(), selectedBeat, duration.clone(factory), voice.getIndex(), true);
		}
	}
}

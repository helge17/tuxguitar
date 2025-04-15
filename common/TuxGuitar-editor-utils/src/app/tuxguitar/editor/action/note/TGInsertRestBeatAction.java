package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

public class TGInsertRestBeatAction extends TGActionBase {

	public static final String NAME = "action.beat.general.insert-rest";

	public TGInsertRestBeatAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
		TGVoice voice = ((TGVoice) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE));
		TGDuration duration = ((TGDuration) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION));
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGSongManager songManager = getSongManager(context);
		
		if (songManager.isFreeEditionMode(measure)) {
			songManager.getMeasureManager().insertRestBeatWithoutControl(beat, voice.getIndex());
		}
		else if( voice.isEmpty() ){
			songManager.getMeasureManager().addSilence(beat, duration.clone(getSongManager(context).getFactory()), voice.getIndex());
		}
		else {
			long start = beat.getStart();
			long length = voice.getDuration().getTime();
			songManager.getMeasureManager().moveVoices(measure, start, length, voice.getIndex(), beat.getVoice(voice.getIndex()).getDuration());
		}
	}
}

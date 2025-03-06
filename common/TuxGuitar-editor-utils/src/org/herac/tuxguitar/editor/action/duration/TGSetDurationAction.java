package org.herac.tuxguitar.editor.action.duration;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGMeasureManager;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGBeatRange;
import org.herac.tuxguitar.util.TGContext;

public class TGSetDurationAction extends TGActionBase {

	public static final String NAME = "action.note.duration.set-duration";

	public TGSetDurationAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGBeatRange beats = (TGBeatRange)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		TGDuration duration = (TGDuration)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION);
		TGVoice voice = (TGVoice)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
		
		TGSongManager songManager = getSongManager(context);
		TGMeasureManager measureManager = songManager.getMeasureManager();
		TGFactory factory = songManager.getFactory();

		if (beats!=null && !beats.isEmpty()){
			for (TGBeat beat : beats.getBeats()) {
				TGMeasure measure = beat.getMeasure();
				measureManager.changeDuration(measure, beat, duration.clone(factory), voice.getIndex(), true);
			}
		}
	}
}

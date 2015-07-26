package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGContext;

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
		
		if( voice.isEmpty() ){
			getSongManager(context).getMeasureManager().addSilence(beat, duration.clone(getSongManager(context).getFactory()), voice.getIndex());
		}
		else {
			long start = beat.getStart();
			long length = voice.getDuration().getTime();
			getSongManager(context).getMeasureManager().moveVoices(measure, start, length, voice.getIndex(), beat.getVoice(voice.getIndex()).getDuration());
		}
	}
}

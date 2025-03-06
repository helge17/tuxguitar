package org.herac.tuxguitar.editor.action.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;

public class TGFixMeasureVoiceAction extends TGActionBase {
	
	public static final String NAME = "action.measure.fix-voice";
	public static final String ATTRIBUTE_VOICE_INDEX  = "action.measure.fix-voice.voice-index";

	public TGFixMeasureVoiceAction(TGContext context) {
		super(context, NAME);
	}

	@Override
	protected void processAction(TGActionContext actionContext) {
		TGSongManager songMgr = getSongManager(actionContext);
		TGMeasure measure = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		int voiceIndex = actionContext.getAttribute(ATTRIBUTE_VOICE_INDEX);

		songMgr.getMeasureManager().fixVoice(measure, voiceIndex);
		
	}

}

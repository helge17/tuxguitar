package app.tuxguitar.editor.action.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGContext;

public class TGFixMeasureVoiceAction extends TGActionBase {
	
	public static final String NAME = "action.measure.fix-voice";
	public static final String ATTRIBUTE_VOICE_INDEX  = "action.measure.fix-voice.voice-index";
	public static final String ATTRIBUTE_ERR_CODE = "action.measure.fix-voice.err-code";

	public TGFixMeasureVoiceAction(TGContext context) {
		super(context, NAME);
	}

	@Override
	protected void processAction(TGActionContext actionContext) {
		TGSongManager songMgr = getSongManager(actionContext);
		TGMeasure measure = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		int voiceIndex = actionContext.getAttribute(ATTRIBUTE_VOICE_INDEX);
		int errCode = actionContext.getAttribute(ATTRIBUTE_ERR_CODE);

		songMgr.getMeasureManager().fixVoice(measure, voiceIndex, errCode);
		
	}

}

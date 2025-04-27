package app.tuxguitar.editor.action.duration;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

public class TGSetSixteenthDurationAction extends TGActionBase {

	public static final String NAME = "action.note.duration.set-sixteenth";

	public static final int VALUE = TGDuration.SIXTEENTH;

	public TGSetSixteenthDurationAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGVoice voice = ((TGVoice) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE));
		TGDuration duration = ((TGDuration) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION));

		if( duration.getValue() != VALUE || (!voice.isEmpty() && voice.getDuration().getValue() != VALUE) ){
			duration.setValue(VALUE);
			duration.setDotted(false);
			duration.setDoubleDotted(false);

			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGSetDurationAction.NAME, context);
		}
	}
}

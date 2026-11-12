package app.tuxguitar.editor.action.duration;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

/**
 * Sets one fixed duration value, undotted, through TGSetDurationAction, unless the duration to set has that value
 * already and the caret voice is empty or has it too.
 */
public abstract class TGSetFixedDurationAction extends TGActionBase {

	private final int value;

	protected TGSetFixedDurationAction(TGContext context, String name, int value) {
		super(context, name);
		this.value = value;
	}

	protected void processAction(TGActionContext context) {
		TGVoice voice = ((TGVoice) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE));
		TGDuration duration = ((TGDuration) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION));

		if( duration.getValue() != this.value || (!voice.isEmpty() && voice.getDuration().getValue() != this.value) ){
			duration.setValue(this.value);
			duration.setDotted(false);
			duration.setDoubleDotted(false);

			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGSetDurationAction.NAME, context);
		}
	}
}

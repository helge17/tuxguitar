package app.tuxguitar.editor.action.duration;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.util.TGContext;

public class TGIncrementDurationAction extends TGActionBase {

	public static final String NAME = "action.note.duration.increment-duration";

	public TGIncrementDurationAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGDuration duration = ((TGDuration) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION));

		if( duration.getValue() > TGDuration.WHOLE ){
			duration.setValue(duration.getValue() / 2);
			duration.setDotted(false);
			duration.setDoubleDotted(false);

			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGSetDurationAction.NAME, context);
		}
	}
}

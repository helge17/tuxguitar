package app.tuxguitar.editor.action.duration;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.util.TGContext;

public class TGChangeDoubleDottedDurationAction extends TGActionBase {

	public static final String NAME = "action.note.duration.change-double-dotted";

	public TGChangeDoubleDottedDurationAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGDuration duration = ((TGDuration) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION));

		duration.setDoubleDotted(!duration.isDoubleDotted());
		duration.setDotted(false);

		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(TGSetDurationAction.NAME, context);
	}
}

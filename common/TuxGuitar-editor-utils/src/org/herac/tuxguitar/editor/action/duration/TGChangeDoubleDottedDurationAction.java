package org.herac.tuxguitar.editor.action.duration;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.util.TGContext;

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

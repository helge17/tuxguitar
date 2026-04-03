package app.tuxguitar.app.action.impl.marker;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.util.TGContext;

public class TGModifyMarkerAction extends TGActionBase{

	public static final String NAME = "action.marker.modify";

	public static final String ATTRIBUTE_MODIFIED_MARKER = "modifiedMarker";

	public TGModifyMarkerAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext actionContext){
		TGActionManager actionManager = TGActionManager.getInstance(getContext());

		TGMarker modifiedMarker = actionContext.getAttribute(ATTRIBUTE_MODIFIED_MARKER);
		TGMarker currentMarker = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER);

		if( currentMarker != null ) {
			actionManager.execute(TGRemoveMarkerAction.NAME, actionContext);
		}

		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, modifiedMarker);
		actionManager.execute(TGUpdateMarkerAction.NAME, actionContext);
	}
}

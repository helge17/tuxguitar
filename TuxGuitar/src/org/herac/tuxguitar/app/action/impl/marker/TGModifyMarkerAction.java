package org.herac.tuxguitar.app.action.impl.marker;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.util.TGContext;

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

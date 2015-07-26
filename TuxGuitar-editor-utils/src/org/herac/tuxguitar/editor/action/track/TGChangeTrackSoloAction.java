package org.herac.tuxguitar.editor.action.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGChangeTrackSoloAction extends TGActionBase {
	
	public static final String NAME = "action.track.change-solo";
	
	public TGChangeTrackSoloAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		
		context.setAttribute(TGSetTrackSoloAction.ATTRIBUTE_SOLO, Boolean.valueOf(!track.isSolo()));
		
		TGActionManager.getInstance(getContext()).execute(TGSetTrackSoloAction.NAME, context);
	}
}

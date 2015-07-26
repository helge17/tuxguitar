package org.herac.tuxguitar.editor.action.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGSetTrackNameAction extends TGActionBase {
	
	public static final String NAME = "action.track.set-name";
	
	public static final String ATTRIBUTE_TRACK_NAME = TGSetTrackInfoAction.ATTRIBUTE_TRACK_NAME;
	
	public TGSetTrackNameAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		String name = ((String) context.getAttribute(ATTRIBUTE_TRACK_NAME));
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		
		if( track != null && name != null ){
			context.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_OFFSET, track.getOffset());
			context.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_COLOR, track.getColor());
			
			TGActionManager.getInstance(getContext()).execute(TGSetTrackInfoAction.NAME, context);
		}
	}
}

package org.herac.tuxguitar.editor.action.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGSetTrackInfoAction extends TGActionBase {
	
	public static final String NAME = "action.track.set-info";
	
	public static final String ATTRIBUTE_TRACK_NAME = "name";
	public static final String ATTRIBUTE_TRACK_OFFSET = "offset";
	public static final String ATTRIBUTE_TRACK_COLOR = "color";
	
	public TGSetTrackInfoAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		if( track != null ){
			String name = ((String) context.getAttribute(ATTRIBUTE_TRACK_NAME));
			Integer offset = ((Integer) context.getAttribute(ATTRIBUTE_TRACK_OFFSET));
			TGColor color = ((TGColor) context.getAttribute(ATTRIBUTE_TRACK_COLOR));
			
			getSongManager(context).getTrackManager().changeInfo(track, name, color, offset);
		}
	}
}

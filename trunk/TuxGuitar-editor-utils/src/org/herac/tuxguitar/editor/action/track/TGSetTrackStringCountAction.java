package org.herac.tuxguitar.editor.action.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGSetTrackStringCountAction extends TGActionBase {
	
	public static final String NAME = "action.track.set-string-count";
	
	public static final String ATTRIBUTE_STRING_COUNT = "stringCount";
	
	public TGSetTrackStringCountAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		Integer count = context.getAttribute(ATTRIBUTE_STRING_COUNT);
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		if( track != null && count != null ){
			getSongManager(context).getTrackManager().changeStringCount(track, count);
		}
	}
}

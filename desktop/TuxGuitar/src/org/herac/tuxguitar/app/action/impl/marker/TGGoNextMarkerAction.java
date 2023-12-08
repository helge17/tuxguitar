package org.herac.tuxguitar.app.action.impl.marker;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGGoNextMarkerAction extends TGActionBase{
	
	public static final String NAME = "action.marker.go-next";
	
	public TGGoNextMarkerAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		TGMarker marker = getSongManager(context).getNextMarker(song, header.getNumber());
		if( marker != null ) {
			context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, marker);
			
			TGActionManager.getInstance(getContext()).execute(TGGoToMarkerAction.NAME, context);
		}
	}
}

package org.herac.tuxguitar.app.action.impl.marker;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateMarkerAction extends TGActionBase{
	
	public static final String NAME = "action.marker.update";
	
	public TGUpdateMarkerAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGMarker marker = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER);
		
		getSongManager(context).updateMarker(song, marker);
	}
}

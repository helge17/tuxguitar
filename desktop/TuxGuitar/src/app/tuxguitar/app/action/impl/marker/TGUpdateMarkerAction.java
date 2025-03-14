package app.tuxguitar.app.action.impl.marker;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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

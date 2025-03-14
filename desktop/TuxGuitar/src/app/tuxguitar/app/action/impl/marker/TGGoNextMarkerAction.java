package app.tuxguitar.app.action.impl.marker;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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

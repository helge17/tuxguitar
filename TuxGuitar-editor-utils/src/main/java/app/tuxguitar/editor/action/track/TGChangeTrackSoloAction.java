package app.tuxguitar.editor.action.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

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

package app.tuxguitar.editor.action.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGChangeTrackMuteAction extends TGActionBase {

	public static final String NAME = "action.track.change-mute";

	public TGChangeTrackMuteAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));

		context.setAttribute(TGSetTrackMuteAction.ATTRIBUTE_MUTE, Boolean.valueOf(!track.isMute()));

		TGActionManager.getInstance(getContext()).execute(TGSetTrackMuteAction.NAME, context);
	}
}

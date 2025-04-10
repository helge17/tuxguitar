package app.tuxguitar.editor.action.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGChangeTrackPropertiesAction extends TGActionBase {

	public static final String NAME = "action.track.change-properties";

	public static final String ATTRIBUTE_UPDATE_INFO = "updateInfo";
	public static final String ATTRIBUTE_UPDATE_CHANNEL = "updateChannel";
	public static final String ATTRIBUTE_UPDATE_TUNING = "updateTuning";

	public TGChangeTrackPropertiesAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		if( Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_UPDATE_INFO)) ) {
			tgActionManager.execute(TGSetTrackInfoAction.NAME, context);
		}
		if( Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_UPDATE_TUNING)) ) {
			tgActionManager.execute(TGChangeTrackTuningAction.NAME, context);
		}
		if( Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_UPDATE_CHANNEL)) ) {
			tgActionManager.execute(TGSetTrackChannelAction.NAME, context);
		}
	}
}
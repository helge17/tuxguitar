package app.tuxguitar.app.action.impl.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.view.dialog.track.TGTrackPropertiesDialogController;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGOpenTrackPropertiesDialogAction extends TGActionBase{

	public static final String NAME = "action.gui.open-track-properties-dialog";

	public TGOpenTrackPropertiesDialogAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext tgActionContext){
		tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGTrackPropertiesDialogController());
		TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
	}
}
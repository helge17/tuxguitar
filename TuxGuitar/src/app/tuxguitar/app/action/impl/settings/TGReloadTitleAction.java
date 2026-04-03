package app.tuxguitar.app.action.impl.settings;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGReloadTitleAction extends TGActionBase {

	public static final String NAME = "action.system.reload-title";

	public TGReloadTitleAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGWindow.getInstance(getContext()).loadTitle();
	}
}
package app.tuxguitar.app.action.impl.layout;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGSetLayoutScaleResetAction extends TGActionBase{

	public static final String NAME = "action.view.layout-reset-scale";

	public TGSetLayoutScaleResetAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext tgActionContext) {
		tgActionContext.setAttribute(TGSetLayoutScaleAction.ATTRIBUTE_SCALE, Tablature.DEFAULT_SCALE);
		TGActionManager.getInstance(getContext()).execute(TGSetLayoutScaleAction.NAME, tgActionContext);
	}
}

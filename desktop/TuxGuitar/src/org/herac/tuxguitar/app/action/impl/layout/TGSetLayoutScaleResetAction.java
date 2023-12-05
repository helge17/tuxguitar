package org.herac.tuxguitar.app.action.impl.layout;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

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

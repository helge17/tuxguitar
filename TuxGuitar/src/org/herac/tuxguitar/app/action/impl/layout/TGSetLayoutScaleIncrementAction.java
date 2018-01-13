package org.herac.tuxguitar.app.action.impl.layout;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGSetLayoutScaleIncrementAction extends TGActionBase{
	
	public static final String NAME = "action.view.layout-increment-scale";
	
	private static final Float MAXIMUM_VALUE = 2f;
	private static final Float INCREMENT_VALUE = 0.1f;
	
	public TGSetLayoutScaleIncrementAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext tgActionContext) {
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		Float scale = Math.min((tablature.getScale() + INCREMENT_VALUE), MAXIMUM_VALUE);
		
		tgActionContext.setAttribute(TGSetLayoutScaleAction.ATTRIBUTE_SCALE, scale);
		TGActionManager.getInstance(getContext()).execute(TGSetLayoutScaleAction.NAME, tgActionContext);
	}
}

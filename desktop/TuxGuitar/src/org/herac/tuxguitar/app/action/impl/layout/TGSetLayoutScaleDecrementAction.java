package org.herac.tuxguitar.app.action.impl.layout;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGSetLayoutScaleDecrementAction extends TGActionBase{
	
	public static final String NAME = "action.view.layout-decrement-scale";
	
	private static final Float MINIMUM_VALUE = 0.5f;
	private static final Float DECREMENT_VALUE = 0.1f;
	
	public TGSetLayoutScaleDecrementAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext tgActionContext) {
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		Float scale = Math.max((tablature.getScale() - DECREMENT_VALUE), MINIMUM_VALUE);
		
		tgActionContext.setAttribute(TGSetLayoutScaleAction.ATTRIBUTE_SCALE, scale);
		TGActionManager.getInstance(getContext()).execute(TGSetLayoutScaleAction.NAME, tgActionContext);
	}
}

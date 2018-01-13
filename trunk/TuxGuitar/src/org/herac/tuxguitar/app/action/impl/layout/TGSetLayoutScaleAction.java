package org.herac.tuxguitar.app.action.impl.layout;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGSetLayoutScaleAction extends TGActionBase{
	
	public static final String NAME = "action.view.layout-set-scale";
	
	public static final String ATTRIBUTE_SCALE = "scale";
	
	public TGSetLayoutScaleAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		Float scale = ((Float) context.getAttribute(ATTRIBUTE_SCALE));
		
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		tablature.scale(scale);
	}
}

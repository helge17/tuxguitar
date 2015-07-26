package org.herac.tuxguitar.app.action.impl.layout;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGLayoutHorizontal;
import org.herac.tuxguitar.util.TGContext;

public class TGSetLinearLayoutAction extends TGActionBase{
	
	public static final String NAME = "action.view.layout-set-linear";
	
	public TGSetLinearLayoutAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		tablature.setViewLayout(new TGLayoutHorizontal(tablature,tablature.getViewLayout().getStyle()));
	}
}

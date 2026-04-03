package app.tuxguitar.app.action.impl.layout;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.graphics.control.TGLayoutHorizontal;
import app.tuxguitar.util.TGContext;

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

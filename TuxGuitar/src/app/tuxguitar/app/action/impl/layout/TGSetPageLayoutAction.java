package app.tuxguitar.app.action.impl.layout;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.graphics.control.TGLayoutVertical;
import app.tuxguitar.util.TGContext;

public class TGSetPageLayoutAction extends TGActionBase{

	public static final String NAME = "action.view.layout-set-page";

	public TGSetPageLayoutAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		tablature.setViewLayout(new TGLayoutVertical(tablature,tablature.getViewLayout().getStyle()));
	}
}

package app.tuxguitar.app.action.impl.layout;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGSetLayoutScaleAction extends TGActionBase{

	public static final String NAME = "action.view.layout-set-scale";

	public static final String ATTRIBUTE_SCALE = "scale";

	public TGSetLayoutScaleAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		Float scale = (Float) context.getAttribute(ATTRIBUTE_SCALE);

		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		tablature.scale(scale);
	}
}

package app.tuxguitar.app.action.impl.layout;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

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

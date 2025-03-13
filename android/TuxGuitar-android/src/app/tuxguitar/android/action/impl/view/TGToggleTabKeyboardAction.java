package app.tuxguitar.android.action.impl.view;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.view.keyboard.TGTabKeyboardController;
import app.tuxguitar.util.TGContext;

public class TGToggleTabKeyboardAction extends TGActionBase{

	public static final String NAME = "action.view.toogle-tab-keyboard";

	public TGToggleTabKeyboardAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGTabKeyboardController.getInstance(getContext()).toggleVisibility();
	}
}

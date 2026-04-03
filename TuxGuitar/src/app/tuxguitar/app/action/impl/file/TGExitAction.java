package app.tuxguitar.app.action.impl.file;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGExitAction extends TGActionBase {

	public static final String NAME = "action.file.exit";

	public TGExitAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGWindow.getInstance(getContext()).getWindow().close();
	}
}

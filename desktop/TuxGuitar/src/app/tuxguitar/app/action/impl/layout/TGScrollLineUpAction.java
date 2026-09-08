package app.tuxguitar.app.action.impl.layout;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGScrollLineUpAction extends TGActionBase {

	public static final String NAME = "action.view.scroll-line-up";

	public TGScrollLineUpAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGMoveCaretToAdjacentLine.processLine(getContext(), context, -1);
	}
}

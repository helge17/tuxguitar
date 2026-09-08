package app.tuxguitar.app.action.impl.layout;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGScrollPageUpAction extends TGActionBase {

	public static final String NAME = "action.view.scroll-page-up";

	public TGScrollPageUpAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGMoveCaretToAdjacentLine.process(getContext(), context, -1);
	}
}

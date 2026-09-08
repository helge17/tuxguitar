package app.tuxguitar.app.action.impl.layout;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGScrollPageDownAction extends TGActionBase {

	public static final String NAME = "action.view.scroll-page-down";

	public TGScrollPageDownAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGMoveCaretToAdjacentLine.processPage(getContext(), context, 1);
	}
}

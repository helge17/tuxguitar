package app.tuxguitar.android.action.impl.caret;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGGoLeftAction extends TGActionBase{

	public static final String NAME = "action.caret.go-left";

	public TGGoLeftAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		getEditor().getCaret().moveLeft();
	}
}

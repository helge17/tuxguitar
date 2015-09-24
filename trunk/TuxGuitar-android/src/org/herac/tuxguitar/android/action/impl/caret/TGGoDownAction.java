package org.herac.tuxguitar.android.action.impl.caret;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGGoDownAction extends TGActionBase{
	
	public static final String NAME = "action.caret.go-down";
	
	public TGGoDownAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		getEditor().getCaret().moveDown();
	}
}

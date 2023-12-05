package org.herac.tuxguitar.app.action.impl.settings;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGReloadTitleAction extends TGActionBase {
	
	public static final String NAME = "action.system.reload-title";
	
	public TGReloadTitleAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGWindow.getInstance(getContext()).loadTitle();
	}
}
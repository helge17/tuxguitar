package org.herac.tuxguitar.app.action.impl.settings;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGReloadStylesAction extends TGActionBase {
	
	public static final String NAME = "action.system.reload-styles";
	
	public TGReloadStylesAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TuxGuitar.getInstance().getTablatureEditor().getTablature().reloadStyles();
	}
}
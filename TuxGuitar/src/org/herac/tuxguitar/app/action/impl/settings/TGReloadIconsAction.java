package org.herac.tuxguitar.app.action.impl.settings;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGReloadIconsAction extends TGActionBase {
	
	public static final String NAME = "action.system.reload-icons";
	
	public TGReloadIconsAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		Boolean force = Boolean.TRUE.equals(context.getAttribute(TGReloadSettingsAction.ATTRIBUTE_FORCE));
		TGIconManager tgIconManager = TGIconManager.getInstance(getContext());
		if( force || tgIconManager.shouldReload() ){
			tgIconManager.reloadIcons();
		}
	}
}
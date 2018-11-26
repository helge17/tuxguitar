package org.herac.tuxguitar.app.action.impl.settings;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.system.icons.TGSkinManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGReloadSkinAction extends TGActionBase {
	
	public static final String NAME = "action.system.reload-skin";
	
	public TGReloadSkinAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		Boolean force = Boolean.TRUE.equals(context.getAttribute(TGReloadSettingsAction.ATTRIBUTE_FORCE));
		TGSkinManager tgSkinManager = TGSkinManager.getInstance(getContext());
		if( force || tgSkinManager.shouldReload() ){
			tgSkinManager.reloadSkin();
		}
	}
}
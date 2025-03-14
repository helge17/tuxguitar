package app.tuxguitar.app.action.impl.settings;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.system.icons.TGSkinManager;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

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
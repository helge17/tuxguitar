package org.herac.tuxguitar.app.action.impl.settings;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGReloadSettingsAction extends TGActionBase {
	
	public static final String NAME = "action.system.reload-settings";
	
	public static final String ATTRIBUTE_FORCE = "force";
	
	public TGReloadSettingsAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(TGReloadTitleAction.NAME, context);
		tgActionManager.execute(TGReloadIconsAction.NAME, context);
		tgActionManager.execute(TGReloadLanguageAction.NAME, context);
		tgActionManager.execute(TGReloadMidiDevicesAction.NAME, context);
		tgActionManager.execute(TGReloadStylesAction.NAME, context);
		tgActionManager.execute(TGReloadTableSettingsAction.NAME, context);
	}
}
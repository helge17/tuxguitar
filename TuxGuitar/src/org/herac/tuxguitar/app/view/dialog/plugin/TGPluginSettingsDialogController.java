package org.herac.tuxguitar.app.view.dialog.plugin;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsManager;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGPluginSettingsDialogController implements TGOpenViewController {

	public static String ATTRIBUTE_MODULE_ID = "moduleId";
	
	public void openView(TGViewContext context) {
		String moduleId = context.getAttribute(ATTRIBUTE_MODULE_ID);
		Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		
		TGPluginSettingsManager.getInstance().openPluginSettingsDialog(moduleId, parent);
	}
}

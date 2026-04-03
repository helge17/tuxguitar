package app.tuxguitar.app.view.dialog.plugin;

import app.tuxguitar.app.system.plugins.TGPluginSettingsManager;
import app.tuxguitar.app.view.controller.TGOpenViewController;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.ui.widget.UIWindow;

public class TGPluginSettingsDialogController implements TGOpenViewController {

	public static String ATTRIBUTE_MODULE_ID = "moduleId";

	public void openView(TGViewContext context) {
		String moduleId = context.getAttribute(ATTRIBUTE_MODULE_ID);
		UIWindow parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);

		TGPluginSettingsManager.getInstance().openPluginSettingsDialog(moduleId, parent);
	}
}

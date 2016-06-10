package org.herac.tuxguitar.carbon.menu;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.carbon.TGCarbonIntegrationPlugin;
import org.herac.tuxguitar.ui.swt.SWTApplication;
import org.herac.tuxguitar.ui.swt.widget.SWTWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class MacMenuPlugin implements TGPlugin {
	
	private MacMenu macMenu;
	
	public void setEnabled(TGContext context, boolean enabled) throws TGPluginException {
		if( this.macMenu != null ){
			this.macMenu.setEnabled(enabled);
		}else if(enabled){
			this.macMenu = new MacMenu(context);
			this.macMenu.setEnabled(true);
			this.macMenu.hookApplicationMenu(((SWTApplication)TGApplication.getInstance(context).getApplication()).getDisplay(), ((SWTWindow) TGWindow.getInstance(context).getWindow()).getControl());
		}
	}

	public String getModuleId() {
		return TGCarbonIntegrationPlugin.MODULE_ID;
	}
	
	public void connect(TGContext context) throws TGPluginException {
		this.setEnabled(context, true);
	}

	public void disconnect(TGContext context) throws TGPluginException {
		this.setEnabled(context, false);
	}
}

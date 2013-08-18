package org.herac.tuxguitar.carbon.menu;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.carbon.TGCarbonIntegrationPlugin;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class MacMenuPlugin implements TGPlugin {
	
	private MacMenu macMenu;
	
	public void init() throws TGPluginException {
		// Nothing todo
	}
	
	public void close() throws TGPluginException {
		// Nothing todo
	}
	
	public void setEnabled(boolean enabled) throws TGPluginException {
		if( this.macMenu != null ){
			this.macMenu.setEnabled(enabled);
		}else if(enabled){
			this.macMenu = new MacMenu();
			this.macMenu.setEnabled(true);
			this.macMenu.hookApplicationMenu(TuxGuitar.instance().getDisplay(), TuxGuitar.instance().getShell());
		}
	}

	public String getModuleId() {
		return TGCarbonIntegrationPlugin.MODULE_ID;
	}
}

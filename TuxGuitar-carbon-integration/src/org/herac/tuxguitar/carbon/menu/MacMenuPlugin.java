package org.herac.tuxguitar.carbon.menu;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.base.TGPluginAdapter;

public class MacMenuPlugin extends TGPluginAdapter {
	
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

}

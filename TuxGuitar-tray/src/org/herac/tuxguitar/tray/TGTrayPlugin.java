package org.herac.tuxguitar.tray;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGTrayPlugin implements TGPlugin {
	
	public static final String MODULE_ID = "tuxguitar-tray";
	
	private TGTray tray;
	
	public TGTrayPlugin(){
		super();
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}

	public void connect(TGContext context) throws TGPluginException {
		if( this.tray == null ) {
			this.tray = new TGTray(context);
			this.tray.addTray();
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		if( this.tray != null ) {
			this.tray.removeTray();
			this.tray = null;
		}
	}
}

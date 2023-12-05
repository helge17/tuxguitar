package org.herac.tuxguitar.cocoa.toolbar;

import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.cocoa.TGCocoaIntegrationPlugin;
import org.herac.tuxguitar.ui.swt.widget.SWTWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class MacToolbarPlugin implements TGPlugin {
	
	private MacToolbar macToolbar;
	
	public String getModuleId() {
		return TGCocoaIntegrationPlugin.MODULE_ID;
	}
	
	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.macToolbar != null ){
				this.macToolbar.setEnabled(true);
			 }else {
				this.macToolbar = new MacToolbar();
				this.macToolbar.setEnabled(true);
				this.macToolbar.init(((SWTWindow) TGWindow.getInstance(context).getWindow()).getControl());
			}
		} catch( Throwable throwable ){
			throw new TGPluginException( throwable );
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.macToolbar != null ){
				this.macToolbar.setEnabled(false);
				this.macToolbar.finalize();
			 }
		} catch( Throwable throwable ){
			throw new TGPluginException( throwable );
		}
	}
}

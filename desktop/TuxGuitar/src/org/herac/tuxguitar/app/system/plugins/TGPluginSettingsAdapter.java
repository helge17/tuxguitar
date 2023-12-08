package org.herac.tuxguitar.app.system.plugins;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGPluginSettingsAdapter implements TGPlugin{
	
	private TGPluginSettingsHandler handler;
	
	protected abstract TGPluginSettingsHandler createHandler(TGContext context) throws TGPluginException;
	
	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.handler == null ) {
				this.handler = createHandler(context);
				
				TGPluginSettingsManager.getInstance().addPluginSettingsHandler(getModuleId(), this.handler);
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
	
	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.handler != null ) {
				TGPluginSettingsManager.getInstance().removePluginSettingsHandler(getModuleId());
				
				this.handler = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
}

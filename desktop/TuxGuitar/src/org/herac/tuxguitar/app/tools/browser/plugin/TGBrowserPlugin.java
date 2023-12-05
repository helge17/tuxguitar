package org.herac.tuxguitar.app.tools.browser.plugin;

import org.herac.tuxguitar.app.tools.browser.TGBrowserManager;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGBrowserPlugin implements TGPlugin{
	
	private TGBrowserFactory factory;
	
	protected abstract TGBrowserFactory getFactory(TGContext context) throws TGPluginException;
	
	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.factory == null ) {
				this.factory = getFactory(context);
				
				TGBrowserManager.getInstance(context).addFactory(this.factory);
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
	
	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.factory != null ) {
				TGBrowserManager.getInstance(context).removeFactory(this.factory);
				
				this.factory = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
}

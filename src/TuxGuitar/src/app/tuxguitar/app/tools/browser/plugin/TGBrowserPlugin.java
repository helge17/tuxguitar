package app.tuxguitar.app.tools.browser.plugin;

import app.tuxguitar.app.tools.browser.TGBrowserManager;
import app.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

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

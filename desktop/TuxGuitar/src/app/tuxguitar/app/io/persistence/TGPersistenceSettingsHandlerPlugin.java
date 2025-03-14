package app.tuxguitar.app.io.persistence;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

public abstract class TGPersistenceSettingsHandlerPlugin implements TGPlugin {

	private TGPersistenceSettingsHandler handler;

	protected abstract TGPersistenceSettingsHandler createSettingsHandler(TGContext context) throws TGPluginException;

	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.handler == null ){
				this.handler = this.createSettingsHandler(context);
				if( this.handler != null ){
					TGPersistenceSettingsManager.getInstance(context).addSettingsHandler(this.handler);
				}
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.handler != null ){
				TGPersistenceSettingsManager.getInstance(context).removeSettingsHandler(this.handler);
				this.handler = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
}

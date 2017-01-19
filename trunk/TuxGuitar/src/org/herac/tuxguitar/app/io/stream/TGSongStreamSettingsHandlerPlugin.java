package org.herac.tuxguitar.app.io.stream;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGSongStreamSettingsHandlerPlugin implements TGPlugin {
	
	private TGSongStreamSettingsHandler handler;
	
	protected abstract TGSongStreamSettingsHandler createSettingsHandler(TGContext context) throws TGPluginException;
	
	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.handler == null ){
				this.handler = this.createSettingsHandler(context);
				if( this.handler != null ){
					TGSongStreamAdapterManager.getInstance(context).addSettingsHandler(this.handler);
				}
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
	
	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.handler != null ){
				TGSongStreamAdapterManager.getInstance(context).removeSettingsHandler(this.handler);
				this.handler = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
}

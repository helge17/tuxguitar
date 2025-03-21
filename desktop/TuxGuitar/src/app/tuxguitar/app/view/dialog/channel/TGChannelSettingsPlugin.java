package app.tuxguitar.app.view.dialog.channel;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

public abstract class TGChannelSettingsPlugin implements TGPlugin{

	private TGChannelSettingsHandler handler;

	protected abstract TGChannelSettingsHandler createHandler(TGContext context) throws TGPluginException;

	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.handler == null ) {
				this.handler = createHandler(context);

				TGChannelManagerDialog.getInstance(context).getChannelSettingsHandlerManager().addChannelSettingsHandler(this.handler);
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.handler != null ) {
				TGChannelManagerDialog.getInstance(context).getChannelSettingsHandlerManager().removeChannelSettingsHandler(this.handler);

				this.handler = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
}

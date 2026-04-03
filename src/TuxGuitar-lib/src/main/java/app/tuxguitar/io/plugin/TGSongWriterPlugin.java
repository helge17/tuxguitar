package app.tuxguitar.io.plugin;

import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

public abstract class TGSongWriterPlugin implements TGPlugin{

	private TGSongWriter stream;

	protected abstract TGSongWriter createOutputStream(TGContext context) throws TGPluginException ;

	public void connect(TGContext context) throws TGPluginException {
		try {
			TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(context);

			if( this.stream == null ) {
				this.stream = createOutputStream(context);
				fileFormatManager.addWriter(this.stream);
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		try {
			TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(context);

			if( this.stream != null ) {
				fileFormatManager.removeWriter(this.stream);
				this.stream = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
}

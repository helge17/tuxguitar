package app.tuxguitar.io.plugin;

import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.io.base.TGSongExporter;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

public abstract class TGSongExporterPlugin implements TGPlugin{

	private TGSongExporter exporter;

	protected abstract TGSongExporter createExporter(TGContext context) throws TGPluginException;

	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.exporter == null ) {
				this.exporter = createExporter(context);

				TGFileFormatManager.getInstance(context).addExporter(this.exporter);
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.exporter != null ) {
				TGFileFormatManager.getInstance(context).removeExporter(this.exporter);

				this.exporter = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
}

package app.tuxguitar.io.plugin;

import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.io.base.TGSongImporter;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

public abstract class TGSongImporterPlugin implements TGPlugin{

	private TGSongImporter importer;

	protected abstract TGSongImporter createImporter(TGContext context) throws TGPluginException;

	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.importer == null ) {
				this.importer = createImporter(context);

				TGFileFormatManager.getInstance(context).addImporter(this.importer);
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.importer != null ) {
				TGFileFormatManager.getInstance(context).removeImporter(this.importer);

				this.importer = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
}

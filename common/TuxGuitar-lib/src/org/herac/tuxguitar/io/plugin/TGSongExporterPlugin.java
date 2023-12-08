package org.herac.tuxguitar.io.plugin;

import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGSongExporter;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

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

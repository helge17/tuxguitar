package org.herac.tuxguitar.io.plugin;

import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGSongWriterPlugin implements TGPlugin{
	
	private boolean commonFileFormat;
	private TGSongWriter stream;
	
	public TGSongWriterPlugin(boolean commonFileFormat) {
		this.commonFileFormat = commonFileFormat;
	}
	
	protected abstract TGSongWriter createOutputStream(TGContext context) throws TGPluginException ;
	
	public void connect(TGContext context) throws TGPluginException {
		try {
			TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(context);
			
			if( this.stream == null ) {
				this.stream = createOutputStream(context);
				
				fileFormatManager.addWriter(this.stream);
				
				if( this.commonFileFormat ) {
					fileFormatManager.addCommonWriteFileFormat(this.stream.getFileFormat());
				}
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
	
	public void disconnect(TGContext context) throws TGPluginException {
		try {
			TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(context);
			
			if( this.stream != null ) {
				if( this.commonFileFormat ) {
					fileFormatManager.removeCommonWriteFileFormat(this.stream.getFileFormat());
				}
				
				fileFormatManager.removeWriter(this.stream);
				
				this.stream = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
}

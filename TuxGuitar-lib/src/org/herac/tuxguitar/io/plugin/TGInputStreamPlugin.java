package org.herac.tuxguitar.io.plugin;

import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGInputStreamPlugin implements TGPlugin{
	
	private TGInputStreamBase stream;
	
	protected abstract TGInputStreamBase createInputStream(TGContext context) throws TGPluginException ;
	
	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.stream == null ) {
				this.stream = createInputStream(context);
				
				TGFileFormatManager.getInstance(context).addInputStream(this.stream);
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
	
	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.stream != null ) {
				TGFileFormatManager.getInstance(context).removeInputStream(this.stream);
				
				this.stream = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
}

package org.herac.tuxguitar.io.plugin;

import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGOutputStreamPlugin implements TGPlugin{
	
	private TGOutputStreamBase stream;
	
	protected abstract TGOutputStreamBase createOutputStream(TGContext context) throws TGPluginException ;
	
	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.stream == null ) {
				this.stream = createOutputStream(context);
				
				TGFileFormatManager.getInstance(context).addOutputStream(this.stream);
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
	
	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.stream != null ) {
				TGFileFormatManager.getInstance(context).removeOutputStream(this.stream);
				
				this.stream = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
}

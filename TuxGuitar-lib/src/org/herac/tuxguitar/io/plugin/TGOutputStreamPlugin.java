package org.herac.tuxguitar.io.plugin;

import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGOutputStreamPlugin implements TGPlugin{
	
	private boolean loaded;
	private TGOutputStreamBase stream;
	
	protected abstract TGOutputStreamBase getOutputStream() throws TGPluginException ;
	
	public void init(TGContext context) throws TGPluginException {
		this.stream = getOutputStream();
	}
	
	public void close() throws TGPluginException {
		this.removePlugin();
	}
	
	public void setEnabled(boolean enabled) throws TGPluginException {
		if(enabled){
			addPlugin();
		}else{
			removePlugin();
		}
	}
	
	protected void addPlugin() throws TGPluginException {
		if(!this.loaded){
			TGFileFormatManager.instance().addOutputStream(this.stream);
			this.loaded = true;
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded){
			TGFileFormatManager.instance().removeOutputStream(this.stream);
			this.loaded = false;
		}
	}
}

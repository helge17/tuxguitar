package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;

public abstract class TGOutputStreamPlugin extends TGPluginAdapter{
	
	private boolean loaded;
	private TGOutputStreamBase stream;
	
	protected abstract TGOutputStreamBase getOutputStream() throws TGPluginException ;
	
	public void init() throws TGPluginException {
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

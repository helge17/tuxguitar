package org.herac.tuxguitar.io.plugin;

import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGInputStreamPlugin implements TGPlugin{
	
	private boolean loaded;
	private TGInputStreamBase stream;
	
	protected abstract TGInputStreamBase getInputStream() throws TGPluginException ;
	
	public void init() throws TGPluginException {
		this.stream = getInputStream();
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
			TGFileFormatManager.instance().addInputStream(this.stream);
			this.loaded = true;
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded){
			TGFileFormatManager.instance().removeInputStream(this.stream);
			this.loaded = false;
		}
	}
}

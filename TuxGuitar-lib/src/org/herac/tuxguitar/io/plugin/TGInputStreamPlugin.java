package org.herac.tuxguitar.io.plugin;

import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGInputStreamPlugin implements TGPlugin{
	
	private boolean loaded;
	private TGContext context;
	private TGInputStreamBase stream;
	
	protected abstract TGInputStreamBase getInputStream() throws TGPluginException ;
	
	public void init(TGContext context) throws TGPluginException {
		this.context = context;
		this.stream = getInputStream();
	}
	
	public TGContext getContext() {
		return this.context;
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
			TGFileFormatManager.getInstance(this.context).addInputStream(this.stream);
			this.loaded = true;
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded){
			TGFileFormatManager.getInstance(this.context).removeInputStream(this.stream);
			this.loaded = false;
		}
	}
}

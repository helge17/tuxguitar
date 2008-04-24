package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;

public abstract class TGOutputStreamPlugin extends TGPluginAdapter{
	
	private boolean loaded;
	private TGOutputStreamBase stream;
	
	protected abstract TGOutputStreamBase getOutputStream();
	
	public void init(){
		this.stream = getOutputStream();
	}
	
	public void close(){
		this.removePlugin();
	}
	
	protected void addPlugin(){
		if(!this.loaded){
			TGFileFormatManager.instance().addOutputStream(this.stream);
			this.loaded = true;
		}
	}
	
	protected void removePlugin(){
		if(this.loaded){
			TGFileFormatManager.instance().removeOutputStream(this.stream);
			this.loaded = false;
		}
	}
	
	public void setEnabled(boolean enabled) {
		if(enabled){
			addPlugin();
		}else{
			removePlugin();
		}
	}
}

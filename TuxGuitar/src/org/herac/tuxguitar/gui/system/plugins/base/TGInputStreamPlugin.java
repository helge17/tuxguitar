package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGInputStreamBase;

public abstract class TGInputStreamPlugin extends TGPluginAdapter{

	private boolean loaded;
	private TGInputStreamBase stream;
	
	protected abstract TGInputStreamBase getInputStream();
	
	public void init(){
		this.stream = getInputStream();
	}
	
	public void close(){
		this.removePlugin();
	}
	
	protected void addPlugin(){
		if(!this.loaded){
			TGFileFormatManager.instance().addInputStream(this.stream);
			this.loaded = true;
		}
	}
	
	protected void removePlugin(){		
		if(this.loaded){
			TGFileFormatManager.instance().removeInputStream(this.stream);
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

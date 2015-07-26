package org.herac.tuxguitar.tray;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;

public class TGTrayPlugin implements TGPlugin {
	
	public static final String MODULE_ID = "tuxguitar-tray";
	
	private boolean loaded;
	private TGTray tray;
	
	public TGTrayPlugin(){
		super();
	}
	
	public void init(TGContext context) {
		this.tray = new TGTray(context);
	}
	
	public void close() {
		this.removePlugin();
	}
	
	public void setEnabled(boolean enabled) {
		if(enabled){
			this.addPlugin();
		}else{
			this.removePlugin();
		}
	}
	
	protected void addPlugin(){
		if(!this.loaded){
			this.tray.addTray();
			this.loaded = true;
		}
	}
	
	protected void removePlugin(){
		if(this.loaded){
			this.tray.removeTray();
			this.loaded = false;
		}
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}

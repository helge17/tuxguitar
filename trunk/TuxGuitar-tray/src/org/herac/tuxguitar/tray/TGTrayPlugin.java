package org.herac.tuxguitar.tray;

import org.herac.tuxguitar.gui.system.plugins.base.TGPluginAdapter;

public class TGTrayPlugin extends TGPluginAdapter {
	
	private boolean loaded;
	private TGTray tray;
	
	public TGTrayPlugin(){
		super();
	}
	
	public void init() {
		this.tray = new TGTray();
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
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getName() {
		return "System Tray plugin";
	}
	
	public String getDescription() {
		return "System Tray plugin for tuxguitar";
	}
	
	public String getVersion() {
		return "1.0";
	}
}

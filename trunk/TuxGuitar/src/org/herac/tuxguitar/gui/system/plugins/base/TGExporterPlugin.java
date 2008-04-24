package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGSongExporter;

public abstract class TGExporterPlugin extends TGPluginAdapter{
	
	private boolean loaded;
	private TGSongExporter exporter;
	
	protected abstract TGSongExporter getExporter();
	
	public void init(){
		this.exporter = getExporter();
	}
	
	public void close(){
		this.removePlugin();
	}
	
	protected void addPlugin(){
		if(!this.loaded){
			TGFileFormatManager.instance().addExporter(this.exporter);
			TuxGuitar.instance().getItemManager().createMenu();
			this.loaded = true;
		}
	}
	
	protected void removePlugin(){
		if(this.loaded){
			TGFileFormatManager.instance().removeExporter(this.exporter);
			TuxGuitar.instance().getItemManager().createMenu();
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

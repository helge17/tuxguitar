package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGSongExporter;

public abstract class TGExporterPlugin extends TGPluginAdapter{
	
	private boolean loaded;
	private TGSongExporter exporter;
	
	protected abstract TGSongExporter getExporter() throws TGPluginException;
	
	public void init() throws TGPluginException {
		this.exporter = getExporter();
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
		if(!this.loaded && this.exporter != null){
			TGFileFormatManager.instance().addExporter(this.exporter);
			TuxGuitar.instance().getItemManager().createMenu();
			this.loaded = true;
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded && this.exporter != null){
			TGFileFormatManager.instance().removeExporter(this.exporter);
			TuxGuitar.instance().getItemManager().createMenu();
			this.loaded = false;
		}
	}	
}

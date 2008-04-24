package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGSongImporter;

public abstract class TGImporterPlugin extends TGPluginAdapter{
	
	private boolean loaded;
	private TGSongImporter importer;
	
	protected abstract TGSongImporter getImporter();
	
	public void init(){
		this.importer = getImporter();
	}
	
	public void close(){
		this.removePlugin();
	}
	
	protected void addPlugin(){
		if(!this.loaded){
			TGFileFormatManager.instance().addImporter(this.importer);
			TuxGuitar.instance().getItemManager().createMenu();
			this.loaded = true;
		}
	}
	
	protected void removePlugin(){
		if(this.loaded){
			TGFileFormatManager.instance().removeImporter(this.importer);
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

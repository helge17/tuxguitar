package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGRawImporter;

public abstract class TGImporterPlugin extends TGPluginAdapter{
	
	private boolean loaded;
	private TGRawImporter importer;
	
	protected abstract TGRawImporter getImporter() throws TGPluginException;
	
	public void init() throws TGPluginException {
		this.importer = getImporter();
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
			TGFileFormatManager.instance().addImporter(this.importer);
			TuxGuitar.instance().getItemManager().createMenu();
			this.loaded = true;
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded){
			TGFileFormatManager.instance().removeImporter(this.importer);
			TuxGuitar.instance().getItemManager().createMenu();
			this.loaded = false;
		}
	}
}

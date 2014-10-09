package org.herac.tuxguitar.io.plugin;

import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGRawImporter;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGImporterPlugin implements TGPlugin{
	
	private boolean loaded;
	private TGRawImporter importer;
	
	protected abstract TGRawImporter getImporter() throws TGPluginException;
	
	public void init(TGContext context) throws TGPluginException {
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
			this.loaded = true;
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded){
			TGFileFormatManager.instance().removeImporter(this.importer);
			this.loaded = false;
		}
	}
}

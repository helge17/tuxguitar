package org.herac.tuxguitar.io.plugin;

import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGExporterPlugin implements TGPlugin{
	
	private boolean loaded;
	private TGRawExporter exporter;
	
	protected abstract TGRawExporter getExporter() throws TGPluginException;
	
	public void init(TGContext context) throws TGPluginException {
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
			this.loaded = true;
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded && this.exporter != null){
			TGFileFormatManager.instance().removeExporter(this.exporter);
			this.loaded = false;
		}
	}	
}

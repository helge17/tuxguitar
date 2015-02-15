package org.herac.tuxguitar.io.plugin;

import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGExporterPlugin implements TGPlugin{
	
	private boolean loaded;
	private TGContext context;
	private TGRawExporter exporter;
	
	protected abstract TGRawExporter getExporter() throws TGPluginException;
	
	public void init(TGContext context) throws TGPluginException {
		this.context = context;
		this.exporter = getExporter();
	}
	
	public TGContext getContext() {
		return this.context;
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
			TGFileFormatManager.getInstance(this.context).addExporter(this.exporter);
			this.loaded = true;
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded && this.exporter != null){
			TGFileFormatManager.getInstance(this.context).removeExporter(this.exporter);
			this.loaded = false;
		}
	}	
}

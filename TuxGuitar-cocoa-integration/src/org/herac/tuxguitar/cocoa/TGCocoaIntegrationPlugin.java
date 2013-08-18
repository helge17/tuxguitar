package org.herac.tuxguitar.cocoa;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.system.plugins.base.TGPluginList;
import org.herac.tuxguitar.cocoa.menu.MacMenuPlugin;
import org.herac.tuxguitar.cocoa.modifiedmarker.ModifiedMarkerPlugin;
import org.herac.tuxguitar.cocoa.opendoc.OpenDocPlugin;
import org.herac.tuxguitar.cocoa.toolbar.MacToolbarPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGCocoaIntegrationPlugin extends TGPluginList {
	
	public static final String MODULE_ID = "tuxguitar-cocoa-integration";
	
	private List plugins; 
	
	protected List getPlugins() throws TGPluginException {
		if( this.plugins == null ){
			this.plugins = new ArrayList();
			
			this.plugins.add(new OpenDocPlugin());
			this.plugins.add(new MacMenuPlugin());
			this.plugins.add(new MacToolbarPlugin());
			this.plugins.add(new ModifiedMarkerPlugin());
		}
		return this.plugins;
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}

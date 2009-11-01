package org.herac.tuxguitar.cocoa;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.cocoa.menu.MacMenuPlugin;
import org.herac.tuxguitar.cocoa.opendoc.OpenDocPlugin;
import org.herac.tuxguitar.cocoa.toolbar.MacToolbarPlugin;
import org.herac.tuxguitar.cocoa.modifiedmarker.ModifiedMarkerPlugin;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.base.TGPluginList;

public class TGCocoaIntegrationPlugin extends TGPluginList {
	
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
	
	public String getAuthor() {
		return "Auria & Julian Casadesus";
	}
	
	public String getDescription() {
		return "Cocoa Integration Plugin";
	}
	
	public String getName() {
		return "Cocoa Integration Plugin";
	}
	
	public String getVersion() {
		return "1.0";
	}
}

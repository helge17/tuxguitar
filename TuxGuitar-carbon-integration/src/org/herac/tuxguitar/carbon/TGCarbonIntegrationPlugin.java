package org.herac.tuxguitar.carbon;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.carbon.menu.MacMenuPlugin;
import org.herac.tuxguitar.carbon.opendoc.OpenDocPlugin;
import org.herac.tuxguitar.gui.system.plugins.base.TGPluginList;

public class TGCarbonIntegrationPlugin extends TGPluginList {

	private List plugins; 
	
	protected List getPlugins() {
		if( this.plugins == null ){
			this.plugins = new ArrayList();
			
			this.plugins.add(new OpenDocPlugin());
			this.plugins.add(new MacMenuPlugin());
		}
		return this.plugins;
	}
	
	

}

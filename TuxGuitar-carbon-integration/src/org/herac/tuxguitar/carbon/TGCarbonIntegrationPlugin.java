package org.herac.tuxguitar.carbon;

import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.base.TGPluginAdapter;

public class TGCarbonIntegrationPlugin extends TGPluginAdapter {
	
	public void close() throws TGPluginException {
		// TODO Auto-generated method stub
	}

	public void init() throws TGPluginException {
		// TODO Auto-generated method stub
	}

	public void setEnabled(boolean enabled) throws TGPluginException {
		// TODO Auto-generated method stub
		if( enabled ){
			new org.herac.tuxguitar.carbon.opendoc.OpenDocListener();
		}
	}

}

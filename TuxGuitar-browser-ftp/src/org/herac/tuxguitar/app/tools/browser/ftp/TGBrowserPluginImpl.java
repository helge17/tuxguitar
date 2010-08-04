package org.herac.tuxguitar.app.tools.browser.ftp;

import org.herac.tuxguitar.app.system.plugins.base.TGBrowserPlugin;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;

public class TGBrowserPluginImpl extends TGBrowserPlugin {
	
	protected TGBrowserFactory getFactory() {
		return new TGBrowserFactoryImpl();
	}
	
	public String getName() {
		return "FTP Plugin for TGBrowser";
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getDescription() {
		return "FTP Plugin for TGBrowser";
	}
	
	public String getVersion() {
		return "1.0";
	}
}

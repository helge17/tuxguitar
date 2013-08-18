package org.herac.tuxguitar.jack.singleton;

import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.jack.JackPlugin;
import org.herac.tuxguitar.jack.settings.JackSettings;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class JackSingletonPlugin implements TGPlugin {
	
	public static final String MODULE_ID = "tuxguitar-jack";
	
	private JackClient jackClient;
	private JackSettings jackSettings;
	
	public JackSingletonPlugin(){
		super();
	}
	
	public void init() throws TGPluginException {
		this.jackClient = new JackClient();
		this.jackSettings = new JackSettings();	
	}
	
	public void close(){
		if( this.jackClient != null ){
			if( this.jackClient.isOpen() ){
				this.jackClient.close();
			}
			this.jackClient.finalize();
		}
		this.jackClient = null;
		this.jackSettings = null;
	}
	
	public JackClient getJackClient() {
		return this.jackClient;
	}

	public JackSettings getJackSettings() {
		return this.jackSettings;
	}

	public void setEnabled(boolean enabled) throws TGPluginException {
		// Not implemented
	}
	
	public String getModuleId() {
		return JackPlugin.MODULE_ID;
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getDescription() {
		return "Jack Audio Connection Kit plugin support";
	}
	
	public String getName() {
		return "Jack Audio Connection Kit plugin support";
	}
	
	public String getVersion() {
		return "1.0";
	}
}

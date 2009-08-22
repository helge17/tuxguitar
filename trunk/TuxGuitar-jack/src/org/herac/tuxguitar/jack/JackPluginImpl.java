package org.herac.tuxguitar.jack;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.base.TGPluginList;
import org.herac.tuxguitar.jack.sequencer.JackSequencerProviderPlugin;
import org.herac.tuxguitar.jack.synthesizer.JackOutputPortProviderPlugin;

public class JackPluginImpl extends TGPluginList {
	
	private JackClient jackClient;
	
	public JackPluginImpl(){
		this.jackClient = new JackClient();
	}
	
	protected List getPlugins() throws TGPluginException {
		List plugins = new ArrayList();
		plugins.add( new JackOutputPortProviderPlugin(this.jackClient) );
		plugins.add( new JackSequencerProviderPlugin(this.jackClient) );
		return plugins;
	}
	
	public void closeAll(){
		if(this.jackClient.isOpen()){
			this.jackClient.close();
			this.jackClient.finalize();
		}
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

package org.herac.tuxguitar.jack.singleton;

import java.util.List;

import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.jack.provider.JackClientProvider;
import org.herac.tuxguitar.util.plugin.TGPluginManager;

public class JackClientInstanceProvider implements JackClientProvider {

	public JackClient getJackClient() {
		JackSingletonPlugin plugin = findSingletonPlugin();
		if( plugin != null ){
			return plugin.getJackClient();
		}
		return null;
	}
	
	private JackSingletonPlugin findSingletonPlugin(){
		List pluginInstances = TGPluginManager.getInstance().getPluginInstances(JackSingletonPlugin.class);
		if( pluginInstances != null && !pluginInstances.isEmpty() ){
			return ((JackSingletonPlugin)pluginInstances.get(0));
		}
		return null;
	}
}

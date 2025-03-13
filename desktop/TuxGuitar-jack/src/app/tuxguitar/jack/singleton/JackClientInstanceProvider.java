package app.tuxguitar.jack.singleton;

import java.util.List;

import app.tuxguitar.jack.JackClient;
import app.tuxguitar.jack.provider.JackClientProvider;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginManager;

public class JackClientInstanceProvider implements JackClientProvider {

	private TGContext context;

	public JackClientInstanceProvider(TGContext context) {
		this.context = context;
	}

	public JackClient getJackClient() {
		JackSingletonPlugin plugin = findSingletonPlugin();
		if( plugin != null ){
			return plugin.getJackClient();
		}
		return null;
	}

	private JackSingletonPlugin findSingletonPlugin(){
		List<JackSingletonPlugin> pluginInstances = TGPluginManager.getInstance(this.context).getPluginInstances(JackSingletonPlugin.class);
		if( pluginInstances != null && !pluginInstances.isEmpty() ){
			return pluginInstances.get(0);
		}
		return null;
	}
}

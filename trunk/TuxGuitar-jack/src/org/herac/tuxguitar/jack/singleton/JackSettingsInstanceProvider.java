package org.herac.tuxguitar.jack.singleton;

import java.util.List;

import org.herac.tuxguitar.jack.provider.JackSettingsProvider;
import org.herac.tuxguitar.jack.settings.JackSettings;
import org.herac.tuxguitar.util.plugin.TGPluginManager;

public class JackSettingsInstanceProvider implements JackSettingsProvider {

	public JackSettings getJackSettings() {
		JackSingletonPlugin plugin = findSingletonPlugin();
		if( plugin != null ){
			return plugin.getJackSettings();
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

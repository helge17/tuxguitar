package org.herac.tuxguitar.jack.settings;

import org.herac.tuxguitar.util.configuration.TGConfigManager;

public interface JackSettingsListener {
	
	public void loadSettings( TGConfigManager config );
	
}

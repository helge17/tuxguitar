package org.herac.tuxguitar.jack.settings;

import org.herac.tuxguitar.app.system.config.TGConfigManager;

public interface JackSettingsListener {
	
	public void loadSettings( TGConfigManager config );
	
}

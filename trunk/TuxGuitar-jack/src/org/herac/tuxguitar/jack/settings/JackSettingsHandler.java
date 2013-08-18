package org.herac.tuxguitar.jack.settings;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.jack.provider.JackSettingsProvider;

public class JackSettingsHandler implements TGPluginSettingsHandler {
	
	private JackSettingsProvider jackSettingsProvider;
	
	public JackSettingsHandler(JackSettingsProvider jackSettingsProvider){
		this.jackSettingsProvider = jackSettingsProvider;
	}
	
	public void openSettingsDialog(Shell parent) {
		JackSettings jackSettings = this.jackSettingsProvider.getJackSettings();
		if( jackSettings != null ){
			JackSettingsDialog jackSettingsDialog = new JackSettingsDialog( jackSettings );
			jackSettingsDialog.open( parent );
		}
	}
}

package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class ABCImportSettingsHandler implements TGSongStreamSettingsHandler {

	public String getProviderId() {
		return ABCSongImporter.PROVIDER_ID;
	}

	public void handleSettings(final TGSongStreamContext context, final Runnable callback) {
		TGSynchronizer.instance().executeLater(new Runnable() {
			public void run() {
				ABCSettings settings = new ABCImportSettingsDialog().open();
				if( settings != null ) {
					context.setAttribute(ABCSettings.class.getName(), settings);
					callback.run();
				}
			}
		});
	}	
}

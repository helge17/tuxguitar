package org.herac.tuxguitar.app.system.error;

import org.herac.tuxguitar.util.error.TGErrorManager;

public class TGErrorAdapter {
	
	public static void initialize() {
		TGErrorManager.getInstance().addErrorHandler(new TGGuiOutErrorHandler());
		TGErrorManager.getInstance().addErrorHandler(new TGStdOutErrorHandler());
	}
}

package org.herac.tuxguitar.app.system.error;

import org.herac.tuxguitar.util.error.TGErrorHandler;

public class TGStdOutErrorHandler implements TGErrorHandler {
	
	public void handleError(Throwable throwable) {
		throwable.printStackTrace();
	}
}

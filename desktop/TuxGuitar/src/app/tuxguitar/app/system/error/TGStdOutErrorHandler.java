package app.tuxguitar.app.system.error;

import app.tuxguitar.util.error.TGErrorHandler;

public class TGStdOutErrorHandler implements TGErrorHandler {

	public void handleError(Throwable throwable) {
		throwable.printStackTrace();
	}
}

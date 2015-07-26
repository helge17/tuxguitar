package org.herac.tuxguitar.app.system.error;

import org.herac.tuxguitar.app.util.MessageDialog;
import org.herac.tuxguitar.util.error.TGErrorHandler;

public class TGGuiOutErrorHandler implements TGErrorHandler {
	
	public void handleError(Throwable throwable) {
		MessageDialog.errorMessage(throwable);
	}
}

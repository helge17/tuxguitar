package org.herac.tuxguitar.app.system.error;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class TGErrorAdapter {
	
	public static void initialize(TGContext context) {
		TGErrorManager.getInstance(context).addErrorHandler(new TGGuiOutErrorHandler(context));
		TGErrorManager.getInstance(context).addErrorHandler(new TGStdOutErrorHandler());
	}
}

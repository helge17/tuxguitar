package app.tuxguitar.app.system.error;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.error.TGErrorManager;

public class TGErrorAdapter {

	public static void initialize(TGContext context) {
		TGErrorManager.getInstance(context).addErrorHandler(new TGGuiOutErrorHandler(context));
		TGErrorManager.getInstance(context).addErrorHandler(new TGStdOutErrorHandler());
	}
}

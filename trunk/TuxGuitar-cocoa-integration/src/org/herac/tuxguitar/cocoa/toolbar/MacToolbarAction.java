package org.herac.tuxguitar.cocoa.toolbar;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class MacToolbarAction {
	
	protected static void toogleToolbar(){
		try {
			TGSynchronizer.instance().executeLater(new TGSynchronizer.TGRunnable() {
				public void run() throws TGException {
					TuxGuitar.getInstance().getItemManager().toogleToolbarVisibility();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

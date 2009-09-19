package org.herac.tuxguitar.cocoa.toolbar;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.util.TGSynchronizer;

public class MacToolbarAction {
	
	protected static void toogleToolbar(){
		try {
			TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
				public void run() throws Throwable {
					TuxGuitar.instance().getItemManager().toogleToolbarVisibility();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

package org.herac.tuxguitar.android.drawer;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionInterceptor;
import org.herac.tuxguitar.android.action.impl.gui.TGBackAction;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGDrawerActionInterceptor implements TGActionInterceptor {
	
	private TGDrawerManager drawerManager;
	
	public TGDrawerActionInterceptor(TGDrawerManager drawerManager) {
		this.drawerManager = drawerManager;
	}
	
	@Override
	public boolean intercept(String id, TGActionContext context) throws TGActionException {
		if( TGBackAction.NAME.equals(id) ) {
			if( this.drawerManager.isOpen() ) {
				this.closeDrawerLater();
				
				return true;
			}
		}
		return false;
	}
	
	public void closeDrawerLater() {
		TGSynchronizer.getInstance(this.drawerManager.findContext()).executeLater(new Runnable() {
			public void run() throws TGException {
				TGDrawerActionInterceptor.this.drawerManager.closeDrawer();
			}
		});
	}
}

package app.tuxguitar.android.drawer;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionInterceptor;
import app.tuxguitar.android.action.impl.gui.TGBackAction;
import app.tuxguitar.util.TGException;
import app.tuxguitar.util.TGSynchronizer;

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

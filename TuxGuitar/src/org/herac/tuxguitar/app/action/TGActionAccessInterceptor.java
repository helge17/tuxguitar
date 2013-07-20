package org.herac.tuxguitar.app.action;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionInterceptor;
import org.herac.tuxguitar.app.TuxGuitar;

public class TGActionAccessInterceptor implements TGActionInterceptor {
	
	private Object lock;
	private Thread lockThreadAccess;
	private TGActionAdapterManager manager;
	
	public TGActionAccessInterceptor(TGActionAdapterManager manager){
		this.lock = new Object();
		this.lockThreadAccess = null;
		this.manager = manager;
	}
	
	public boolean intercept(String id, TGActionContext context) throws TGActionException {
		synchronized( this.lock ) {
			boolean intercepted = true;
			
			if(!TuxGuitar.instance().isLocked()){
				if(!TGActionLock.isLocked() || hastLockAccessToThread(Thread.currentThread())){
					boolean running = TuxGuitar.instance().getPlayer().isRunning();
					if(!running || !this.manager.getDisableOnPlayingActionIds().hasActionId(id)){
						intercepted = false;
					}
				}
			}
			
			this.lockThreadAccess = null;
			
			if( intercepted ){
				TuxGuitar.instance().updateCache( this.manager.getAutoUpdateActionIds().hasActionId(id) );
			}
			
			return intercepted;
		}
	}
	
	public void grantLockAccessToThread() {
		synchronized( this.lock ) {
			this.lockThreadAccess = Thread.currentThread();
		}
	}
	
	public boolean hastLockAccessToThread(Thread thread){
		synchronized( this.lock ){
			return (this.lockThreadAccess != null && this.lockThreadAccess != thread);
		}
	}
}

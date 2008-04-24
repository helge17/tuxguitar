/*
 * Created on 09-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.helper;

import org.herac.tuxguitar.gui.TuxGuitar;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class SyncThread extends Thread {
	
	private Runnable runnable;
	
	public SyncThread(Runnable runnable) {
		this.runnable = runnable;
	}
	
	public void run() {
		if(!TuxGuitar.isDisposed()){
			TuxGuitar.instance().getDisplay().syncExec(this.runnable);
		}
	}
}
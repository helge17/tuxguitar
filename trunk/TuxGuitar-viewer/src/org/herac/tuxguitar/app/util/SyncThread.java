/*
 * Created on 09-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.util;

import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class SyncThread extends Thread {
	
	private TGSynchronizer.TGRunnable runnable;
	
	public SyncThread(TGSynchronizer.TGRunnable runnable) {
		this.runnable = runnable;
	}
	
	public SyncThread(final Runnable runnable) {
		this(new TGSynchronizer.TGRunnable() {
			public void run() throws TGException{
				runnable.run();
			}
		});
	}
	
	public void run() {
		try {
			TGSynchronizer.instance().execute(this.runnable);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
/*
 * Created on 09-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.helper;

import org.eclipse.swt.widgets.Display;
import org.herac.tuxguitar.gui.TuxGuitar;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class AsyncThread extends Thread {
	private Display display;
	private Runnable runnable;
	
	public AsyncThread(Runnable runnable) {
		this.display = TuxGuitar.instance().getDisplay();
		this.runnable = runnable;
	}
	
	public void run() {
		if(!this.display.isDisposed()){
			this.display.asyncExec(this.runnable);
		}
	}
}
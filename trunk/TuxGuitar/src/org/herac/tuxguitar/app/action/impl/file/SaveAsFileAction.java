/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.file;

import org.eclipse.swt.SWT;
import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.action.TGActionLock;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveAsFileAction extends TGActionBase {
	
	public static final String NAME = "action.file.save-as";
	
	public SaveAsFileAction() {
		super(NAME, AUTO_LOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE );
	}
	
	protected void processAction(TGActionContext context){
		final String fileName = FileActionUtils.chooseFileName();
		if( fileName == null ){
			TGActionLock.unlock();
			return;
		}
		TuxGuitar.getInstance().loadCursor(SWT.CURSOR_WAIT);
		new Thread(new Runnable() {
			public void run() {
				if(!TuxGuitar.isDisposed()){
					FileActionUtils.save(fileName);
					TuxGuitar.getInstance().loadCursor(SWT.CURSOR_ARROW);
					TGActionLock.unlock();
				}
			}
		}).start();
	}
}

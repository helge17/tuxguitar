package org.herac.tuxguitar.cocoa.opendoc;

import org.eclipse.swt.SWT;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.actions.file.FileActionUtils;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.util.ConfirmDialog;

public class OpenDocAction {
	
	public static void saveAndOpen(final String file){
		ActionLock.lock();
		
		TuxGuitar.instance().getPlayer().reset();
		
		if(TuxGuitar.instance().getFileHistory().isUnsavedFile()){
			ConfirmDialog confirm = new ConfirmDialog(TuxGuitar.getProperty("file.save-changes-question"));
			confirm.setDefaultStatus( ConfirmDialog.STATUS_CANCEL );
			int status = confirm.confirm(ConfirmDialog.BUTTON_YES | ConfirmDialog.BUTTON_NO | ConfirmDialog.BUTTON_CANCEL, ConfirmDialog.BUTTON_YES);
			if(status == ConfirmDialog.STATUS_CANCEL){
				ActionLock.unlock();
				return;
			}
			if(status == ConfirmDialog.STATUS_YES){
				final String fileName = FileActionUtils.getFileName();
				if(fileName == null){
					ActionLock.unlock();
					return;
				}
				TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
				new Thread(new Runnable() {
					public void run() {
						if(!TuxGuitar.isDisposed()){
							FileActionUtils.save(fileName);
							new SyncThread(new Runnable() {
								public void run() {
									if(!TuxGuitar.isDisposed()){
										TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
										openFile( file );
									}
								}
							}).start();
						}
					}
				}).start();
				return;
			}
		}
		openFile(file );
	}
	
	protected static void openFile(final String url){
		if(url == null){
			ActionLock.unlock();
			return;
		}
		TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
		new Thread(new Runnable() {
			public void run() {
				if(!TuxGuitar.isDisposed()){
					FileActionUtils.open(url);
					TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
					ActionLock.unlock();
				}
			}
		}).start();
	}
}

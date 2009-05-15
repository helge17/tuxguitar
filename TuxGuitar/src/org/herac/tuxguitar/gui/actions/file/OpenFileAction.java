/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.file;

import java.io.File;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.util.ConfirmDialog;
import org.herac.tuxguitar.gui.util.FileChooser;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class OpenFileAction extends Action {
	public static final String NAME = "action.file.open";
	
	public OpenFileAction() {
		super(NAME, AUTO_LOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(final TypedEvent event){
		TuxGuitar.instance().getPlayer().reset();
		
		final Object data = event.widget.getData();
		if(TuxGuitar.instance().getFileHistory().isUnsavedFile()){
			ConfirmDialog confirm = new ConfirmDialog(TuxGuitar.getProperty("file.save-changes-question"));
			confirm.setDefaultStatus( ConfirmDialog.STATUS_CANCEL );
			int status = confirm.confirm(ConfirmDialog.BUTTON_YES | ConfirmDialog.BUTTON_NO | ConfirmDialog.BUTTON_CANCEL, ConfirmDialog.BUTTON_YES);
			if(status == ConfirmDialog.STATUS_CANCEL){
				return AUTO_UNLOCK;
			}
			if(status == ConfirmDialog.STATUS_YES){
				final String fileName = FileActionUtils.getFileName();
				if(fileName == null){
					return AUTO_UNLOCK;
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
										openFile( data );
									}
								}
							}).start();
						}
					}
				}).start();
				return 0;
			}
		}
		openFile( data );
		
		return 0;
	}
	
	protected void openFile(Object data){
		final URL url = getOpenFileName(data);
		if(url == null){
			ActionLock.unlock();
			return;
		}
		TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
		try {
			TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
				public void run() throws Throwable {
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
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	protected URL getOpenFileName(Object data){
		try{
			if(data instanceof URL){
				TuxGuitar.instance().getFileHistory().setChooserPath( (URL)data );
				return (URL)data;
			}
			String path = FileChooser.instance().open(TuxGuitar.instance().getShell(),TGFileFormatManager.instance().getInputFormats());
			if(path != null){
				File file = new File(path);
				if( file.exists() && file.isFile() ){
					return file.toURI().toURL();
				}
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}
}
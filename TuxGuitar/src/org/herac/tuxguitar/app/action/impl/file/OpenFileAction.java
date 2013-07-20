/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.file;

import java.io.File;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.action.TGActionLock;
import org.herac.tuxguitar.app.helper.SyncThread;
import org.herac.tuxguitar.app.util.ConfirmDialog;
import org.herac.tuxguitar.app.util.FileChooser;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class OpenFileAction extends TGActionBase {
	
	public static final String NAME = "action.file.open";
	
	public static final String PROPERTY_URL = "url";
	
	public OpenFileAction() {
		super(NAME, AUTO_LOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		final Object propertyUrl = context.getAttribute(PROPERTY_URL);
		
		TuxGuitar.instance().getPlayer().reset();
		
		if(TuxGuitar.instance().getFileHistory().isUnsavedFile()){
			ConfirmDialog confirm = new ConfirmDialog(TuxGuitar.getProperty("file.save-changes-question"));
			confirm.setDefaultStatus( ConfirmDialog.STATUS_CANCEL );
			int status = confirm.confirm(ConfirmDialog.BUTTON_YES | ConfirmDialog.BUTTON_NO | ConfirmDialog.BUTTON_CANCEL, ConfirmDialog.BUTTON_YES);
			if( status == ConfirmDialog.STATUS_CANCEL ){
				TGActionLock.unlock();
				return;
			}
			if(status == ConfirmDialog.STATUS_YES){
				final String fileName = FileActionUtils.getFileName();
				if( fileName == null ){
					TGActionLock.unlock();
					return;
				}
				TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
				new Thread(new Runnable() {
					public void run() throws TGException {
						if(!TuxGuitar.isDisposed()){
							FileActionUtils.save(fileName);
							new SyncThread(new Runnable() {
								public void run() throws TGException {
									if(!TuxGuitar.isDisposed()){
										TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
										openFile( propertyUrl );
									}
								}
							}).start();
						}
					}
				}).start();
				return;
			}
		}
		openFile( propertyUrl );
	}
	
	protected void openFile(Object data){
		final URL url = getOpenFileName(data);
		if(url == null){
			TGActionLock.unlock();
			return;
		}
		TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
		try {
			TGSynchronizer.instance().executeLater(new TGSynchronizer.TGRunnable() {
				public void run() throws TGException {
					new Thread(new Runnable() {
						public void run() throws TGException {
							if(!TuxGuitar.isDisposed()){
								FileActionUtils.open(url);
								TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
								TGActionLock.unlock();
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
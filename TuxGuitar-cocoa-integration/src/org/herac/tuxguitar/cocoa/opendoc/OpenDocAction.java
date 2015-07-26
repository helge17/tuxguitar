package org.herac.tuxguitar.cocoa.opendoc;

import java.io.File;
import java.net.MalformedURLException;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

public class OpenDocAction {
	
	public static void saveAndOpen(final String file){
		try {
			TuxGuitar.getInstance().getPlayer().reset();
			
			TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(), TGReadURLAction.NAME);
			tgActionProcessor.setAttribute(TGReadURLAction.ATTRIBUTE_URL, new File(file).toURI().toURL());
			tgActionProcessor.process();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
//	public static void saveAndOpen(final String file){
//		
//		TGActionLock.lock();
//		
//		TuxGuitar.getInstance().getPlayer().reset();
//		
//		if(TuxGuitar.getInstance().getFileHistory().isUnsavedFile()){
//			ConfirmDialog confirm = new ConfirmDialog(TuxGuitar.getProperty("file.save-changes-question"));
//			confirm.setDefaultStatus( ConfirmDialog.STATUS_CANCEL );
//			int status = confirm.confirm(ConfirmDialog.BUTTON_YES | ConfirmDialog.BUTTON_NO | ConfirmDialog.BUTTON_CANCEL, ConfirmDialog.BUTTON_YES);
//			if(status == ConfirmDialog.STATUS_CANCEL){
//				TGActionLock.unlock();
//				return;
//			}
//			if(status == ConfirmDialog.STATUS_YES){
//				final String fileName = TGFileFormatUtils.getFileName();
//				if(fileName == null){
//					TGActionLock.unlock();
//					return;
//				}
//				TuxGuitar.getInstance().loadCursor(SWT.CURSOR_WAIT);
//				new Thread(new Runnable() {
//					public void run() {
//						if(!TuxGuitar.isDisposed()){
//							TGFileFormatUtils.save(fileName);
//							new SyncThread(new Runnable() {
//								public void run() {
//									if(!TuxGuitar.isDisposed()){
//										TuxGuitar.getInstance().loadCursor(SWT.CURSOR_ARROW);
//										openFile( file );
//									}
//								}
//							}).start();
//						}
//					}
//				}).start();
//				return;
//			}
//		}
//		openFile(file );
//	}
//	
//	protected static void openFile(final String url){
//		if(url == null){
//			TGActionLock.unlock();
//			return;
//		}
//		TuxGuitar.getInstance().loadCursor(SWT.CURSOR_WAIT);
//		new Thread(new Runnable() {
//			public void run() {
//				if(!TuxGuitar.isDisposed()){
//					TGFileFormatUtils.open(url);
//					TuxGuitar.getInstance().loadCursor(SWT.CURSOR_ARROW);
//					TGActionLock.unlock();
//				}
//			}
//		}).start();
//	}
}

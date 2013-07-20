/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.file;

import java.io.File;

import org.eclipse.swt.SWT;
import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.action.TGActionLock;
import org.herac.tuxguitar.app.helper.SyncThread;
import org.herac.tuxguitar.app.util.ConfirmDialog;
import org.herac.tuxguitar.app.util.FileChooser;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.io.base.TGRawImporter;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ImportSongAction extends TGActionBase {
	
	public static final String NAME = "action.file.import";
	
	public static final String PROPERTY_IMPORTER = "importer";
	
	public ImportSongAction() {
		super(NAME, AUTO_LOCK | AUTO_UPDATE);
	}
	
	protected void processAction(TGActionContext context){
		final Object propertyImporter = context.getAttribute(PROPERTY_IMPORTER);
		
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
					public void run() {
						if(!TuxGuitar.isDisposed()){
							FileActionUtils.save(fileName);
							new SyncThread(new Runnable() {
								public void run() {
									if(!TuxGuitar.isDisposed()){
										TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
										processImporter(propertyImporter);
									}
								}
							}).start();
						}
					}
				}).start();
				return;
			}
		}
		processImporter(propertyImporter);
	}
	
	protected void processImporter(Object importer){
		if( importer instanceof TGLocalFileImporter ){
			this.processLocalFileImporter( (TGLocalFileImporter)importer );
		}else if( importer instanceof TGRawImporter ){
			this.processRawImporter( (TGRawImporter)importer );
		}
	}
	
	private void processLocalFileImporter(final TGLocalFileImporter importer){
		final String path = FileChooser.instance().open(TuxGuitar.instance().getShell(),importer.getFileFormat());
		if(!isValidFile(path) || !importer.configure(false)){
			TGActionLock.unlock();
			return;
		}
		
		TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
		new Thread(new Runnable() {
			public void run() {
				if(!TuxGuitar.isDisposed()){
					FileActionUtils.importSong(importer, path);
					TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
					TGActionLock.unlock();
				}
			}
		}).start();
	}
	
	private void processRawImporter( final TGRawImporter importer ){
		TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
		new Thread(new Runnable() {
			public void run() {
				if(!TuxGuitar.isDisposed()){
					FileActionUtils.importSong(importer);
					TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
					TGActionLock.unlock();
				}
			}
		}).start();
	}
	
	protected boolean isValidFile( String path ){
		if( path != null ){
			File file = new File( path );
			return ( file.exists() && file.isFile() );
		}
		return false;
	}
}

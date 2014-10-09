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
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGRawExporter;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ExportSongAction extends TGActionBase {
	
	public static final String NAME = "action.file.export";
	
	public static final String PROPERTY_EXPORTER = "exporter";
	
	public ExportSongAction() {
		super(NAME, AUTO_LOCK | AUTO_UPDATE );
	}
	
	protected void processAction(TGActionContext context){
		Object propertyExporter = context.getAttribute(PROPERTY_EXPORTER);
		if(!(propertyExporter instanceof TGRawExporter) ){
			TGActionLock.unlock();
			return;
		}
		
		final TGRawExporter exporter = (TGRawExporter)propertyExporter;
		if( exporter instanceof TGLocalFileExporter ){
			this.processLocalFileExporter( (TGLocalFileExporter)exporter );
			return;
		}
		this.processRawExporter( exporter );
	}
	
	private void processLocalFileExporter( final TGLocalFileExporter exporter ){
		if(!exporter.configure(false)){
			TGActionLock.unlock();
			return;
		}
		
		final String fileName = FileActionUtils.chooseFileName(exporter.getFileFormat());
		if( fileName == null ){
			TGActionLock.unlock();
			return;
		}
		
		TuxGuitar.getInstance().loadCursor(SWT.CURSOR_WAIT);
		new Thread(new Runnable() {
			public void run() {
				if(!TuxGuitar.isDisposed()){
					FileActionUtils.exportSong(exporter, fileName);
					TuxGuitar.getInstance().loadCursor(SWT.CURSOR_ARROW);
					TGActionLock.unlock();
				}
			}
		}).start();
	}
	
	private void processRawExporter( final TGRawExporter exporter ){
		TuxGuitar.getInstance().loadCursor(SWT.CURSOR_WAIT);
		new Thread(new Runnable() {
			public void run() {
				if(!TuxGuitar.isDisposed()){
					FileActionUtils.exportSong(exporter);
					TuxGuitar.getInstance().loadCursor(SWT.CURSOR_ARROW);
					TGActionLock.unlock();
				}
			}
		}).start();
	}
}

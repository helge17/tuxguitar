/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.file;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGRawExporter;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ExportSongAction extends Action {
	public static final String NAME = "action.file.export";
	
	public ExportSongAction() {
		super(NAME, AUTO_LOCK | AUTO_UPDATE );
	}
	
	protected int execute(TypedEvent e){
		Object data = e.widget.getData(); 
		if(! (data instanceof TGRawExporter) ){
			return AUTO_UNLOCK;
		}
		
		final TGRawExporter exporter = (TGRawExporter)data;
		if( exporter instanceof TGLocalFileExporter ){
			return this.processLocalFileExporter( (TGLocalFileExporter)exporter );
		}
		return this.processRawExporter( exporter );
		/*
		if(!exporter.configure(false)){
			return AUTO_UNLOCK;
		}
		
		final String fileName = FileActionUtils.chooseFileName(exporter.getFileFormat());
		if(fileName == null){
			return AUTO_UNLOCK;
		}
		
		TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
		new Thread(new Runnable() {
			public void run() {
				if(!TuxGuitar.isDisposed()){
					FileActionUtils.exportSong(exporter, fileName);
					TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
					ActionLock.unlock();
				}
			}
		}).start();
		
		return 0;
		*/
	}
	
	private int processLocalFileExporter( final TGLocalFileExporter exporter ){
		if(!exporter.configure(false)){
			return AUTO_UNLOCK;
		}
		
		final String fileName = FileActionUtils.chooseFileName(exporter.getFileFormat());
		if(fileName == null){
			return AUTO_UNLOCK;
		}
		
		TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
		new Thread(new Runnable() {
			public void run() {
				if(!TuxGuitar.isDisposed()){
					FileActionUtils.exportSong(exporter, fileName);
					TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
					ActionLock.unlock();
				}
			}
		}).start();
		
		return 0;
	}
	
	private int processRawExporter( final TGRawExporter exporter ){
		TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
		new Thread(new Runnable() {
			public void run() {
				if(!TuxGuitar.isDisposed()){
					FileActionUtils.exportSong(exporter);
					TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
					ActionLock.unlock();
				}
			}
		}).start();
		
		return 0;
	}
}

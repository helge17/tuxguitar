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
import org.herac.tuxguitar.app.helper.SyncThread;
import org.herac.tuxguitar.app.tools.template.TGTemplate;
import org.herac.tuxguitar.app.util.ConfirmDialog;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NewFileAction extends TGActionBase{
	
	public static final String NAME = "action.file.new";
	
	public static final String PROPERTY_TEMPLATE = "template";
	
	public NewFileAction() {
		super(NAME, AUTO_LOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		final Object propertyTemplate = context.getAttribute(PROPERTY_TEMPLATE);
		
		if(TuxGuitar.instance().getFileHistory().isUnsavedFile()){
			ConfirmDialog confirm = new ConfirmDialog(TuxGuitar.getProperty("file.save-changes-question"));
			confirm.setDefaultStatus( ConfirmDialog.STATUS_CANCEL );
			int status = confirm.confirm(ConfirmDialog.BUTTON_YES | ConfirmDialog.BUTTON_NO | ConfirmDialog.BUTTON_CANCEL, ConfirmDialog.BUTTON_YES);
			if(status == ConfirmDialog.STATUS_CANCEL){
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
							TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
							new SyncThread(new Runnable() {
								public void run() {
									if(!TuxGuitar.isDisposed()){
										newSong(propertyTemplate);
									}
								}
							}).start();
						}
					}
				}).start();
				return;
			}
		}
		newSong(propertyTemplate);
	}
	
	protected void newSong(final Object propertyTemplate){
		TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
		new Thread(new Runnable() {
			public void run() {
				if(!TuxGuitar.isDisposed()){
					TuxGuitar.instance().newSong(getTemplate(propertyTemplate));
					TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
					TGActionLock.unlock();
				}
			}
		}).start();
	}
	
	protected TGTemplate getTemplate(Object propertyTemplate){
		TGTemplate tgTemplate = null;
		if( propertyTemplate instanceof TGTemplate ){
			tgTemplate = (TGTemplate)propertyTemplate;
		}
		if( tgTemplate == null ){
			tgTemplate = TuxGuitar.instance().getTemplateManager().getDefatulTemplate();
		}
		return tgTemplate;
	}
}

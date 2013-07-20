/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.file;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.TGActionBase;
import org.herac.tuxguitar.app.actions.TGActionLock;
import org.herac.tuxguitar.app.helper.SyncThread;
import org.herac.tuxguitar.app.util.ConfirmDialog;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.util.MessageDialog;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class OpenURLAction extends TGActionBase {
	
	public static final String NAME = "action.file.open-url";
	
	public static final String PROPERTY_URL = "url";
	
	public OpenURLAction() {
		super(NAME, AUTO_LOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		final Object propertyUrl = context.getAttribute(PROPERTY_URL);
		
		TuxGuitar.instance().getPlayer().reset();
		
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
							new SyncThread(new Runnable() {
								public void run() {
									if(!TuxGuitar.isDisposed()){
										TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
										openURL( propertyUrl );
									}
								}
							}).start();
						}
					}
				}).start();
				return;
			}
		}
		openURL( propertyUrl );
	}
	
	protected void openURL(Object data){
		final URL url = getURL(data);
		if(url == null){
			TGActionLock.unlock();
			return;
		}
		TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
		new Thread(new Runnable() {
			public void run() {
				if(!TuxGuitar.isDisposed()){
					FileActionUtils.open(url);
					TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
					TGActionLock.unlock();
				}
			}
		}).start();
	}
	
	protected URL getURL(Object data){
		if(data instanceof URL){
			return (URL)data;
		}
		return new URLDialog().openDialog();
	}
	
	protected class URLDialog{
		
		protected URL url;
		
		protected URL openDialog(){
			this.url = null;
			
			final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			dialog.setLayout(new GridLayout());
			dialog.setText(TuxGuitar.getProperty("file.open-url"));
			
			Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			group.setLayout(new GridLayout());
			group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			group.setText(TuxGuitar.getProperty("file.open-url"));
			
			Composite composite = new Composite(group, SWT.NONE);
			composite.setLayout(new GridLayout(2,false));
			composite.setLayoutData(getMainData());
			
			final Label label = new Label(composite,SWT.LEFT);
			label.setText(TuxGuitar.getProperty("url") + ":");
			label.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
			
			final Text url = new Text(composite,SWT.BORDER | SWT.SINGLE);
			url.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			
			//------------------BUTTONS--------------------------
			Composite buttons = new Composite(dialog, SWT.NONE);
			buttons.setLayout(new GridLayout(2,false));
			buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
			
			final Button buttonOK = new Button(buttons, SWT.PUSH);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setLayoutData(getButtonData());
			buttonOK.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					try {
						URLDialog.this.url = new URL(url.getText());
						dialog.dispose();
					} catch (Throwable throwable) {
						MessageDialog.errorMessage(throwable);
					}
				}
			});
			
			Button buttonCancel = new Button(buttons, SWT.PUSH);
			buttonCancel.setText(TuxGuitar.getProperty("cancel"));
			buttonCancel.setLayoutData(getButtonData());
			buttonCancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					dialog.dispose();
				}
			});
			
			dialog.setDefaultButton( buttonOK );
			
			DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
			
			return this.url;
		}
		
		private GridData getMainData(){
			GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
			data.minimumWidth = 450;
			return data;
		}
		
		private GridData getButtonData(){
			GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			data.minimumWidth = 80;
			data.minimumHeight = 25;
			return data;
		}
	}
}
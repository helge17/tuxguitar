package org.herac.tuxguitar.gui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;

public class ConfirmDialog {
	
	public static int BUTTON_CANCEL = 0x01;
	public static int BUTTON_YES = 0x02;
	public static int BUTTON_NO = 0x04;
	
	public static int STATUS_YES = 1;
	public static int STATUS_NO = 2;
	public static int STATUS_CANCEL = 3;
	
	protected Shell dialog;
	protected int status;
	private String message;
	
	
	public ConfirmDialog(String message){
		this.message = message;
	}
	
	public int confirm(int style, int defaultButton){
		Shell parent = TuxGuitar.instance().getShell();
		this.dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.dialog.setLayout(new GridLayout(1, true));
		
		//========================================================================
		Composite labelComposite = new Composite(this.dialog, SWT.NONE);
		labelComposite.setLayout(new GridLayout(2, false));
		labelComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Label icon = new Label(labelComposite, SWT.NONE);
		Label message = new Label(labelComposite, SWT.NONE);
		icon.setImage(parent.getDisplay().getSystemImage(SWT.ICON_QUESTION));
		message.setText(this.message);
		
		
		//========================================================================
		GridLayout buttonsLayout = new GridLayout(0,false);
		Composite buttonsComposite = new Composite(this.dialog, SWT.NONE);
		buttonsComposite.setLayout(buttonsLayout);
		buttonsComposite.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		if((style & BUTTON_YES)!= 0){
			addCloseButton(TuxGuitar.getProperty("yes"),STATUS_YES,buttonsComposite, (defaultButton == BUTTON_YES) );
			buttonsLayout.numColumns ++;
		}
		if((style & BUTTON_NO)!= 0){
			addCloseButton(TuxGuitar.getProperty("no"),STATUS_NO,buttonsComposite, (defaultButton == BUTTON_NO) );
			buttonsLayout.numColumns ++;
		}
		if((style & BUTTON_CANCEL)!= 0){
			addCloseButton(TuxGuitar.getProperty("cancel"),STATUS_CANCEL,buttonsComposite, (defaultButton == BUTTON_CANCEL) );
			buttonsLayout.numColumns ++;
		}
		
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		
		return this.status;
	}
	
	private void addCloseButton(String text,final int value,Composite parent, boolean defaultButton){
		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(getButtonData());
		button.setText(text);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				ConfirmDialog.this.dialog.dispose();
				ConfirmDialog.this.status = value;
			}
		});
		if(defaultButton){
			this.dialog.setDefaultButton(button);
		}
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	public void setDefaultStatus(int status){
		this.status = status;
	}
	
}

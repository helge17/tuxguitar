package org.herac.tuxguitar.gui.tools.browser.ftp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserData;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class TGBrowserFactoryImpl implements TGBrowserFactory{
	
	public TGBrowserFactoryImpl() {
		super();
	}
	
	public String getType(){
		return "ftp";
	}
	
	public String getName(){
		return "FTP";
	}
	
	public TGBrowser newTGBrowser(TGBrowserData data) {
		if(data instanceof TGBrowserDataImpl){
			return new TGBrowserImpl((TGBrowserDataImpl)data);
		}
		return null;
	}
	
	public TGBrowserData parseData(String string) {
		return TGBrowserDataImpl.fromString(string);
	}
	
	public TGBrowserData dataDialog(Shell parent) {
		return new TGBrowserDataDialog().show(parent);
	}
	
}
class TGBrowserDataDialog{
	
	protected TGBrowserDataImpl data;
	
	public TGBrowserDataImpl show(Shell parent){
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("FTP Location"));
		
		//-------------LIBRARY DATA-----------------------------------------------
		Composite composite = new Composite(dialog, SWT.NONE);
		composite.setLayout(new GridLayout(2,false));
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		GridData textData = new GridData(SWT.FILL,SWT.FILL,true,true);
		textData.minimumWidth = 300;
		
		//host
		Label hostLabel = new Label(composite, SWT.NULL);
		hostLabel.setText(TuxGuitar.getProperty("Host"));
		final Text hostText = new Text(composite,SWT.BORDER);
		hostText.setLayoutData(textData);
		
		//path
		Label pathLabel = new Label(composite, SWT.NULL);
		pathLabel.setText(TuxGuitar.getProperty("Path"));
		final Text pathText = new Text(composite,SWT.BORDER);
		pathText.setLayoutData(textData);
		
		//user
		Label userLabel = new Label(composite, SWT.NULL);
		userLabel.setText(TuxGuitar.getProperty("Login name"));
		final Text userText = new Text(composite,SWT.BORDER);
		userText.setLayoutData(textData);
		
		//password
		Label passwordLabel = new Label(composite, SWT.NULL);
		passwordLabel.setText(TuxGuitar.getProperty("Password"));
		final Text passwordText = new Text(composite,SWT.BORDER | SWT.PASSWORD);
		passwordText.setLayoutData(textData);
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		
		final Button buttonOk = new Button(buttons, SWT.PUSH);
		buttonOk.setText(TuxGuitar.getProperty("ok"));
		buttonOk.setLayoutData(data);
		buttonOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				String host = hostText.getText();
				String path = pathText.getText();
				String user = userText.getText();
				String password = passwordText.getText();
				if(host != null && host.length() > 0){
					TGBrowserDataDialog.this.data = new TGBrowserDataImpl(host,path,user,password);
				}
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setLayoutData(data);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonOk );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		
		return this.data;
	}
}
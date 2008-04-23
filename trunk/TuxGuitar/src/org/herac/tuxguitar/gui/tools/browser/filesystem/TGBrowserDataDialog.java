package org.herac.tuxguitar.gui.tools.browser.filesystem;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserData;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;

public class TGBrowserDataDialog {
	
	private TGBrowserData data;
	
	public TGBrowserData getData() {
		return this.data;
	}
	
	public void setData(TGBrowserData data) {
		this.data = data;
	}
	
	public TGBrowserData open(Shell parent) {
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("browser.collection.fs.editor-title"));
		
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("browser.collection.fs.editor-tip"));
		
		Composite composite = new Composite(group,SWT.NONE);
		composite.setLayout(new GridLayout(3,false));
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		final Label titleLabel = new Label(composite,SWT.LEFT);
		titleLabel.setText(TuxGuitar.getProperty("browser.collection.fs.name"));
		titleLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,true));
		
		final Text titleValue = new Text(composite,SWT.BORDER);
		titleValue.setLayoutData(getTextData(2));
		
		final Label pathLabel = new Label(composite,SWT.LEFT);
		pathLabel.setText(TuxGuitar.getProperty("browser.collection.fs.path"));
		pathLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,true));
		
		final Text pathValue = new Text(composite,SWT.BORDER);
		pathValue.setLayoutData(getTextData(1));
		
		final Button pathChooser = new Button(composite,SWT.PUSH);
		pathChooser.setImage(TuxGuitar.instance().getIconManager().getFileOpen());
		pathChooser.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(dialog);
				String selection = directoryDialog.open();
				if(selection != null){
					pathValue.setText(selection);
				}
			}
		});
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				String selectedTitle = titleValue.getText();
				String selectedPath = pathValue.getText();
				if(!isValidPath(selectedPath)){
					MessageDialog.errorMessage(dialog,TuxGuitar.getProperty("browser.collection.fs.invalid-path"));
					return;
				}
				if(isBlank(selectedTitle)){
					selectedTitle = selectedPath;
				}
				setData(new TGBrowserDataImpl(selectedTitle,selectedPath));
				dialog.dispose();
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
		
		return getData();
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	private GridData getTextData(int span){
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, true,span,1);
		data.minimumWidth = 350;
		return data;
	}
	
	protected boolean isBlank(String s){
		return (s == null || s.length() == 0);
	}
	
	protected boolean isValidPath(String path){
		if(!isBlank(path)){
			File file = new File(path);
			return (file.exists() && file.isDirectory());
		}
		return false;
	}
}

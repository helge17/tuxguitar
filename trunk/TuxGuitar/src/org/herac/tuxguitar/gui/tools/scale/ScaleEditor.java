package org.herac.tuxguitar.gui.tools.scale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class ScaleEditor {
	
	public void show() {
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("scale.list"));
		dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		// ----------------------------------------------------------------------
		Composite composite = new Composite(dialog, SWT.NONE);
		composite.setLayout(new GridLayout(2,false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final List keys = new List(composite,SWT.BORDER | SWT.V_SCROLL);
		keys.setLayoutData(new GridData(50,200));
		String[] keyNames = TuxGuitar.instance().getScaleManager().getKeyNames();
		for(int i = 0;i < keyNames.length;i ++){
			keys.add(keyNames[i]);
		}
		keys.select(TuxGuitar.instance().getScaleManager().getSelectionKey());
		
		final List scales = new List(composite,SWT.BORDER | SWT.V_SCROLL);
		scales.setLayoutData(new GridData(SWT.DEFAULT,200));
		scales.add("None");
		String[] scaleNames = TuxGuitar.instance().getScaleManager().getScaleNames();
		for(int i = 0;i < scaleNames.length;i ++){
			scales.add(scaleNames[i]);
		}
		scales.select(TuxGuitar.instance().getScaleManager().getSelectionIndex() + 1);
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TuxGuitar.instance().getScaleManager().selectScale((scales.getSelectionIndex() - 1), keys.getSelectionIndex());
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
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
}

package org.herac.tuxguitar.app.view.dialog.scale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.tools.TGSelectScaleAction;
import org.herac.tuxguitar.app.tools.scale.ScaleManager;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

public class TGScaleDialog {
	
	public void show(final TGViewContext context) {
		final ScaleManager scaleManager = ScaleManager.getInstance(context.getContext());
		final Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("scale.list"));
		dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		// ----------------------------------------------------------------------
		Composite composite = new Composite(dialog, SWT.NONE);
		composite.setLayout(new GridLayout(2,false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final List keys = new List(composite,SWT.BORDER | SWT.V_SCROLL);
		keys.setLayoutData(new GridData(50,200));
		String[] keyNames = scaleManager.getKeyNames();
		for(int i = 0;i < keyNames.length;i ++){
			keys.add(keyNames[i]);
		}
		keys.select(scaleManager.getSelectionKey());
		
		final List scales = new List(composite,SWT.BORDER | SWT.V_SCROLL);
		scales.setLayoutData(new GridData(SWT.DEFAULT,200));
		scales.add("None");
		String[] scaleNames = scaleManager.getScaleNames();
		for(int i = 0;i < scaleNames.length;i ++){
			scales.add(scaleNames[i]);
		}
		scales.select(scaleManager.getSelectionIndex() + 1);
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				selectScale(context.getContext(), (scales.getSelectionIndex() - 1), keys.getSelectionIndex());
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
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	public void selectScale(TGContext context, Integer index, Integer key) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGSelectScaleAction.NAME);
		tgActionProcessor.setAttribute(TGSelectScaleAction.ATTRIBUTE_INDEX, index);
		tgActionProcessor.setAttribute(TGSelectScaleAction.ATTRIBUTE_KEY, key);
		tgActionProcessor.process();
	}
}

package org.herac.tuxguitar.app.view.dialog.measure;

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
import org.eclipse.swt.widgets.Spinner;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.measure.TGPasteMeasureAction;
import org.herac.tuxguitar.app.clipboard.MeasureTransferable;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

public class TGMeasurePasteDialog {
	
	public void show(final TGViewContext context) {
		final Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("edit.paste"));
		
		//-----------------COUNT------------------------
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("edit.paste"));
		
		Label countLabel = new Label(group, SWT.NULL);
		countLabel.setText(TuxGuitar.getProperty("edit.paste.count"));
		
		final Spinner countSpinner = new Spinner(group, SWT.BORDER);
		countSpinner.setLayoutData(getSpinnerData());
		countSpinner.setMinimum( 1 );
		countSpinner.setMaximum( 100 );
		countSpinner.setSelection( 1 );
		
		//----------------------------------------------------------------------
		Group options = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		options.setLayout(new GridLayout());
		options.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		options.setText(TuxGuitar.getProperty("options"));
		
		final Button replace = new Button(options,SWT.RADIO);
		replace.setText(TuxGuitar.getProperty("edit.paste.replace-mode"));
		replace.setSelection(true);
		
		final Button insert = new Button(options,SWT.RADIO);
		insert.setText(TuxGuitar.getProperty("edit.paste.insert-mode"));
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				int pasteMode = 0;
				int pasteCount = countSpinner.getSelection();
				if( replace.getSelection() ){
					pasteMode = MeasureTransferable.TRANSFER_TYPE_REPLACE;
				}else if(insert.getSelection()){
					pasteMode = MeasureTransferable.TRANSFER_TYPE_INSERT;
				}
				processAction(context.getContext(), pasteMode, pasteCount);
				
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
	
	private GridData getSpinnerData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 150;
		return data;
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	public void processAction(TGContext context, Integer pasteMode, Integer pasteCount) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGPasteMeasureAction.NAME);
		tgActionProcessor.setAttribute(TGPasteMeasureAction.ATTRIBUTE_PASTE_MODE, pasteMode);
		tgActionProcessor.setAttribute(TGPasteMeasureAction.ATTRIBUTE_PASTE_COUNT, pasteCount);
		tgActionProcessor.process();
	}
}

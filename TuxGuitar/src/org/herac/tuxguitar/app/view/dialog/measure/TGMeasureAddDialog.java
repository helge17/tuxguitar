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
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.measure.TGAddMeasureListAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGMeasureAddDialog {
	
	public void show(final TGViewContext context) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		
		final Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("measure.add"));
		
		//-----------------COUNT------------------------
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("measure.add"));
		
		Label countLabel = new Label(group, SWT.NULL);
		countLabel.setText(TuxGuitar.getProperty("measure.add.count"));
		
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
		
		final Button beforePosition = new Button(options,SWT.RADIO);
		beforePosition.setText(TuxGuitar.getProperty("measure.add-before-current-position"));
		
		final Button afterPosition = new Button(options,SWT.RADIO);
		afterPosition.setText(TuxGuitar.getProperty("measure.add-after-current-position"));
		
		final Button atEnd = new Button(options,SWT.RADIO);
		atEnd.setText(TuxGuitar.getProperty("measure.add-at-end"));
		atEnd.setSelection(true);
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				int number = 0;
				int count = countSpinner.getSelection();
				if( beforePosition.getSelection() ){
					number = (header.getNumber());
				}else if( afterPosition.getSelection() ){
					number = (header.getNumber() + 1);
				}else if( atEnd.getSelection() ){
					number = (song.countMeasureHeaders() + 1);
				}
				processAction(context.getContext(), song, number, count);
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
	
	public void processAction(TGContext context, TGSong song, Integer number, Integer count) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGAddMeasureListAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_COUNT, count);
		tgActionProcessor.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_NUMBER, number);
		tgActionProcessor.process();
	}
}

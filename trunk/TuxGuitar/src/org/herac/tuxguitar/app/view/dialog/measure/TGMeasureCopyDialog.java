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
import org.herac.tuxguitar.app.action.impl.measure.TGCopyMeasureAction;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGMeasureCopyDialog {
	
	public void show(final TGViewContext context) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		
		final Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("edit.copy"));
		
		//----------------------------------------------------------------------
		Group range = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		range.setLayout(new GridLayout(2,false));
		range.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		range.setText(TuxGuitar.getProperty("edit.copy"));
		
		int measureCount = song.countMeasureHeaders();
		
		Label fromLabel = new Label(range, SWT.NULL);
		fromLabel.setText(TuxGuitar.getProperty("edit.from"));
		final Spinner fromSpinner = new Spinner(range, SWT.BORDER);
		fromSpinner.setLayoutData(getSpinnerData());
		fromSpinner.setMinimum(1);
		fromSpinner.setMaximum(measureCount);
		fromSpinner.setSelection(header.getNumber());
		
		Label toLabel = new Label(range, SWT.NULL);
		toLabel.setText(TuxGuitar.getProperty("edit.to"));
		final Spinner toSpinner = new Spinner(range, SWT.BORDER);
		toSpinner.setLayoutData(getSpinnerData());
		toSpinner.setMinimum(1);
		toSpinner.setMaximum(measureCount);
		toSpinner.setSelection(header.getNumber());
		
		final int minSelection = 1;
		final int maxSelection = track.countMeasures();
		
		fromSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int fromSelection = fromSpinner.getSelection();
				int toSelection = toSpinner.getSelection();
				
				if(fromSelection < minSelection){
					fromSpinner.setSelection(minSelection);
				}else if(fromSelection > toSelection){
					fromSpinner.setSelection(toSelection);
				}
			}
		});
		toSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int toSelection = toSpinner.getSelection();
				int fromSelection = fromSpinner.getSelection();
				if(toSelection < fromSelection){
					toSpinner.setSelection(fromSelection);
				}else if(toSelection > maxSelection){
					toSpinner.setSelection(maxSelection);
				}
			}
		});
		//----------------------------------------------------------------------
		Button allTracks = null;
		if( song.countTracks() > 1){
			Group checkComposites = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			checkComposites.setLayout(new GridLayout());
			checkComposites.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			checkComposites.setText(TuxGuitar.getProperty("options"));
			
			allTracks = new Button(checkComposites,SWT.CHECK);
			allTracks.setText(TuxGuitar.getProperty("edit.all-tracks"));
			allTracks.setSelection(true);
		}
		final Button allTracksFinal = allTracks;
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				processAction(context.getContext(), fromSpinner.getSelection(),toSpinner.getSelection(), (allTracksFinal != null ? allTracksFinal.getSelection() : true));
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
	
	protected GridData getSpinnerData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 180;
		return data;
	}
	
	public void processAction(TGContext context, Integer measure1, Integer measure2, Boolean copyAllTracks) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGCopyMeasureAction.NAME);
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_1, measure1);
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_2, measure2);
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_ALL_TRACKS, copyAllTracks);
		tgActionProcessor.process();
	}
}

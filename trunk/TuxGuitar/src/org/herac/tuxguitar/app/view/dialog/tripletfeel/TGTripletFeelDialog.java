package org.herac.tuxguitar.app.view.dialog.tripletfeel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeTripletFeelAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGTripletFeelDialog {
	
	public void show(final TGViewContext context) {
		Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("composition.tripletfeel"));
		dialog.setMinimumSize(300,0);
		
		//-------------TIME SIGNATURE-----------------------------------------------
		Group tripletFeel = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		tripletFeel.setLayout(new GridLayout());
		tripletFeel.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		tripletFeel.setText(TuxGuitar.getProperty("composition.tripletfeel"));
		
		//none
		final Button tripletFeelNone = new Button(tripletFeel, SWT.RADIO);
		tripletFeelNone.setText(TuxGuitar.getProperty("composition.tripletfeel.none"));
		tripletFeelNone.setSelection(header.getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_NONE);
		
		final Button tripletFeelEighth = new Button(tripletFeel, SWT.RADIO);
		tripletFeelEighth.setText(TuxGuitar.getProperty("composition.tripletfeel.eighth"));
		tripletFeelEighth.setSelection(header.getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_EIGHTH);
		
		final Button tripletFeelSixteenth = new Button(tripletFeel, SWT.RADIO);
		tripletFeelSixteenth.setText(TuxGuitar.getProperty("composition.tripletfeel.sixteenth"));
		tripletFeelSixteenth.setSelection(header.getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH);
		
		//--------------------To End Checkbox-------------------------------
		Group check = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		check.setLayout(new GridLayout());
		check.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		check.setText(TuxGuitar.getProperty("options"));
		
		final Button toEnd = new Button(check, SWT.CHECK);
		toEnd.setText(TuxGuitar.getProperty("composition.tripletfeel.to-the-end"));
		toEnd.setSelection(true);
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		final Button buttonOk = new Button(buttons, SWT.PUSH);
		buttonOk.setText(TuxGuitar.getProperty("ok"));
		buttonOk.setLayoutData(getButtonData());
		buttonOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				changeTripletFeel(context.getContext(), song, header, parseTripletFeel(tripletFeelNone, tripletFeelEighth, tripletFeelSixteenth), toEnd.getSelection());
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonOk );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected int parseTripletFeel(Button tripletFeelNone,Button tripletFeelEighth, Button tripletFeelSixteenth){
		if(tripletFeelNone.getSelection()){
			return TGMeasureHeader.TRIPLET_FEEL_NONE;
		}else if(tripletFeelEighth.getSelection()){
			return TGMeasureHeader.TRIPLET_FEEL_EIGHTH;
		}else if(tripletFeelSixteenth.getSelection()){
			return TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH;
		}
		return TGMeasureHeader.TRIPLET_FEEL_NONE;
	}
	
	public void changeTripletFeel(TGContext context, TGSong song, TGMeasureHeader header, Integer tripletFeel, Boolean applyToEnd) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeTripletFeelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGChangeTripletFeelAction.ATTRIBUTE_TRIPLET_FEEL, tripletFeel);
		tgActionProcessor.setAttribute(TGChangeTripletFeelAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.processOnNewThread();
	}
}

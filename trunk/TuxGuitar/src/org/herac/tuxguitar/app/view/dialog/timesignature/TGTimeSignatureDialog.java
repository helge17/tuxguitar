package org.herac.tuxguitar.app.view.dialog.timesignature;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.util.TGContext;

public class TGTimeSignatureDialog {
	
	public void show(final TGViewContext context) {
		Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final TGSongManager songManager = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("composition.timesignature"));
		
		//-------------TIME SIGNATURE-----------------------------------------------
		Group timeSignature = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		timeSignature.setLayout(new GridLayout(2,false));
		timeSignature.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		timeSignature.setText(TuxGuitar.getProperty("composition.timesignature"));
		
		TGTimeSignature currentTimeSignature = header.getTimeSignature();
		//numerator
		Label numeratorLabel = new Label(timeSignature, SWT.NULL);
		numeratorLabel.setText(TuxGuitar.getProperty("composition.timesignature.Numerator"));
		final Combo numerator = new Combo(timeSignature, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (int i = 1; i <= 32; i++) {
			numerator.add(Integer.toString(i));
		}
		numerator.setText(Integer.toString(currentTimeSignature.getNumerator()));
		numerator.setLayoutData(getComboData());
		//denominator
		Label denominatorLabel = new Label(timeSignature, SWT.NULL);
		denominatorLabel.setText(TuxGuitar.getProperty("composition.timesignature.denominator"));
		final Combo denominator = new Combo(timeSignature, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (int i = 1; i <= 32; i = i * 2) {
			denominator.add(Integer.toString(i));
		}
		denominator.setText(Integer.toString(currentTimeSignature.getDenominator().getValue()));
		denominator.setLayoutData(getComboData());
		
		//--------------------To End Checkbox-------------------------------
		Group check = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		check.setLayout(new GridLayout());
		check.setText(TuxGuitar.getProperty("options"));
		check.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		final Button toEnd = new Button(check, SWT.CHECK);
		toEnd.setText(TuxGuitar.getProperty("composition.timesignature.to-the-end"));
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
				changeTimeSignature(context.getContext(), song, header, parseTimeSignature(songManager, numerator, denominator), toEnd.getSelection());
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
	
	private GridData getComboData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 150;
		return data;
	}
	
	public TGTimeSignature parseTimeSignature(TGSongManager songManager, Combo numerator, Combo denominator) {
		TGTimeSignature tgTimeSignature = songManager.getFactory().newTimeSignature();
		tgTimeSignature.setNumerator(Integer.parseInt(numerator.getText()));
		tgTimeSignature.getDenominator().setValue(Integer.parseInt(denominator.getText()));
		return tgTimeSignature;
	}
	
	public void changeTimeSignature(TGContext context, TGSong song, TGMeasureHeader header, TGTimeSignature timeSignature, Boolean applyToEnd) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeTimeSignatureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE, timeSignature);
		tgActionProcessor.setAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.processOnNewThread();
	}
}

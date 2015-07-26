package org.herac.tuxguitar.app.view.dialog.keysignature;

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
import org.herac.tuxguitar.editor.action.composition.TGChangeKeySignatureAction;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGKeySignatureDialog {
	
	public void show(final TGViewContext context) {
		Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("composition.keysignature"));
		
		//-------key Signature-------------------------------------
		Group keySignature = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		keySignature.setLayout(new GridLayout(2,false));
		keySignature.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		keySignature.setText(TuxGuitar.getProperty("composition.keysignature"));
		
		Label numeratorLabel = new Label(keySignature, SWT.NULL);
		numeratorLabel.setText(TuxGuitar.getProperty("composition.keysignature") + ":");
		
		final Combo keySignatures = new Combo(keySignature, SWT.DROP_DOWN | SWT.READ_ONLY);
		
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.natural"));
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.sharp-1"));
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.sharp-2"));
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.sharp-3"));
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.sharp-4"));
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.sharp-5"));
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.sharp-6"));
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.sharp-7"));
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.flat-1"));
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.flat-2"));
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.flat-3"));
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.flat-4"));
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.flat-5"));
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.flat-6"));
		keySignatures.add(TuxGuitar.getProperty("composition.keysignature.flat-7"));
		keySignatures.select(measure.getKeySignature());
		keySignatures.setLayoutData(getComboData());
		//--------------------To End Checkbox-------------------------------
		Group check = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		check.setLayout(new GridLayout());
		check.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		check.setText(TuxGuitar.getProperty("options"));
		
		final Button toEnd = new Button(check, SWT.CHECK);
		toEnd.setText(TuxGuitar.getProperty("composition.keysignature.to-the-end"));
		toEnd.setSelection(true);
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				changeKeySignature(context.getContext(), track, measure, keySignatures.getSelectionIndex(), toEnd.getSelection());
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
	
	private GridData getComboData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 150;
		return data;
	}
	
	public void changeKeySignature(TGContext context, TGTrack track, TGMeasure measure, Integer value, Boolean applyToEnd) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeKeySignatureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_KEY_SIGNATURE, value);
		tgActionProcessor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.processOnNewThread();
	}
}

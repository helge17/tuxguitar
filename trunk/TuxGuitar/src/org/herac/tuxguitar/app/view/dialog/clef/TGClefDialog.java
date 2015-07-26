package org.herac.tuxguitar.app.view.dialog.clef;

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
import org.herac.tuxguitar.editor.action.composition.TGChangeClefAction;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGClefDialog {
	
	public void show(final TGViewContext context) {
		Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("composition.clef"));
		
		//-------clef-------------------------------------
		Group clef = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		clef.setLayout(new GridLayout(2,false));
		clef.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		clef.setText(TuxGuitar.getProperty("composition.clef"));
		
		Label numeratorLabel = new Label(clef, SWT.NULL);
		numeratorLabel.setText(TuxGuitar.getProperty("composition.clef") + ":");
		
		final Combo clefs = new Combo(clef, SWT.DROP_DOWN | SWT.READ_ONLY);
		
		clefs.add(TuxGuitar.getProperty("composition.clef.treble"));
		clefs.add(TuxGuitar.getProperty("composition.clef.bass"));
		clefs.add(TuxGuitar.getProperty("composition.clef.tenor"));
		clefs.add(TuxGuitar.getProperty("composition.clef.alto"));
		clefs.select(measure.getClef() - 1);
		clefs.setLayoutData(getComboData());
		
		//--------------------To End Checkbox-------------------------------
		Group check = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		check.setLayout(new GridLayout());
		check.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		check.setText(TuxGuitar.getProperty("options"));
		
		final Button toEnd = new Button(check, SWT.CHECK);
		toEnd.setText(TuxGuitar.getProperty("composition.clef.to-the-end"));
		toEnd.setSelection(true);
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				changeClef(context.getContext(), song, track, measure, (clefs.getSelectionIndex() + 1), toEnd.getSelection());
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
	
	private GridData getComboData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 150;
		return data;
	}
	
	protected GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	public void changeClef(TGContext context, TGSong song, TGTrack track, TGMeasure measure, Integer value, Boolean applyToEnd) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeClefAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGChangeClefAction.ATTRIBUTE_CLEF, value);
		tgActionProcessor.setAttribute(TGChangeClefAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.processOnNewThread();
	}
}

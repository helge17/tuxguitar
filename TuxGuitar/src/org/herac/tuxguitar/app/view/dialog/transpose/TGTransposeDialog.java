package org.herac.tuxguitar.app.view.dialog.transpose;

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
import org.herac.tuxguitar.app.action.impl.tools.TGTransposeAction;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

public class TGTransposeDialog {
	
	public void show(final TGViewContext context) {
		final Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		final int[] transpositions = new int[25];
		for( int i = 0 ; i < transpositions.length ; i ++ ){
			transpositions[ i ] = ( i - ( transpositions.length / 2 ) );
		}
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("tools.transpose"));
		
		//-----------------TEMPO------------------------
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("tools.transpose"));
		
		
		Label transpositionLabel = new Label(group, SWT.NULL);
		transpositionLabel.setText(TuxGuitar.getProperty("tools.transpose.semitones"));
		transpositionLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		final Combo transpositionCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY );
		transpositionCombo.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true , true) );
		for( int i = 0 ; i < transpositions.length ; i ++ ){
			transpositionCombo.add( Integer.toString( transpositions[i]) );
		}
		transpositionCombo.select( ( transpositions.length / 2 ) );
		
		//------------------OPTIONS--------------------------
		Group options = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		options.setLayout(new GridLayout());
		options.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		options.setText(TuxGuitar.getProperty("options"));
		
		final Button applyToAllMeasuresButton = new Button(options, SWT.RADIO);
		applyToAllMeasuresButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-track"));
		applyToAllMeasuresButton.setSelection(true);
		
		final Button applyToCurrentMeasureButton = new Button(options, SWT.RADIO);
		applyToCurrentMeasureButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-measure"));
		
		final Button applyToAllTracksButton = new Button(options, SWT.CHECK);
		applyToAllTracksButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-all-tracks"));
		applyToAllTracksButton.setSelection(true);
		
		final Button applyToChordsButton = new Button(options, SWT.CHECK);
		applyToChordsButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-chords"));
		applyToChordsButton.setSelection(true);
		
		final Button tryKeepStringButton = new Button(options, SWT.CHECK);
		tryKeepStringButton.setText(TuxGuitar.getProperty("tools.transpose.try-keep-strings"));
		tryKeepStringButton.setSelection(true);
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				int transpositionIndex =  transpositionCombo.getSelectionIndex() ;
				if( transpositionIndex >= 0 && transpositionIndex < transpositions.length ){
					final int transposition = transpositions[ transpositionIndex ];
					final boolean tryKeepString = tryKeepStringButton.getSelection();
					final boolean applyToChords = applyToChordsButton.getSelection();
					final boolean applyToAllTracks = applyToAllTracksButton.getSelection();
					final boolean applyToAllMeasures = applyToAllMeasuresButton.getSelection();
					
					transposeNotes(context, transposition, tryKeepString, applyToChords , applyToAllMeasures, applyToAllTracks);
				}
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
	
	public void transposeNotes(TGViewContext context, int transposition , boolean tryKeepString , boolean applyToChords , boolean applyToAllMeasures , boolean applyToAllTracks) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context.getContext(), TGTransposeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_TRANSPOSITION, transposition);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_TRY_KEEP_STRING, tryKeepString);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_CHORDS, applyToChords);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_ALL_TRACKS, applyToAllTracks);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_ALL_MEASURES, applyToAllMeasures);
		tgActionProcessor.processOnNewThread();
	}
}

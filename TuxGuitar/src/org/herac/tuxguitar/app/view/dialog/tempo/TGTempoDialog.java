package org.herac.tuxguitar.app.view.dialog.tempo;

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
import org.herac.tuxguitar.editor.action.composition.TGChangeTempoRangeAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.util.TGContext;

public class TGTempoDialog {
	
	private static final int MIN_TEMPO = 30;
	private static final int MAX_TEMPO = 320;
	
	protected static final int[] DEFAULT_PERCENTS = new int[]{25,50,75,100,125,150,175,200};
	
	public void show(final TGViewContext context) {
		Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("composition.tempo"));
		
		//-----------------TEMPO------------------------
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("composition.tempo"));
		
		TGTempo currentTempo = header.getTempo();
		Label tempoLabel = new Label(group, SWT.NULL);
		tempoLabel.setText(TuxGuitar.getProperty("composition.tempo"));
		
		final Spinner tempo = new Spinner(group, SWT.BORDER);
		tempo.setLayoutData(getSpinnerData());
		tempo.setMinimum(MIN_TEMPO);
		tempo.setMaximum(MAX_TEMPO);
		tempo.setSelection(currentTempo.getValue());
		
		//------------------OPTIONS--------------------------
		Group options = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		options.setLayout(new GridLayout());
		options.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		options.setText(TuxGuitar.getProperty("options"));
		
		final Button applyToAllMeasures = new Button(options, SWT.RADIO);
		applyToAllMeasures.setText(TuxGuitar.getProperty("composition.tempo.start-to-end"));
		
		final Button applyToEnd = new Button(options, SWT.RADIO);
		applyToEnd.setText(TuxGuitar.getProperty("composition.tempo.position-to-end"));
		
		final Button applyToNext = new Button(options, SWT.RADIO);
		applyToNext.setText(TuxGuitar.getProperty("composition.tempo.position-to-next"));
		
		applyToAllMeasures.setSelection(true);
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				Integer value = tempo.getSelection();
				Integer applyTo = parseApplyTo(applyToAllMeasures, applyToEnd, applyToNext);
				
				changeTempo(context.getContext(), song, header, value, applyTo);
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
	
	private GridData getSpinnerData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 150;
		return data;
	}
	
	private Integer parseApplyTo(Button applyToAll, Button applyToEnd, Button applyToNext) {
		if( applyToAll.getSelection() ) {
			return TGChangeTempoRangeAction.APPLY_TO_ALL;
		}
		if( applyToEnd.getSelection() ) {
			return TGChangeTempoRangeAction.APPLY_TO_END;
		}
		if( applyToNext.getSelection() ) {
			return TGChangeTempoRangeAction.APPLY_TO_NEXT;
		}
		return 0;
	}
	
	public void changeTempo(TGContext context, TGSong song, TGMeasureHeader header, Integer value, Integer applyTo) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeTempoRangeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO, value);
		tgActionProcessor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_APPLY_TO, applyTo);
		tgActionProcessor.processOnNewThread();
	}
}

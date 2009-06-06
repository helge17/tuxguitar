package org.herac.tuxguitar.gui.printer;

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
import org.eclipse.swt.widgets.Spinner;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class PrintStylesDialog {

	public static PrintStyles open(Shell shell) {
		final PrintStyles styles = new PrintStyles();
		final Shell dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("options"));
		
		//------------------TRACK SELECTION------------------
		Group track = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		track.setLayout(new GridLayout(2,false));
		track.setLayoutData(getGroupData());
		track.setText(TuxGuitar.getProperty("track"));
		
		Label trackLabel = new Label(track, SWT.NULL);
		trackLabel.setText(TuxGuitar.getProperty("track"));
		
		final Combo tracks = new Combo(track, SWT.DROP_DOWN | SWT.READ_ONLY);
		tracks.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		for(int number = 1; number <= TuxGuitar.instance().getSongManager().getSong().countTracks(); number ++){
			tracks.add(TuxGuitar.instance().getSongManager().getTrack(number).getName());
		}
		tracks.select(TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getTrack().getNumber() - 1);
		
		//------------------MEASURE RANGE------------------
		Group range = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		range.setLayout(new GridLayout(2,false));
		range.setLayoutData(getGroupData());
		range.setText(TuxGuitar.getProperty("print.range"));
		
		final int minSelection = 1;
		final int maxSelection = TuxGuitar.instance().getSongManager().getSong().countMeasureHeaders();
		
		Label fromLabel = new Label(range, SWT.NULL);
		fromLabel.setText(TuxGuitar.getProperty("edit.from"));
		final Spinner fromSpinner = new Spinner(range, SWT.BORDER);
		fromSpinner.setLayoutData(getSpinnerData());
		fromSpinner.setMaximum(maxSelection);
		fromSpinner.setMinimum(minSelection);
		fromSpinner.setSelection(minSelection);
		
		Label toLabel = new Label(range, SWT.NULL);
		toLabel.setText(TuxGuitar.getProperty("edit.to"));
		final Spinner toSpinner = new Spinner(range, SWT.BORDER);
		toSpinner.setLayoutData(getSpinnerData());
		toSpinner.setMinimum(minSelection);
		toSpinner.setMaximum(maxSelection);
		toSpinner.setSelection(maxSelection);
		
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
		//------------------CHECK OPTIONS--------------------
		Group options = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		options.setLayout(new GridLayout());
		options.setLayoutData(getGroupData());
		options.setText(TuxGuitar.getProperty("options"));
		
		final Button tablatureEnabled = new Button(options,SWT.CHECK);
		tablatureEnabled.setText(TuxGuitar.getProperty("export.tablature-enabled"));
		tablatureEnabled.setSelection(true);
		
		final Button scoreEnabled = new Button(options,SWT.CHECK);
		scoreEnabled.setText(TuxGuitar.getProperty("export.score-enabled"));
		scoreEnabled.setSelection(true);
		
		final Button chordNameEnabled = new Button(options,SWT.CHECK);
		chordNameEnabled.setText(TuxGuitar.getProperty("export.chord-name-enabled"));
		chordNameEnabled.setSelection(true);
		
		final Button chordDiagramEnabled = new Button(options,SWT.CHECK);
		chordDiagramEnabled.setText(TuxGuitar.getProperty("export.chord-diagram-enabled"));
		chordDiagramEnabled.setSelection(true);
		
		final Button blackAndWhite = new Button(options,SWT.CHECK);
		blackAndWhite.setText(TuxGuitar.getProperty("export.black-and-white"));
		blackAndWhite.setSelection(true);
		
		tablatureEnabled.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if(!tablatureEnabled.getSelection()){
					scoreEnabled.setSelection(true);
				}
			}
		});
		scoreEnabled.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if(!scoreEnabled.getSelection()){
					tablatureEnabled.setSelection(true);
				}
			}
		});
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				int style = 0;
				style |= (scoreEnabled.getSelection() ? ViewLayout.DISPLAY_SCORE : 0);
				style |= (tablatureEnabled.getSelection() ? ViewLayout.DISPLAY_TABLATURE : 0);
				style |= (chordNameEnabled.getSelection() ? ViewLayout.DISPLAY_CHORD_NAME : 0);
				style |= (chordDiagramEnabled.getSelection() ? ViewLayout.DISPLAY_CHORD_DIAGRAM : 0);
				styles.setTrackNumber(tracks.getSelectionIndex() + 1);
				styles.setFromMeasure(fromSpinner.getSelection());
				styles.setToMeasure(toSpinner.getSelection());
				styles.setBlackAndWhite(blackAndWhite.getSelection());
				styles.setStyle(style);
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
		
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		
		return ((styles.getTrackNumber() > 0)?styles:null);
	}
	
	private static GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	private static GridData getSpinnerData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 60;
		return data;
	}
	
	private static GridData getGroupData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 300;
		return data;
	}
}

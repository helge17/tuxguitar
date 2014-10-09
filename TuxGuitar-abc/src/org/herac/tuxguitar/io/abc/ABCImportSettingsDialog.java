package org.herac.tuxguitar.io.abc;

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

public class ABCImportSettingsDialog {
	
	private static final int STATUS_NONE = 0;
	
	private static final int STATUS_CANCELLED = 1;
	
	private static final int STATUS_ACCEPTED = 2;
	
	protected int status;
	
	public ABCImportSettingsDialog(){
		super();
	}
	
	public ABCSettings open() {
		this.status = STATUS_NONE;
		final ABCSettings settings = ABCSettings.getDefaults();
		
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout(2, false));
		dialog.setText(TuxGuitar.getProperty("abc.options"));
		
		Composite columnLeft = new Composite(dialog, SWT.NONE);
		columnLeft.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		columnLeft.setLayout(getColumnLayout());
		
		Composite columnRight = new Composite(dialog, SWT.NONE);
		columnRight.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		columnRight.setLayout(getColumnLayout());
		
		//------------------X SELECTION------------------
		Group xGroup = new Group(columnLeft,SWT.SHADOW_ETCHED_IN);
		xGroup.setLayout(new GridLayout(2,false));
		xGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		xGroup.setText(TuxGuitar.getProperty("abc.options.x.tip"));
		
		final int minXNum = 1;
		final int maxXNum = 100;
		
		Label xNumLabel = new Label(xGroup, SWT.NULL);
		xNumLabel.setText(TuxGuitar.getProperty("abc.options.x.number"));
		xNumLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		final Spinner xNumSpinner = new Spinner(xGroup, SWT.BORDER);
		xNumSpinner.setLayoutData(getSpinnerData());
		xNumSpinner.setMaximum(maxXNum);
		xNumSpinner.setMinimum(minXNum);
		xNumSpinner.setSelection(minXNum);
		
		xNumSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int fromSelection = xNumSpinner.getSelection();
				
				if(fromSelection < minXNum){
					xNumSpinner.setSelection(minXNum);
				}else if(fromSelection > maxXNum){
					xNumSpinner.setSelection(maxXNum);
				}
			}
		});

		//------------------LAYOUT OPTIONS------------------
		Group layoutGroup = new Group(columnRight,SWT.SHADOW_ETCHED_IN);
		layoutGroup.setLayout(new GridLayout());
		layoutGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		layoutGroup.setText(TuxGuitar.getProperty("abc.options.layout.tip"));
		
		final Button trackNameCheck = new Button(layoutGroup,SWT.CHECK);
		trackNameCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		trackNameCheck.setSelection(settings.isTrackNameEnabled());
		trackNameCheck.setText(TuxGuitar.getProperty("abc.options.layout.enable-track-names"));

		final Button lyricsCheck = new Button(layoutGroup,SWT.CHECK);
		lyricsCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		lyricsCheck.setSelection(settings.isLyricsEnabled());
		lyricsCheck.setText(TuxGuitar.getProperty("abc.options.layout.enable-lyrics"));

		final Button textsCheck = new Button(layoutGroup,SWT.CHECK);
		textsCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		textsCheck.setSelection(settings.isTextEnabled());
		textsCheck.setText(TuxGuitar.getProperty("abc.options.layout.enable-texts"));
		
		final Button chordDiagramsCheck = new Button(layoutGroup,SWT.CHECK);
		chordDiagramsCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		chordDiagramsCheck.setSelection(settings.isChordDiagramEnabled());
		chordDiagramsCheck.setText(TuxGuitar.getProperty("abc.options.layout.enable-chord-diagrams"));
		
		final Button chordTracksCheck = new Button(layoutGroup,SWT.CHECK);
		chordTracksCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		chordTracksCheck.setSelection(settings.isChordEnabled());
		chordTracksCheck.setText(TuxGuitar.getProperty("abc.options.layout.enable-chord-tracks"));
		
		final Button droneTrackCheck = new Button(layoutGroup,SWT.CHECK);
		droneTrackCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		droneTrackCheck.setSelection(settings.isDroneEnabled());
		droneTrackCheck.setText(TuxGuitar.getProperty("abc.options.layout.enable-drone-track"));
		
		final Button instrCheck = new Button(layoutGroup,SWT.CHECK);
		instrCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		instrCheck.setSelection(settings.isInstrumentsStartAt1());
		instrCheck.setText(TuxGuitar.getProperty("abc.options.layout.instruments-start-at-1"));
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true,2,1));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				ABCImportSettingsDialog.this.status = STATUS_ACCEPTED;
				
				settings.setX(xNumSpinner.getSelection());
				settings.setTrackNameEnabled( trackNameCheck.getSelection() );
				settings.setChordDiagramEnabled(chordDiagramsCheck.getSelection());
				settings.setChordEnabled(chordTracksCheck.getSelection());
				settings.setDroneEnabled(droneTrackCheck.getSelection());
				settings.setLyricsEnabled(lyricsCheck.getSelection());
				settings.setTextEnabled(textsCheck.getSelection());
				settings.setInstrumentsStartAt1(instrCheck.getSelection());
				settings.check();
				
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				ABCImportSettingsDialog.this.status = STATUS_CANCELLED;
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		
		return ( ( this.status == STATUS_ACCEPTED )? settings : null );
	}
	
	private GridLayout getColumnLayout(){
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		return layout;
	}
	
	private GridData getSpinnerData(){
		GridData data = new GridData(SWT.FILL,SWT.CENTER,true,true);
		data.minimumWidth = 60;
		return data;
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
}

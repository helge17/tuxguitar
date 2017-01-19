package org.herac.tuxguitar.io.abc;

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
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.swt.widget.SWTWindow;
import org.herac.tuxguitar.util.TGContext;

public class ABCSongWriterSettingsDialog {
	
	private TGContext context;
	
	private TGSong song;
	
	public ABCSongWriterSettingsDialog(TGContext context, TGSong song){
		this.context = context;
		this.song = song;
	}
	
	public void open(final ABCSettings settings, final Runnable onSuccess) {
		final Shell parent = ((SWTWindow) TGWindow.getInstance(this.context).getWindow()).getControl();
		final Shell dialog = SWTDialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
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

		//------------------TRACK SELECTION------------------
		Group trackGroup = new Group(columnLeft ,SWT.SHADOW_ETCHED_IN);
		trackGroup.setLayout(new GridLayout(2,false));
		trackGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		trackGroup.setText(TuxGuitar.getProperty("abc.options.select-track.tip"));
		
		final Label trackLabel = new Label(trackGroup, SWT.NULL);
		trackLabel.setText(TuxGuitar.getProperty("abc.options.select-track") + ":");
		trackLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		final Combo trackCombo = new Combo(trackGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		trackCombo.setLayoutData(getComboData());
		for(int number = 1; number <= this.song.countTracks(); number ++){
			trackCombo.add(TuxGuitar.getInstance().getSongManager().getTrack(this.song, number).getName());
		}
		trackCombo.select(TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().getNumber() - 1);
		trackCombo.setEnabled( settings.getTrack() != ABCSettings.ALL_TRACKS );
		
		final Button trackAllCheck = new Button(trackGroup,SWT.CHECK);
		trackAllCheck.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,2,1));
		trackAllCheck.setText(TuxGuitar.getProperty("abc.options.select-all-tracks"));
		trackAllCheck.setSelection( settings.getTrack() == ABCSettings.ALL_TRACKS );
		
		//------------------MEASURE RANGE------------------
		Group measureGroup = new Group(columnLeft,SWT.SHADOW_ETCHED_IN);
		measureGroup.setLayout(new GridLayout(2,false));
		measureGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		measureGroup.setText(TuxGuitar.getProperty("abc.options.measure-range.tip"));
		
		final int minSelection = 1;
		final int maxSelection = this.song.countMeasureHeaders();
		
		Label measureFromLabel = new Label(measureGroup, SWT.NULL);
		measureFromLabel.setText(TuxGuitar.getProperty("abc.options.measure-range.from"));
		measureFromLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		final Spinner measureFromSpinner = new Spinner(measureGroup, SWT.BORDER);
		measureFromSpinner.setLayoutData(getSpinnerData());
		measureFromSpinner.setMaximum(maxSelection);
		measureFromSpinner.setMinimum(minSelection);
		measureFromSpinner.setSelection(minSelection);
		
		Label measureToLabel = new Label(measureGroup, SWT.NULL);
		measureToLabel.setText(TuxGuitar.getProperty("abc.options.measure-range.to"));
		measureToLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		final Spinner measureToSpinner = new Spinner(measureGroup, SWT.BORDER);
		measureToSpinner.setLayoutData(getSpinnerData());
		measureToSpinner.setMinimum(minSelection);
		measureToSpinner.setMaximum(maxSelection);
		measureToSpinner.setSelection(maxSelection);
		
		measureFromSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int fromSelection = measureFromSpinner.getSelection();
				int toSelection = measureToSpinner.getSelection();
				
				if(fromSelection < minSelection){
					measureFromSpinner.setSelection(minSelection);
				}else if(fromSelection > toSelection){
					measureFromSpinner.setSelection(toSelection);
				}
			}
		});
		measureToSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int toSelection = measureToSpinner.getSelection();
				int fromSelection = measureFromSpinner.getSelection();
				if(toSelection < fromSelection){
					measureToSpinner.setSelection(fromSelection);
				}else if(toSelection > maxSelection){
					measureToSpinner.setSelection(maxSelection);
				}
			}
		});
		
		//------------------LAYOUT OPTIONS------------------
		Group layoutGroup = new Group(columnRight,SWT.SHADOW_ETCHED_IN);
		layoutGroup.setLayout(new GridLayout());
		layoutGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		layoutGroup.setText(TuxGuitar.getProperty("abc.options.layout.tip"));
		
		final Button scoreCheck = new Button(layoutGroup,SWT.CHECK);
		scoreCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		scoreCheck.setText(TuxGuitar.getProperty("abc.options.layout.enable-score"));
		scoreCheck.setSelection(settings.isScoreEnabled());
		
		final Button tablatureCheck = new Button(layoutGroup,SWT.CHECK);
		tablatureCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		tablatureCheck.setText(TuxGuitar.getProperty("abc.options.layout.enable-tablature"));
		tablatureCheck.setSelection(settings.isTablatureEnabled());
		
		final Button trackGroupCheck = new Button(layoutGroup,SWT.CHECK);
		trackGroupCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		trackGroupCheck.setText(TuxGuitar.getProperty("abc.options.layout.enable-track-groups"));
		trackGroupCheck.setSelection(settings.isTrackGroupEnabled());
		trackGroupCheck.setEnabled(settings.getTrack() == ABCSettings.ALL_TRACKS);
		
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
		
		final Label diagramTrackLabel = new Label(layoutGroup, SWT.NULL);
		diagramTrackLabel.setText(TuxGuitar.getProperty("abc.options.layout.chord-diagrams-track"));
		diagramTrackLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		final Combo diagramTrackCombo = new Combo(layoutGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		diagramTrackCombo.setLayoutData(getComboData());
		diagramTrackCombo.add("");
		for(int number = 1; number <= this.song.countTracks(); number ++){
			diagramTrackCombo.add(TuxGuitar.getInstance().getSongManager().getTrack(this.song, number).getName());
		}
		diagramTrackCombo.select(TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().getNumber());
		diagramTrackCombo.setEnabled( settings.isChordDiagramEnabled() );
		
		final Label chordTrackLabel = new Label(layoutGroup, SWT.NULL);
		chordTrackLabel.setText(TuxGuitar.getProperty("abc.options.layout.chord-track"));
		chordTrackLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		final Combo chordTrackCombo = new Combo(layoutGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		chordTrackCombo.setLayoutData(getComboData());
		chordTrackCombo.add("");
		for(int number = 1; number <= this.song.countTracks(); number ++){
			chordTrackCombo.add(TuxGuitar.getInstance().getSongManager().getTrack(this.song, number).getName());
		}
		chordTrackCombo.select(TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().getNumber());
		chordTrackCombo.setEnabled( settings.isChordEnabled() );
		
		final Label baseTrackLabel = new Label(layoutGroup, SWT.NULL);
		baseTrackLabel.setText(TuxGuitar.getProperty("abc.options.layout.base-track"));
		baseTrackLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		final Combo baseTrackCombo = new Combo(layoutGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		baseTrackCombo.setLayoutData(getComboData());
		baseTrackCombo.add("");
		for(int number = 1; number <= this.song.countTracks(); number ++){
			baseTrackCombo.add(TuxGuitar.getInstance().getSongManager().getTrack(this.song, number).getName());
		}
		baseTrackCombo.select(TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().getNumber());
		baseTrackCombo.setEnabled( settings.isChordEnabled() );
		
		final Label droneTrackLabel = new Label(layoutGroup, SWT.NULL);
		droneTrackLabel.setText(TuxGuitar.getProperty("abc.options.layout.drone-track"));
		droneTrackLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		final Combo droneTrackCombo = new Combo(layoutGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		droneTrackCombo.setLayoutData(getComboData());
		droneTrackCombo.add("");
		for(int number = 1; number <= this.song.countTracks(); number ++){
			droneTrackCombo.add(TuxGuitar.getInstance().getSongManager().getTrack(this.song, number).getName());
		}
		droneTrackCombo.select(TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().getNumber());
		droneTrackCombo.setEnabled( settings.isDroneEnabled() );
		
		Label measuresPerLineLabel = new Label(layoutGroup, SWT.NULL);
		measuresPerLineLabel.setText(TuxGuitar.getProperty("abc.options.layout.measures-per-line"));
		measuresPerLineLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		final Spinner measuresPerLineSpinner = new Spinner(layoutGroup, SWT.BORDER);
		measuresPerLineSpinner.setLayoutData(getSpinnerData());
		measuresPerLineSpinner.setMaximum(5);
		measuresPerLineSpinner.setMinimum(-1);
		measuresPerLineSpinner.setSelection(3);

		tablatureCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if(!tablatureCheck.getSelection()){
					scoreCheck.setSelection(true);
				}
			}
		});
		scoreCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if(!scoreCheck.getSelection()){
					tablatureCheck.setSelection(true);
				}
			}
		});
		trackAllCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				trackLabel.setEnabled( !trackAllCheck.getSelection() );
				trackCombo.setEnabled( !trackAllCheck.getSelection() );
				trackGroupCheck.setEnabled( trackAllCheck.getSelection() );
			}
		});
		chordDiagramsCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				diagramTrackCombo.setEnabled( chordDiagramsCheck.getSelection() );
			}
		});
		chordTracksCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				chordTrackCombo.setEnabled( chordTracksCheck.getSelection() );
				baseTrackCombo.setEnabled( chordTracksCheck.getSelection() );
			}
		});
		droneTrackCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				droneTrackCombo.setEnabled( droneTrackCheck.getSelection() );
			}
		});
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true,2,1));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {				
				settings.setX(xNumSpinner.getSelection());
				settings.setTrack( trackAllCheck.getSelection()?ABCSettings.ALL_TRACKS:trackCombo.getSelectionIndex() + 1);
				settings.setTrackGroupEnabled( trackAllCheck.getSelection()? trackGroupCheck.getSelection() : false);
				settings.setTrackNameEnabled( trackNameCheck.getSelection() );
				settings.setMeasureFrom(measureFromSpinner.getSelection());
				settings.setMeasureTo(measureToSpinner.getSelection());
				settings.setScoreEnabled(scoreCheck.getSelection());
				settings.setTablatureEnabled(tablatureCheck.getSelection());
				settings.setChordDiagramEnabled(chordDiagramsCheck.getSelection());
				settings.setChordEnabled(chordTracksCheck.getSelection());
				settings.setDroneEnabled(droneTrackCheck.getSelection());
				settings.setLyricsEnabled(lyricsCheck.getSelection());
				settings.setTextEnabled(textsCheck.getSelection());
				settings.setMeasuresPerLine(measuresPerLineSpinner.getSelection());
				settings.setInstrumentsStartAt1(instrCheck.getSelection());
				if(settings.isChordDiagramEnabled()) {
					settings.setDiagramTrack(diagramTrackCombo.getSelectionIndex());
				}
				else {
					settings.setDiagramTrack(ABCSettings.NO_TRACK);
				}
				if(settings.isChordEnabled()) {
					settings.setChordTrack(chordTrackCombo.getSelectionIndex());
					settings.setBaseTrack(baseTrackCombo.getSelectionIndex());
				}
				else {
					settings.setChordTrack(ABCSettings.NO_TRACK);
					settings.setBaseTrack(ABCSettings.NO_TRACK);
				}
				if(settings.isDroneEnabled()) {
					settings.setDroneTrack(droneTrackCombo.getSelectionIndex());
				}
				else {
					settings.setDroneTrack(ABCSettings.NO_TRACK);
				}
				settings.check();
				
				dialog.dispose();
				onSuccess.run();
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
		
		SWTDialogUtils.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private GridLayout getColumnLayout(){
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		return layout;
	}
	
	private GridData getComboData(){
		GridData data = new GridData(SWT.FILL,SWT.CENTER,true,true);
		data.minimumWidth = 120;
		return data;
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

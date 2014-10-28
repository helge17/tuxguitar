package org.herac.tuxguitar.io.lilypond;

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
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.song.models.TGSong;

public class LilypondSettingsDialog {
	
	private static final int STATUS_NONE = 0;
	
	private static final int STATUS_CANCELLED = 1;
	
	private static final int STATUS_ACCEPTED = 2;
	
	private TGSong song;
	
	protected int status;
	
	public LilypondSettingsDialog(TGSong song){
		this.song = song;
	}
	
	public LilypondSettings open() {
		this.status = STATUS_NONE;
		final LilypondSettings settings = LilypondSettings.getDefaults();
		
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout(2, false));
		dialog.setText(TuxGuitar.getProperty("lilypond.options"));
		
		Composite columnLeft = new Composite(dialog, SWT.NONE);
		columnLeft.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		columnLeft.setLayout(getColumnLayout());
		
		Composite columnRight = new Composite(dialog, SWT.NONE);
		columnRight.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		columnRight.setLayout(getColumnLayout());
		
		//------------------TRACK SELECTION------------------
		Group trackGroup = new Group(columnLeft ,SWT.SHADOW_ETCHED_IN);
		trackGroup.setLayout(new GridLayout(2,false));
		trackGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		trackGroup.setText(TuxGuitar.getProperty("lilypond.options.select-track.tip"));
		
		final Label trackLabel = new Label(trackGroup, SWT.NULL);
		trackLabel.setText(TuxGuitar.getProperty("lilypond.options.select-track") + ":");
		trackLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		final Combo trackCombo = new Combo(trackGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		trackCombo.setLayoutData(getComboData());
		for(int number = 1; number <= this.song.countTracks(); number ++){
			trackCombo.add(TuxGuitar.getInstance().getSongManager().getTrack(this.song, number).getName());
		}
		trackCombo.select(TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().getNumber() - 1);
		trackCombo.setEnabled( settings.getTrack() != LilypondSettings.ALL_TRACKS );
		
		final Button trackAllCheck = new Button(trackGroup,SWT.CHECK);
		trackAllCheck.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,2,1));
		trackAllCheck.setText(TuxGuitar.getProperty("lilypond.options.select-all-tracks"));
		trackAllCheck.setSelection( settings.getTrack() == LilypondSettings.ALL_TRACKS );
		
		//------------------MEASURE RANGE------------------
		Group measureGroup = new Group(columnLeft,SWT.SHADOW_ETCHED_IN);
		measureGroup.setLayout(new GridLayout(2,false));
		measureGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		measureGroup.setText(TuxGuitar.getProperty("lilypond.options.measure-range.tip"));
		
		final int minSelection = 1;
		final int maxSelection = this.song.countMeasureHeaders();
		
		Label measureFromLabel = new Label(measureGroup, SWT.NULL);
		measureFromLabel.setText(TuxGuitar.getProperty("lilypond.options.measure-range.from"));
		measureFromLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		final Spinner measureFromSpinner = new Spinner(measureGroup, SWT.BORDER);
		measureFromSpinner.setLayoutData(getSpinnerData());
		measureFromSpinner.setMaximum(maxSelection);
		measureFromSpinner.setMinimum(minSelection);
		measureFromSpinner.setSelection(minSelection);
		
		Label measureToLabel = new Label(measureGroup, SWT.NULL);
		measureToLabel.setText(TuxGuitar.getProperty("lilypond.options.measure-range.to"));
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
		
		//------------------VERSION OPTIONS------------------
		Group versionGroup = new Group(columnRight,SWT.SHADOW_ETCHED_IN);
		versionGroup.setLayout(new GridLayout());
		versionGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		versionGroup.setText(TuxGuitar.getProperty("lilypond.options.format-version"));
		
		final Text lilyVersion = new Text(versionGroup, SWT.LEFT | SWT.BORDER);
		lilyVersion.setText(settings.getLilypondVersion());
		lilyVersion.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		//------------------LAYOUT OPTIONS------------------
		Group layoutGroup = new Group(columnRight,SWT.SHADOW_ETCHED_IN);
		layoutGroup.setLayout(new GridLayout());
		layoutGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		layoutGroup.setText(TuxGuitar.getProperty("lilypond.options.layout.tip"));
		
		final Button scoreCheck = new Button(layoutGroup,SWT.CHECK);
		scoreCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		scoreCheck.setText(TuxGuitar.getProperty("lilypond.options.layout.enable-score"));
		scoreCheck.setSelection(settings.isScoreEnabled());
		
		final Button tablatureCheck = new Button(layoutGroup,SWT.CHECK);
		tablatureCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		tablatureCheck.setText(TuxGuitar.getProperty("lilypond.options.layout.enable-tablature"));
		tablatureCheck.setSelection(settings.isTablatureEnabled());
		
		final Button trackGroupCheck = new Button(layoutGroup,SWT.CHECK);
		trackGroupCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		trackGroupCheck.setText(TuxGuitar.getProperty("lilypond.options.layout.enable-track-groups"));
		trackGroupCheck.setSelection(settings.isTrackGroupEnabled());
		trackGroupCheck.setEnabled(settings.getTrack() == LilypondSettings.ALL_TRACKS);
		
		final Button trackNameCheck = new Button(layoutGroup,SWT.CHECK);
		trackNameCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		trackNameCheck.setSelection(settings.isTrackNameEnabled());
		trackNameCheck.setText(TuxGuitar.getProperty("lilypond.options.layout.enable-track-names"));

		final Button lyricsCheck = new Button(layoutGroup,SWT.CHECK);
		lyricsCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		lyricsCheck.setSelection(settings.isLyricsEnabled());
		lyricsCheck.setText(TuxGuitar.getProperty("lilypond.options.layout.enable-lyrics"));

		final Button textsCheck = new Button(layoutGroup,SWT.CHECK);
		textsCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		textsCheck.setSelection(settings.isTextEnabled());
		textsCheck.setText(TuxGuitar.getProperty("lilypond.options.layout.enable-texts"));
		
		final Button chordDiagramsCheck = new Button(layoutGroup,SWT.CHECK);
		chordDiagramsCheck.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		chordDiagramsCheck.setSelection(settings.isChordDiagramEnabled());
		chordDiagramsCheck.setText(TuxGuitar.getProperty("lilypond.options.layout.enable-chord-diagrams"));
		
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
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true,2,1));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				LilypondSettingsDialog.this.status = STATUS_ACCEPTED;
				
				settings.setTrack( trackAllCheck.getSelection()?LilypondSettings.ALL_TRACKS:trackCombo.getSelectionIndex() + 1);
				settings.setTrackGroupEnabled( trackAllCheck.getSelection()? trackGroupCheck.getSelection() : false);
				settings.setTrackNameEnabled( trackNameCheck.getSelection() );
				settings.setMeasureFrom(measureFromSpinner.getSelection());
				settings.setMeasureTo(measureToSpinner.getSelection());
				settings.setScoreEnabled(scoreCheck.getSelection());
				settings.setTablatureEnabled(tablatureCheck.getSelection());
				settings.setChordDiagramEnabled(chordDiagramsCheck.getSelection());
				settings.setLyricsEnabled(lyricsCheck.getSelection());
				settings.setTextEnabled(textsCheck.getSelection());
				settings.setLilypondVersion(lilyVersion.getText());
				settings.check();
				
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				LilypondSettingsDialog.this.status = STATUS_CANCELLED;
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

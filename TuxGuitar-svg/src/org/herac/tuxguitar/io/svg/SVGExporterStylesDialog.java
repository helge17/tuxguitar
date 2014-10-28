package org.herac.tuxguitar.io.svg;

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
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.song.models.TGSong;

public class SVGExporterStylesDialog extends SVGExporterStyles {
	
	public SVGExporterStylesDialog(){
		super();
	}
	
	public void configure() {
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("options"));
		
		//------------------TRACK SELECTION------------------
		Group trackGroup = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		trackGroup.setLayout(new GridLayout(2,false));
		trackGroup.setLayoutData(getGroupData());
		trackGroup.setText(TuxGuitar.getProperty("track"));
		
		final Label trackLabel = new Label(trackGroup, SWT.NULL);
		trackLabel.setText(TuxGuitar.getProperty("track"));
		
		final Combo trackCombo = new Combo(trackGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		trackCombo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		TGSong song = TuxGuitar.getInstance().getDocumentManager().getSong();
		for(int number = 1; number <= song.countTracks(); number ++){
			trackCombo.add(TuxGuitar.getInstance().getSongManager().getTrack(song, number).getName());
		}
		trackCombo.select(TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().getNumber() - 1);
		
		final Button trackAllCheck = new Button(trackGroup,SWT.CHECK);
		trackAllCheck.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,2,1));
		trackAllCheck.setText(TuxGuitar.getProperty("export.all-tracks"));
		trackAllCheck.setSelection(false);
		trackAllCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				trackLabel.setEnabled( !trackAllCheck.getSelection() );
				trackCombo.setEnabled( !trackAllCheck.getSelection() );
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
				int track = (!trackAllCheck.getSelection() ? (trackCombo.getSelectionIndex() + 1) : -1);
				boolean showScore = scoreEnabled.getSelection();
				boolean showTablature = tablatureEnabled.getSelection();
				boolean showChordName = chordNameEnabled.getSelection();
				boolean showChordDiagram = chordDiagramEnabled.getSelection();
				
				configure(track, showScore, showTablature, showChordName, showChordDiagram);
				
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
	}
	
	public void configure(int track, boolean showScore,boolean showTablature,boolean showChordName,boolean showChordDiagram) {
		this.configureWithDefaults();
		this.setTrack( track );
		this.setFlags( TGLayout.DISPLAY_COMPACT );
		if( showScore ){
			this.setFlags( this.getFlags() | TGLayout.DISPLAY_SCORE );
		}
		if( showTablature ){
			this.setFlags( this.getFlags() | TGLayout.DISPLAY_TABLATURE );
		}
		if( showChordName ){
			this.setFlags( this.getFlags() | TGLayout.DISPLAY_CHORD_NAME );
		}
		if( showChordDiagram ){
			this.setFlags( this.getFlags() | TGLayout.DISPLAY_CHORD_DIAGRAM );
		}
		if( track < 0 ){
			this.setFlags( this.getFlags() | TGLayout.DISPLAY_MULTITRACK );
		}
		this.setConfigured(true);
	}
	
	private static GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	private static GridData getGroupData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 300;
		return data;
	}
}

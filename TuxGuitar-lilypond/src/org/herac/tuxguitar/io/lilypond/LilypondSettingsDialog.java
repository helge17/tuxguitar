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
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class LilypondSettingsDialog {
	
	private static final int STATUS_NONE = 0;
	
	private static final int STATUS_CANCELLED = 1;
	
	private static final int STATUS_ACCEPTED = 2;
	
	protected int status;
	
	protected LilypondSettings settings;
		
	public LilypondSettingsDialog(){
		this.settings = new LilypondSettings();
	}

	public LilypondSettings open() {
		this.status = STATUS_NONE;
		
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);  
		dialog.setLayout(new GridLayout());
		dialog.setText("Options");
        
		//------------------TRACK SELECTION------------------
		Group trackGroup = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		trackGroup.setLayout(new GridLayout(2,false));
		trackGroup.setLayoutData(getGroupData());           
		trackGroup.setText("Track options");
        
		final Label trackLabel = new Label(trackGroup, SWT.NULL);            
		trackLabel.setText("Export track:");   
                
		final Combo trackCombo = new Combo(trackGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		trackCombo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		for(int number = 1; number <= TuxGuitar.instance().getSongManager().getSong().countTracks(); number ++){
			trackCombo.add(TuxGuitar.instance().getSongManager().getTrack(number).getName());        	
		}
		trackCombo.select(TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getTrack().getNumber() - 1);

		final Button trackAllCheck = new Button(trackGroup,SWT.CHECK);
		trackAllCheck.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,true,2,1));
		trackAllCheck.setText("Export all tracks");
			
		final Button trackGroupCheck = new Button(trackGroup,SWT.CHECK);
		trackGroupCheck.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,true,2,1));
		trackGroupCheck.setText("Export grouped tracks");
		trackGroupCheck.setEnabled(false);
		
		final Button trackNameCheck = new Button(trackGroup,SWT.CHECK);
		trackNameCheck.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,true,2,1));
		trackNameCheck.setText("Export track names");		
		
		trackAllCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				trackLabel.setEnabled( !trackAllCheck.getSelection() );
				trackCombo.setEnabled( !trackAllCheck.getSelection() );
				trackGroupCheck.setEnabled( trackAllCheck.getSelection() );
			}
		});			
		
		//------------------MEASURE RANGE------------------
		Group measureGroup = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		measureGroup.setLayout(new GridLayout(2,false));
		measureGroup.setLayoutData(getGroupData());               
		measureGroup.setText("Measure range");
        
		final int minSelection = 1;
		final int maxSelection = TuxGuitar.instance().getSongManager().getSong().countMeasureHeaders();
            
		Label measureFromLabel = new Label(measureGroup, SWT.NULL);            
		measureFromLabel.setText("From");                        
		final Spinner measureFromSpinner = new Spinner(measureGroup, SWT.BORDER);        
		measureFromSpinner.setLayoutData(getSpinnerData());              
		measureFromSpinner.setMaximum(maxSelection);
		measureFromSpinner.setMinimum(minSelection);
		measureFromSpinner.setSelection(minSelection);
            
		Label measureToLabel = new Label(measureGroup, SWT.NULL);
		measureToLabel.setText("To");
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
		
		//------------------BUTTONS--------------------------            
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));    	
            
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);    
		data.minimumWidth = 80;
		data.minimumHeight = 25;     
            
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(data);
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				LilypondSettingsDialog.this.status = STATUS_ACCEPTED;
					
				LilypondSettingsDialog.this.settings.setTrack( trackAllCheck.getSelection()?LilypondSettings.ALL_TRACKS:trackCombo.getSelectionIndex() + 1);
				LilypondSettingsDialog.this.settings.setTrackGroupEnabled( trackAllCheck.getSelection()? trackGroupCheck.getSelection() : false);
				LilypondSettingsDialog.this.settings.setTrackNameEnabled( trackNameCheck.getSelection() );
				LilypondSettingsDialog.this.settings.setMeasureFrom(measureFromSpinner.getSelection());
				LilypondSettingsDialog.this.settings.setMeasureTo(measureToSpinner.getSelection());
					
				dialog.dispose();
			}
		});

		Button buttonCancel = new Button(buttons, SWT.PUSH);            
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(data);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				LilypondSettingsDialog.this.status = STATUS_CANCELLED;
				dialog.dispose();
			}
		});

		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
    	  
		return ((this.status == STATUS_ACCEPTED)?LilypondSettingsDialog.this.settings:null);
	}	
    
	private GridData getSpinnerData(){
    	GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
    	data.minimumWidth = 60;
    	return data;
    }
	
    private GridData getGroupData(){
    	GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
    	data.minimumWidth = 300;
    	return data;
    }	

}

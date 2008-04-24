/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.measure;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.clipboard.MeasureTransferable;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.editors.tab.TGTrackImpl;
import org.herac.tuxguitar.gui.util.DialogUtils;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CopyMeasureAction extends Action{
	public static final String NAME = "action.measure.copy";
	
	protected boolean copyAllTracks;
	
	public CopyMeasureAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		showDialog(getEditor().getTablature().getShell());
		
		return 0;
	}
	
	public void showDialog(Shell shell) {
		TGTrackImpl track = getEditor().getTablature().getCaret().getTrack();
		TGMeasureImpl measure = getEditor().getTablature().getCaret().getMeasure();
		if (measure != null) {
			final Shell dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			dialog.setLayout(new GridLayout());
			dialog.setText(TuxGuitar.getProperty("edit.copy"));
			
			//----------------------------------------------------------------------
			Group range = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			range.setLayout(new GridLayout(2,false));
			range.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			range.setText(TuxGuitar.getProperty("edit.copy"));
			
			int measureCount = getSongManager().getSong().countMeasureHeaders();
			
			Label fromLabel = new Label(range, SWT.NULL);
			fromLabel.setText(TuxGuitar.getProperty("edit.from"));
			final Spinner fromSpinner = new Spinner(range, SWT.BORDER);
			fromSpinner.setLayoutData(getSpinnerData());
			fromSpinner.setMinimum(1);
			fromSpinner.setMaximum(measureCount);
			fromSpinner.setSelection(measure.getNumber());
			
			Label toLabel = new Label(range, SWT.NULL);
			toLabel.setText(TuxGuitar.getProperty("edit.to"));
			final Spinner toSpinner = new Spinner(range, SWT.BORDER);
			toSpinner.setLayoutData(getSpinnerData());
			toSpinner.setMinimum(1);
			toSpinner.setMaximum(measureCount);
			toSpinner.setSelection(measure.getNumber());
			
			final int minSelection = 1;
			final int maxSelection = track.countMeasures();
			
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
			//----------------------------------------------------------------------
			this.copyAllTracks = true;
			if(getSongManager().getSong().countTracks() > 1){
				Group checkComposites = new Group(dialog,SWT.SHADOW_ETCHED_IN);
				checkComposites.setLayout(new GridLayout());
				checkComposites.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
				checkComposites.setText(TuxGuitar.getProperty("options"));
				
				final Button allTracks = new Button(checkComposites,SWT.CHECK);
				allTracks.setText(TuxGuitar.getProperty("edit.all-tracks"));
				allTracks.setSelection(this.copyAllTracks);
				allTracks.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						CopyMeasureAction.this.copyAllTracks = allTracks.getSelection();
					}
				});
			}
			//------------------BUTTONS--------------------------
			Composite buttons = new Composite(dialog, SWT.NONE);
			buttons.setLayout(new GridLayout(2,false));
			buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
			
			final Button buttonOK = new Button(buttons, SWT.PUSH);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setLayoutData(getButtonData());
			buttonOK.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					copyMeasures(fromSpinner.getSelection(),toSpinner.getSelection(),CopyMeasureAction.this.copyAllTracks);
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
			
			DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		}
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected GridData getSpinnerData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 180;
		return data;
	}
	
	protected void copyMeasures(int m1,int m2,boolean allTracks){
		if(m1 > 0 && m1 <= m2){
			MeasureTransferable transferable = new MeasureTransferable(getEditor(),m1,m2,allTracks);
			getEditor().getClipBoard().addTransferable(transferable);
		}
	}
}

package org.herac.tuxguitar.gui.editors.chord;

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

/**
 * Dialog for customizing chord criteria parameters
 * 
 * @author Nikola Kolarovic
 *
 */
public class ChordSettingsDialog {
	
	private boolean updated;
	private Shell dialog;
	private Button emptyStringChords = null;
	private Spinner chordsToDisplay = null;
	private Combo typeCombo = null;
	private Spinner minFret = null;
	private Spinner maxFret = null;
	
	public ChordSettingsDialog() {
		super();
	}
	
	public boolean open(Shell parent){
		this.updated = false;
		
		this.dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.dialog.setLayout(new GridLayout());
		this.dialog.setText(TuxGuitar.getProperty("settings"));
		this.init();
		DialogUtils.openDialog(this.dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		
		return this.updated;
	}
	
	protected void init() {
		Group group = new Group(this.dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("chord.settings.tip"));
		
		Composite composite = new Composite(group,SWT.NONE);
		composite.setLayout(new GridLayout(2,false));
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		initTypeCombo(composite);
		initChordsToDisplay(composite);
		initEmptyStringChords(composite);
		initFretSearch(composite);
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(this.dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dispose(true);
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dispose(false);
			}
		});
		
		this.dialog.setDefaultButton( buttonOK );
	}
	
	private GridData getGridData(int minimumWidth, int minimumHeight){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = minimumWidth;
		data.minimumHeight = minimumHeight;
		return data;
	}
	
	private GridData getGridData(){
		return getGridData(125,0);
	}
	
	private GridData getButtonData(){
		return getGridData(80,25);
	}
	
	private Spinner makeSpinner(Composite parent,String label,int value, int min, int max){
		this.newLabel(parent,label);
		Spinner spinner = new Spinner(parent,SWT.BORDER);
		spinner.setMinimum(min);
		spinner.setMaximum(max);
		spinner.setSelection(value);
		spinner.setLayoutData(getGridData());
		return spinner;
	}
	
	private Label newLabel(Composite parent,String text){
		Label label = new Label(parent,SWT.HORIZONTAL);
		label.setText(text);
		return label;
	}
	
	private void initTypeCombo(Composite parent) {
		this.newLabel(parent, TuxGuitar.getProperty("chord.settings.type"));
		this.typeCombo = new Combo(parent,SWT.DROP_DOWN | SWT.READ_ONLY);
		this.typeCombo.setLayoutData(getGridData());
		this.typeCombo.add(TuxGuitar.getProperty("chord.settings.type.most-common"));
		this.typeCombo.add(TuxGuitar.getProperty("chord.settings.type.inversions"));
		this.typeCombo.add(TuxGuitar.getProperty("chord.settings.type.close-voiced"));
		this.typeCombo.add(TuxGuitar.getProperty("chord.settings.type.open-voiced"));
		this.typeCombo.select(ChordSettings.instance().getChordTypeIndex());
	}
	
	private void initChordsToDisplay(Composite parent) {
		this.chordsToDisplay = makeSpinner(parent,TuxGuitar.getProperty("chord.settings.chords-to-display"),ChordSettings.instance().getChordsToDisplay(),1,100);
	}
	
	private void initEmptyStringChords(Composite parent) {
		this.emptyStringChords = new Button(parent,SWT.CHECK);
		this.emptyStringChords.setSelection(ChordSettings.instance().isEmptyStringChords());
		this.emptyStringChords.setText(TuxGuitar.getProperty("chord.settings.open-chords"));
		this.emptyStringChords.setSize(100,20);
		this.emptyStringChords.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,2,1));
	}
	
	private void initFretSearch(Composite parent) {
		Group group = new Group(parent,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(4,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,2,1));
		group.setText(TuxGuitar.getProperty("chord.settings.search-frets"));
		this.minFret = makeSpinner(group,TuxGuitar.getProperty("chord.settings.minimum-fret"),ChordSettings.instance().getFindChordsMin(),0,15);
		this.maxFret = makeSpinner(group,TuxGuitar.getProperty("chord.settings.maximum-fret"),ChordSettings.instance().getFindChordsMax(),2,25);
		this.minFret.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				checkMinimumFretValue();
			}
		});
		this.maxFret.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				checkMaximumFretValue();
			}
		});
	}
	
	protected void checkMinimumFretValue(){
		int maxSelection = this.maxFret.getSelection();
		int minSelection = this.minFret.getSelection();
		if(maxSelection < minSelection){
			this.maxFret.setSelection(minSelection);
		}
	}
	
	protected void checkMaximumFretValue(){
		int maxSelection = this.maxFret.getSelection();
		int minSelection = this.minFret.getSelection();
		if(maxSelection < minSelection){
			this.maxFret.setSelection(minSelection);
		}
	}
	
	private void update(){
		ChordSettings.instance().setChordTypeIndex(this.typeCombo.getSelectionIndex());
		ChordSettings.instance().setEmptyStringChords(this.emptyStringChords.getSelection());
		ChordSettings.instance().setChordsToDisplay(this.chordsToDisplay.getSelection() );
		ChordSettings.instance().setFindChordsMax(this.maxFret.getSelection());
		ChordSettings.instance().setFindChordsMin(this.minFret.getSelection());
	}
	
	protected void dispose(boolean updated){
		this.updated = updated;
		if(this.updated){
			this.update();
		}
		this.dialog.dispose();
	}
}

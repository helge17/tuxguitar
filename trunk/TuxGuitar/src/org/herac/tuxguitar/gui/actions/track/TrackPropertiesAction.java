/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.track;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.editors.tab.TGTrackImpl;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.undo.undoables.UndoableJoined;
import org.herac.tuxguitar.gui.undo.undoables.track.UndoableTrackGeneric;
import org.herac.tuxguitar.gui.undo.undoables.track.UndoableTrackInfo;
import org.herac.tuxguitar.gui.undo.undoables.track.UndoableTrackInstrument;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.TGMusicKeyUtils;
import org.herac.tuxguitar.player.base.MidiInstrument;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TrackPropertiesAction extends Action {
	public static final String NAME = "action.track.properties";
	
	private static final String[] NOTE_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_TUNING);
	private static final int MINIMUM_LEFT_CONTROLS_WIDTH = 180;
	private static final int MINIMUM_BUTTON_WIDTH = 80;
	private static final int MINIMUM_BUTTON_HEIGHT = 25;
	private static final int MAX_STRINGS = 7;
	private static final int MIN_STRINGS = 4;
	private static final int MAX_OCTAVES = 10;
	private static final int MAX_NOTES = 12;
	
	protected Shell dialog;
	protected Text nameText;
	protected TGColor trackColor;
	protected List tempStrings;
	protected Button stringTransposition;
	protected Button stringTranspositionTryKeepString;
	protected Button stringTranspositionApplyToChords;
	protected Spinner stringCountSpinner;
	protected Combo[] stringCombos = new Combo[MAX_STRINGS];
	protected Combo offsetCombo;
	protected int stringCount;
	protected Combo instrumentCombo;
	protected Button percussionCheckBox;
	protected Color colorButtonValue;
	
	public TrackPropertiesAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		showDialog(getEditor().getTablature().getShell());
		return 0;
	}
	
	public void showDialog(Shell shell) {
		TGTrackImpl track = getEditor().getTablature().getCaret().getTrack();
		if (track != null) {
			this.stringCount = track.getStrings().size();
			this.trackColor = track.getColor().clone(getSongManager().getFactory());
			this.initTempStrings(track.getStrings());
			
			this.dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			this.dialog.setLayout(new GridLayout(2,false));
			this.dialog.setText(TuxGuitar.getProperty("track.properties"));
			
			Composite left = new Composite(this.dialog,SWT.NONE);
			left.setLayout(new GridLayout());
			left.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			
			Composite right = new Composite(this.dialog,SWT.NONE);
			right.setLayout(new GridLayout());
			right.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			
			Composite bottom = new Composite(this.dialog, SWT.NONE);
			bottom.setLayout(new GridLayout(2,false));
			bottom.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true,2,1));
			
			//GENERAL
			initTrackInfo(makeGroup(left,1,TuxGuitar.getProperty("track.properties.general")), track);
			
			//INSTRUMENT
			initInstrumentFields(makeGroup(left,1,TuxGuitar.getProperty("instrument.instrument")), track);
			
			//TUNING
			initTuningInfo(makeGroup(right,2,TuxGuitar.getProperty("tuning")), track);
			
			//BUTTONS
			initButtons(bottom);
			
			updateTuningGroup(!track.isPercussionTrack());
			
			DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		}
	}
	
	private Group makeGroup(Composite parent,int horizontalSpan,String text){
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(makeGridData(horizontalSpan));
		group.setText(text);
		
		return group;
	}
	
	private GridData makeGridData(int horizontalSpan){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.horizontalSpan = horizontalSpan;
		return data;
	}
	
	public GridData getButtonsData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = MINIMUM_BUTTON_WIDTH;
		data.minimumHeight = MINIMUM_BUTTON_HEIGHT;
		return data;
	}
	
	private void initTrackInfo(Composite composite,TGTrackImpl track) {
		composite.setLayout(new GridLayout(/*2,false*/));
		Composite top = new Composite(composite, SWT.NONE);
		top.setLayout(new GridLayout(/*2,false*/));
		top.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		Composite bottom = new Composite(composite, SWT.NONE);
		bottom.setLayout(new GridLayout(/*2,false*/));
		bottom.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,true));
		
		//-----------------------NAME---------------------------------
		Label nameLabel = new Label(top, SWT.NONE);
		nameLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		nameLabel.setText(TuxGuitar.getProperty("track.name") + ":");
		
		this.nameText = new Text(top, SWT.BORDER);
		this.nameText.setLayoutData(getAlignmentData(MINIMUM_LEFT_CONTROLS_WIDTH,SWT.FILL));
		this.nameText.setText(track.getName());
		
		//-----------------------COLOR---------------------------------
		Label colorLabel = new Label(bottom, SWT.NONE);
		colorLabel.setText(TuxGuitar.getProperty("track.color") + ":");
		colorLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		
		final Button colorButton = new Button(bottom, SWT.PUSH);
		colorButton.setLayoutData(getAlignmentData(MINIMUM_LEFT_CONTROLS_WIDTH,SWT.FILL));
		colorButton.setText(TuxGuitar.getProperty("choose"));
		colorButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ColorDialog dlg = new ColorDialog(TrackPropertiesAction.this.dialog);
				dlg.setRGB(TrackPropertiesAction.this.dialog.getDisplay().getSystemColor(SWT.COLOR_BLACK).getRGB());
				dlg.setText(TuxGuitar.getProperty("choose-color"));
				RGB rgb = dlg.open();
				if (rgb != null) {
					TrackPropertiesAction.this.trackColor.setR(rgb.red);
					TrackPropertiesAction.this.trackColor.setG(rgb.green);
					TrackPropertiesAction.this.trackColor.setB(rgb.blue);
					TrackPropertiesAction.this.setButtonColor(colorButton);
				}
			}
		});
		colorButton.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				TrackPropertiesAction.this.disposeButtonColor();
			}
		});
		this.setButtonColor(colorButton);
	}
	
	private void initTuningInfo(Composite composite,TGTrackImpl track) {
		composite.setLayout(new GridLayout(2,false));
		initTuningData(composite,track);
		initTuningCombos(composite);
		//initTuningOptions(composite);
	}
	
	private void initTuningCombos(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(/*MAX_STRINGS, false*/));
		composite.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,false,true));
		String[] tuningTexts = getAllValueNames();
		for (int i = 0; i < MAX_STRINGS; i++) {
			this.stringCombos[i] = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
			this.stringCombos[i].setItems(tuningTexts);
		}
	}
	
	private void initTuningData(Composite parent,TGTrackImpl track) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		
		Composite top = new Composite(composite, SWT.NONE);
		top.setLayout(new GridLayout());
		top.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		
		Composite middle = new Composite(composite, SWT.NONE);
		middle.setLayout(new GridLayout());
		middle.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		
		Composite bottom = new Composite( composite , SWT.NONE );
		bottom.setLayout( new GridLayout() );
		bottom.setLayoutData( new GridData(SWT.FILL,SWT.TOP,true,true) );
		
		//---------------------------------STRING--------------------------------
		Label stringCountLabel = new Label(top, SWT.NONE);
		stringCountLabel.setText(TuxGuitar.getProperty("tuning.strings") + ":");
		stringCountLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,true));
		
		this.stringCountSpinner = new Spinner(top, SWT.BORDER);
		this.stringCountSpinner.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		//this.stringCountSpinner.setLayoutData(getAlignmentData(80,SWT.FILL));
		this.stringCountSpinner.setMinimum(MIN_STRINGS);
		this.stringCountSpinner.setMaximum(MAX_STRINGS);
		this.stringCountSpinner.setSelection(this.stringCount);
		this.stringCountSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TrackPropertiesAction.this.stringCount = TrackPropertiesAction.this.stringCountSpinner.getSelection();
				setDefaultTuning();
				updateTuningGroup(!TrackPropertiesAction.this.percussionCheckBox.getSelection());
			}
		});
		
		//---------------------------------OFFSET--------------------------------
		Label offsetLabel = new Label(middle, SWT.NONE);
		offsetLabel.setText(TuxGuitar.getProperty("tuning.offset") + ":");
		offsetLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,true));
		
		this.offsetCombo = new Combo(middle, SWT.DROP_DOWN | SWT.READ_ONLY);
		//this.offsetCombo.setLayoutData(getAlignmentData(80,SWT.LEFT));
		this.offsetCombo.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		for(int i = TGTrack.MIN_OFFSET;i <= TGTrack.MAX_OFFSET;i ++){
			this.offsetCombo.add(Integer.toString(i));
			if(i == track.getOffset()){
				this.offsetCombo.select(i - TGTrack.MIN_OFFSET);
			}
		}
		
		//---------------------------------OPTIONS----------------------------------
		this.stringTransposition = new Button( bottom , SWT.CHECK );
		this.stringTransposition.setLayoutData( new GridData(SWT.FILL,SWT.CENTER,true,true) );
		this.stringTransposition.setText(TuxGuitar.getProperty("tuning.strings.transpose"));
		this.stringTransposition.setSelection( true );
		
		this.stringTranspositionApplyToChords = new Button( bottom , SWT.CHECK );
		this.stringTranspositionApplyToChords.setLayoutData( new GridData(SWT.FILL,SWT.CENTER,true,true) );
		this.stringTranspositionApplyToChords.setText(TuxGuitar.getProperty("tuning.strings.transpose.apply-to-chords"));
		this.stringTranspositionApplyToChords.setSelection( true );
		
		this.stringTranspositionTryKeepString = new Button( bottom , SWT.CHECK );
		this.stringTranspositionTryKeepString.setLayoutData( new GridData(SWT.FILL,SWT.CENTER,true,true) );
		this.stringTranspositionTryKeepString.setText(TuxGuitar.getProperty("tuning.strings.transpose.try-keep-strings"));
		this.stringTranspositionTryKeepString.setSelection( true );
		
		this.stringTransposition.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button stringTransposition = TrackPropertiesAction.this.stringTransposition;
				Button stringTranspositionApplyToChords = TrackPropertiesAction.this.stringTranspositionApplyToChords;
				Button stringTranspositionTryKeepString = TrackPropertiesAction.this.stringTranspositionTryKeepString;
				stringTranspositionApplyToChords.setEnabled( ( stringTransposition.isEnabled() && stringTransposition.getSelection() ) );
				stringTranspositionTryKeepString.setEnabled( ( stringTransposition.isEnabled() && stringTransposition.getSelection() ) );
			}
		});
	}
	
	private GridData getAlignmentData(int minimumWidth,int horizontalAlignment){
		GridData data = new GridData();
		data.minimumWidth = minimumWidth;
		data.horizontalAlignment = horizontalAlignment;
		data.verticalAlignment = SWT.DEFAULT;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		return data;
	}
	
	private void initButtons(final Composite parent) {
		Button buttonOK = new Button(parent, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonsData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				updateTrackProperties();
				TrackPropertiesAction.this.dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(parent, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonsData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TrackPropertiesAction.this.dialog.dispose();
			}
		});
		
		this.dialog.setDefaultButton( buttonOK );
	}
	
	private void initInstrumentFields(Composite composite,TGTrackImpl track) {
		composite.setLayout(new GridLayout());
		
		Composite top = new Composite(composite, SWT.NONE);
		top.setLayout(new GridLayout());
		top.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		
		Composite bottom = new Composite(composite, SWT.NONE);
		bottom.setLayout(new GridLayout());
		bottom.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,true));
		
		//------------Instrument Combo-------------------------------------
		Label instrumentLabel = new Label(top, SWT.NONE);
		instrumentLabel.setText(TuxGuitar.getProperty("instrument.instrument") + ":");
		instrumentLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		
		this.instrumentCombo = new Combo(top, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.instrumentCombo.setLayoutData(getAlignmentData(MINIMUM_LEFT_CONTROLS_WIDTH,SWT.FILL));
		MidiInstrument[] instruments = TuxGuitar.instance().getPlayer().getInstruments();
		if (instruments != null) {
			int count = instruments.length;
			if (count > 128) {
				count = 128;
			}
			for (int i = 0; i < count; i++) {
				this.instrumentCombo.add(instruments[i].getName());
			}
			this.instrumentCombo.select(track.getChannel().getInstrument());
		}
		this.instrumentCombo.setEnabled(!track.isPercussionTrack() && instruments != null);
		
		//--------------------Precusion CheckBox-------------------------------
		this.percussionCheckBox = new Button(bottom, SWT.CHECK);
		this.percussionCheckBox.setText(TuxGuitar.getProperty("instrument.percussion-track"));
		this.percussionCheckBox.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		if (instruments != null) {
			this.percussionCheckBox.setSelection(track.isPercussionTrack());
			this.percussionCheckBox.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					TrackPropertiesAction.this.instrumentCombo.setEnabled(!TrackPropertiesAction.this.percussionCheckBox.getSelection());
					
					setDefaultTuning();
					updateTuningGroup(!TrackPropertiesAction.this.percussionCheckBox.getSelection());
				}
			});
		} else {
			this.percussionCheckBox.setEnabled(false);
		}
		//-----------------------------------------------------------
	}
	
	protected void updateTrackProperties() {
		final TGTrackImpl track = getEditor().getTablature().getCaret().getTrack();
		
		final String trackName = this.nameText.getText();
		
		final List strings = new ArrayList();
		for (int i = 0; i < this.stringCount; i++) {
			strings.add(TGSongManager.newString(getSongManager().getFactory(),(i + 1), this.stringCombos[i].getSelectionIndex()));
		}
		
		final boolean percussion = this.percussionCheckBox.getSelection();
		final int offset = ((percussion)?0:TGTrack.MIN_OFFSET + this.offsetCombo.getSelectionIndex());
		final int instrument = ((this.instrumentCombo.getSelectionIndex() >= 0)?this.instrumentCombo.getSelectionIndex():0);
		
		final TGColor trackColor = this.trackColor;
		final boolean infoChanges = hasInfoChanges(track,trackName,trackColor,offset);
		final boolean tuningChanges = hasTuningChanges(track,strings);
		final boolean instrumentChanges = hasInstrumentChanges(track,instrument,percussion);
		final boolean transposeStrings = (this.stringTransposition.getSelection() && !percussion && !track.isPercussionTrack() );
		final boolean transposeApplyToChords = (transposeStrings && this.stringTranspositionApplyToChords.getSelection());
		final boolean transposeTryKeepString = (transposeStrings && this.stringTranspositionTryKeepString.getSelection());
		
		try {
			if(infoChanges || tuningChanges || instrumentChanges){
				ActionLock.lock();
				TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
					public void run() throws Throwable {
						TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
						new Thread( new Runnable() {
							public void run() {
								TuxGuitar.instance().getFileHistory().setUnsavedFile();
								UndoableJoined undoable = new UndoableJoined();
								
								UndoableTrackGeneric undoableGeneric = null;
								if(tuningChanges){
									undoableGeneric = UndoableTrackGeneric.startUndo(track);
								}
								
								//--------------------------------------info---------------------------------------
								if(infoChanges){
									UndoableTrackInfo undoableInfo = null;
									if(!tuningChanges){
										undoableInfo = UndoableTrackInfo.startUndo(track);
									}
									getSongManager().getTrackManager().changeInfo(track,trackName,trackColor,offset);
									if(!tuningChanges && undoableInfo != null){
										undoable.addUndoableEdit(undoableInfo.endUndo(track));
									}
								}
								//--------------------------------------tuning---------------------------------------
								if(tuningChanges){
									updateTrackTunings(track, strings, transposeStrings, transposeTryKeepString, transposeApplyToChords);
								}
								//-----------------------------instrument----------------------------------------------
								if(instrumentChanges){
									UndoableTrackInstrument undoableInstrument = null;
									if(!tuningChanges){
										undoableInstrument = UndoableTrackInstrument.startUndo(track);
									}
									getSongManager().getTrackManager().changeInstrument(track,instrument,percussion);
									if(!tuningChanges && undoableInstrument != null){
										undoable.addUndoableEdit(undoableInstrument.endUndo(track));
									}
								}
								if(tuningChanges && undoableGeneric != null){
									undoable.addUndoableEdit(undoableGeneric.endUndo(track));
								}
								addUndoableEdit(undoable.endUndo());
								
								new SyncThread(new Runnable() {
									public void run() {
										if(!TuxGuitar.isDisposed()){
											updateTablature();
											TuxGuitar.instance().getMixer().updateValues();
											TuxGuitar.instance().updateCache( true );
											TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
											ActionLock.unlock();
										}
									}
								}).start();
							}
						}).start();
					}
				});
			}
		} catch (Throwable throwable) {
			TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
			ActionLock.unlock();
			throwable.printStackTrace();
		}
	}
	
	protected boolean hasInfoChanges(TGTrackImpl track,String name,TGColor color,int offset){
		if(!name.equals(track.getName())){
			return true;
		}
		if(!color.isEqual(track.getColor())){
			return true;
		}
		if(offset != track.getOffset()){
			return true;
		}
		return false;
	}
	
	protected boolean hasInstrumentChanges(TGTrackImpl track,int instrument,boolean percussion){
		return ((track.getChannel().getInstrument() != instrument) || (track.isPercussionTrack() != percussion));
	}
	
	protected boolean hasTuningChanges(TGTrackImpl track,List newStrings){
		List oldStrings = track.getStrings();
		//check the number of strings
		if(oldStrings.size() != newStrings.size()){
			return true;
		}
		//check the tuning of strings
		for(int i = 0;i < oldStrings.size();i++){
			TGString oldString = (TGString)oldStrings.get(i);
			boolean stringExists = false;
			for(int j = 0;j < newStrings.size();j++){
				TGString newString = (TGString)newStrings.get(j);
				if(newString.isEqual(oldString)){
					stringExists = true;
				}
			}
			if(!stringExists){
				return true;
			}
		}
		return false;
	}
	
	protected void updateTrackTunings(TGTrackImpl track, List strings, boolean transposeStrings , boolean transposeTryKeepString , boolean transposeApplyToChords ){
		int[] transpositions = getStringTranspositions(track, strings);
		getSongManager().getTrackManager().changeInstrumentStrings(track,strings);
		if( transposeStrings ){
			getSongManager().getTrackManager().transposeNotes(track, transpositions, transposeTryKeepString, transposeApplyToChords );
		}
	}
	
	protected int[] getStringTranspositions(TGTrackImpl track, List newStrings ){
		int[] transpositions = new int[ newStrings.size() ];
		
		TGString newString = null;
		TGString oldString = null;
		for( int index = 0; index < transpositions.length ; index ++ ){
			for( int i = 0; i < track.stringCount() ; i ++ ){
				TGString string = track.getString( i + 1 );
				if( string.getNumber() == (index + 1) ){
					oldString = string;
					break;
				}
			}
			for( int i = 0; i < newStrings.size() ; i ++ ){
				TGString string = (TGString)newStrings.get( i );
				if( string.getNumber() == (index + 1) ){
					newString = string;
					break;
				}
			}
			if( oldString != null && newString != null ){
				transpositions[ index ] = (oldString.getValue() - newString.getValue());
			}else{
				transpositions[ index ] = 0;
			}
			
			newString = null;
			oldString = null;
		}
		
		return transpositions;
	}
	
	protected void setButtonColor(Button button){
		Color color = new Color(this.dialog.getDisplay(), this.trackColor.getR(), this.trackColor.getG(), this.trackColor.getB());
		button.setForeground( color );
		this.disposeButtonColor();
		this.colorButtonValue = color;
	}
	
	protected void disposeButtonColor(){
		if(this.colorButtonValue != null && !this.colorButtonValue.isDisposed()){
			this.colorButtonValue.dispose();
			this.colorButtonValue = null;
		}
	}
	
	protected void updateTuningGroup(boolean enabled) {
		for (int i = 0; i < this.tempStrings.size(); i++) {
			TGString string = (TGString)this.tempStrings.get(i);
			this.stringCombos[i].select(string.getValue());
			this.stringCombos[i].setVisible(true);
			this.stringCombos[i].setEnabled(enabled);
		}
		
		for (int i = this.tempStrings.size(); i < MAX_STRINGS; i++) {
			this.stringCombos[i].select(0);
			this.stringCombos[i].setVisible(false);
		}
		this.offsetCombo.setEnabled(enabled);
		this.stringTransposition.setEnabled(enabled);
		this.stringTranspositionApplyToChords.setEnabled(enabled && this.stringTransposition.getSelection());
		this.stringTranspositionTryKeepString.setEnabled(enabled && this.stringTransposition.getSelection());
	}
	
	protected void initTempStrings(List realStrings) {
		this.tempStrings = new ArrayList();
		for (int i = 0; i < realStrings.size(); i++) {
			TGString realString = (TGString) realStrings.get(i);
			this.tempStrings.add(realString.clone(getSongManager().getFactory()));
		}
	}
	
	protected void setDefaultTuning() {
		this.tempStrings.clear();
		if (this.percussionCheckBox.getSelection()) {
			for (int i = 1; i <= this.stringCount; i++) {
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),i, 0));
			}
		}
		else {
			switch (this.stringCount) {
			case 7:
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),1, 64));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),2, 59));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),3, 55));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),4, 50));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),5, 45));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),6, 40));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),7, 35));
				break;
			case 6:
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),1, 64));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),2, 59));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),3, 55));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),4, 50));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),5, 45));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),6, 40));
				break;
			case 5:
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),1, 43));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),2, 38));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),3, 33));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),4, 28));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),5, 23));
				break;
			case 4:
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),1, 43));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),2, 38));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),3, 33));
				this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),4, 28));
				break;
			default:
				for( int i = 1 ; i <= this.stringCount ; i ++ ){
					this.tempStrings.add(TGSongManager.newString(getSongManager().getFactory(),i,0));
				}
				break;
			}
		}
	}
	
	protected String[] getAllValueNames() {
		String[] valueNames = new String[MAX_NOTES * MAX_OCTAVES];
		for (int i = 0; i < valueNames.length; i++) {
			valueNames[i] =  NOTE_NAMES[ (i -  ((i / MAX_NOTES) * MAX_NOTES) ) ] + (i / MAX_NOTES);
		}
		return valueNames;
	}
}
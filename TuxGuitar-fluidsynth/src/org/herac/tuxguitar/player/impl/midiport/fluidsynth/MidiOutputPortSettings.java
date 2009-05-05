package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class MidiOutputPortSettings extends MidiSettings {
	
	private static final int TABLE_WIDTH = 350;
	private static final int TABLE_HEIGHT = 200;
	
	public MidiOutputPortSettings(MidiOutputPortProviderImpl provider){
		super( provider );
	}
	
	public void configure(Shell parent) {
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setText(TuxGuitar.getProperty("fluidsynth.settings"));
		dialog.setLayout(new GridLayout());
		dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		TabFolder tabs = new TabFolder(dialog, SWT.TOP);
		tabs.setLayout( new FormLayout() );
		
		// ----------------------------------------------------------------------
		Composite cSoundfonts = new Composite( tabs , SWT.NONE);
		cSoundfonts.setLayout(getGridLayout( 2 ));
		cSoundfonts.setLayoutData(new FormData());
		
		TabItem tSoundfonts = new TabItem( tabs  , SWT.None ); 
		tSoundfonts.setText(TuxGuitar.getProperty("fluidsynth.settings.soundfonts"));
		tSoundfonts.setControl(cSoundfonts);
		
		Composite cSoundfontsTable = new Composite(cSoundfonts, SWT.NONE);
		cSoundfontsTable.setLayout(new GridLayout());
		cSoundfontsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final Table soundfontsTable = new Table(cSoundfontsTable, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		soundfontsTable.setLayoutData(new GridData(TABLE_WIDTH,TABLE_HEIGHT));
		soundfontsTable.setHeaderVisible(true);
		
		TableColumn soundfontsTableColumn = new TableColumn(soundfontsTable, SWT.NONE);
		soundfontsTableColumn.setWidth(TABLE_WIDTH);
		soundfontsTableColumn.setText(TuxGuitar.getProperty("fluidsynth.settings.soundfonts.list"));
		
		Composite cSoundfontsButtons = new Composite(cSoundfonts, SWT.NONE);
		cSoundfontsButtons.setLayout(new GridLayout());
		cSoundfontsButtons.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Button buttonAdd = new Button(cSoundfontsButtons, SWT.PUSH);
		buttonAdd.setLayoutData(getButtonData(SWT.FILL,SWT.TOP, true,false));
		buttonAdd.setText(TuxGuitar.getProperty("add"));
		buttonAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addMidiPort(soundfontsTable);
			}
		});
		
		Button buttonDelete = new Button(cSoundfontsButtons, SWT.PUSH);
		buttonDelete.setText(TuxGuitar.getProperty("remove"));
		buttonDelete.setLayoutData(getButtonData(SWT.FILL,SWT.TOP, true,false));
		buttonDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				removeMidiPort(soundfontsTable);
			}
		});
		
		this.addMidiPorts( soundfontsTable);
		
		// ----------------------------------------------------------------------
		Composite cSettings = new Composite( tabs , SWT.NONE);
		cSettings.setLayout(new GridLayout(2, false));
		cSettings.setLayoutData(new FormData());
		
		TabItem tSettings = new TabItem( tabs  , SWT.None ); 
		tSettings.setText(TuxGuitar.getProperty("fluidsynth.settings.audio"));
		tSettings.setControl( cSettings );
		
		// Audio Driver  --------------------------------------------------------
		final List audioDriverOptions = this.getAudioDriverOptions();
		
		Label lAudioDriver = new Label(cSettings, SWT.NONE);
		lAudioDriver.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lAudioDriver.setText(TuxGuitar.getProperty("fluidsynth.settings.audio.driver"));
		
		final Combo cAudioDriver = new Combo(cSettings, SWT.DROP_DOWN | SWT.READ_ONLY);
		cAudioDriver.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		fillAudioDriverCombo( audioDriverOptions , cAudioDriver );
		
		// Audio Period Size -------------------------------------------------------
		final List audioPeriodSizeOptions = this.getAudioPeriodSizeOptions();
		
		Label lAudioPeriodSize = new Label( cSettings , SWT.NONE);
		lAudioPeriodSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lAudioPeriodSize.setText(TuxGuitar.getProperty("fluidsynth.settings.audio.period-size"));
		
		final Combo cAudioPeriodSize = new Combo( cSettings , SWT.DROP_DOWN | SWT.READ_ONLY);
		cAudioPeriodSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		fillAudioPeriodSizeCombo( audioPeriodSizeOptions , cAudioPeriodSize );
		
		// ------------------BUTTONS--------------------------
		Composite compositeButtons = new Composite(dialog, SWT.NONE);
		compositeButtons.setLayout(new GridLayout(2,false));
		compositeButtons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(compositeButtons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData(SWT.FILL, SWT.FILL, true, true));
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				updateSoundfontsSelection( soundfontsTable );
				updateAudioDriverSelection( audioDriverOptions, cAudioDriver.getSelectionIndex() );
				updateAudioPeriodSizeSelection( audioPeriodSizeOptions, cAudioPeriodSize.getSelectionIndex() );
				
				new Thread( new Runnable() {
					public void run() {
						update();
					}
				}).start();
				
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(compositeButtons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData(SWT.FILL, SWT.FILL, true, true));
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
	}
	
	protected GridLayout getGridLayout(int columns){
		GridLayout layout = new GridLayout(columns, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		return layout;
	}
	
	protected GridData getButtonData(int hAlignment,int vAlignment,boolean grabExcessHSpace,boolean grabExcessVSpace){
		GridData data = new GridData(hAlignment,vAlignment,grabExcessHSpace,grabExcessVSpace);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected void addMidiPorts(Table table){
		Iterator it = getSoundfonts().iterator();
		while(it.hasNext()){
			String path = (String)it.next();
			this.addMidiPort(table, path );
		}
	}
	
	protected void addMidiPort(final Table table) {
		FileDialog chooser = new FileDialog(table.getShell());
		String path = chooser.open();
		if(path != null && path.length() > 0){
			addMidiPort(table, path);
		}
	}
	
	protected void addMidiPort(Table table, String path){
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText( path );
		item.setData( path );
	}
	
	protected void removeMidiPort(Table table){
		int index = table.getSelectionIndex();
		if(index >= 0 && index < table.getItemCount()){
			table.remove( index );
		}
	}
	
	protected List getAudioDriverOptions(){
		return this.getSynth().getPropertyOptions(MidiSettings.AUDIO_DRIVER);
	}
	
	protected List getAudioPeriodSizeOptions(){
		List options = new ArrayList();
		int[] range = getSynth().getIntegerPropertyRange( MidiSettings.AUDIO_PERIOD_SIZE );
		if( range.length == 2 && range[0] < range[1] ){
			int value = range[0];
			while( value <= range[1] ){
				options.add( new Integer(value) );
				value = (value * 2);
			}
		}
		return options;
	}
	
	protected void fillAudioDriverCombo( List options, Combo combo ){
		int selectedIndex = 0;
		String selectedValue = getStringValue( AUDIO_DRIVER );
		for(int i = 0 ; i < options.size(); i ++){
			String currentValue = (String)options.get(i);
			combo.add( currentValue );
			if( selectedValue != null && selectedValue.equals( currentValue )){
				selectedIndex = i;
			}
		}
		combo.select( selectedIndex );
	}
	
	protected void fillAudioPeriodSizeCombo( List options, Combo combo ){
		int selectedIndex = 0;
		int selectedValue = getIntegerValue( AUDIO_PERIOD_SIZE );
		for(int i = 0 ; i < options.size(); i ++){
			int currentValue = ((Integer)options.get(i)).intValue();
			combo.add( Integer.toString(currentValue) );
			if( selectedValue == currentValue ){
				selectedIndex = i;
			}
		}
		combo.select( selectedIndex );
	}
	
	protected void updateAudioDriverSelection( List options , int index ){
		if( index >=0 && index < options.size() ){
			setStringValue(MidiSettings.AUDIO_DRIVER, (( String )options.get( index )) ); 
		}
	}
	
	protected void updateAudioPeriodSizeSelection( List options , int index ){
		if( index >=0 && index < options.size() ){
			setIntegerValue(MidiSettings.AUDIO_PERIOD_SIZE, (( Integer )options.get( index )).intValue() ); 
		}
	}
	
	protected void updateSoundfontsSelection(Table table){
		List soundfonts = new ArrayList();
		int count = table.getItemCount();
		for( int i = 0 ; i < count; i ++ ){
			TableItem item = table.getItem( i );
			if( item.getData() instanceof String ){
				soundfonts.add( item.getData() );
			}
		}
		setSoundfonts( soundfonts );
	}
	
	protected void update(){
		this.save();
		this.apply();
	}
	
}

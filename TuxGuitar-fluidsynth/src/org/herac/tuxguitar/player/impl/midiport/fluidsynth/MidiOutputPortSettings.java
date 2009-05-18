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
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
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
		soundfontsTable.setLayoutData(getTableData());
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
		Composite cAudio = new Composite( tabs , SWT.NONE);
		cAudio.setLayout(new GridLayout(2, false));
		cAudio.setLayoutData(new FormData());
		
		TabItem tAudio = new TabItem( tabs  , SWT.None ); 
		tAudio.setText(TuxGuitar.getProperty("fluidsynth.settings.audio"));
		tAudio.setControl( cAudio );
		
		// Audio Driver  --------------------------------------------------------
		final List audioDriverOptions = this.getAudioDriverOptions();
		
		Label lAudioDriver = new Label(cAudio, SWT.NONE);
		lAudioDriver.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lAudioDriver.setText(TuxGuitar.getProperty("fluidsynth.settings.audio.driver"));
		
		final Combo cAudioDriver = new Combo(cAudio, SWT.DROP_DOWN | SWT.READ_ONLY);
		cAudioDriver.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		fillAudioDriverCombo( audioDriverOptions , cAudioDriver );
		
		// Audio Sample Format  -----------------------------------------------------
		final List audioSampleFormatOptions = this.getAudioSampleFormatOptions();
		
		Label lAudioSampleFormat = new Label(cAudio, SWT.NONE);
		lAudioSampleFormat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lAudioSampleFormat.setText(TuxGuitar.getProperty("fluidsynth.settings.audio.sample-format"));
		
		final Combo cAudioSampleFormat = new Combo(cAudio, SWT.DROP_DOWN | SWT.READ_ONLY);
		cAudioSampleFormat.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		fillAudioSampleFormatCombo( audioSampleFormatOptions , cAudioSampleFormat );
		
		// Audio Period Size -------------------------------------------------------
		final List audioPeriodSizeOptions = this.getAudioPeriodSizeOptions();
		
		Label lAudioPeriodSize = new Label( cAudio , SWT.NONE);
		lAudioPeriodSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lAudioPeriodSize.setText(TuxGuitar.getProperty("fluidsynth.settings.audio.period-size"));
		
		final Combo cAudioPeriodSize = new Combo( cAudio , SWT.DROP_DOWN | SWT.READ_ONLY);
		cAudioPeriodSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		fillAudioPeriodSizeCombo( audioPeriodSizeOptions , cAudioPeriodSize );
		
		// Audio Period Count -------------------------------------------------------
		final List audioPeriodCountOptions = this.getAudioPeriodCountOptions();
		
		Label lAudioPeriodCount = new Label( cAudio , SWT.NONE);
		lAudioPeriodCount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lAudioPeriodCount.setText(TuxGuitar.getProperty("fluidsynth.settings.audio.periods"));
		
		final Combo cAudioPeriodCount = new Combo( cAudio , SWT.DROP_DOWN | SWT.READ_ONLY);
		cAudioPeriodCount.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		fillAudioPeriodCountCombo( audioPeriodCountOptions , cAudioPeriodCount );
		
		// ----------------------------------------------------------------------
		Composite cSynth = new Composite( tabs , SWT.NONE);
		cSynth.setLayout(new GridLayout(2, false));
		cSynth.setLayoutData(new FormData());
		
		TabItem tSynth = new TabItem( tabs  , SWT.None ); 
		tSynth.setText(TuxGuitar.getProperty("fluidsynth.settings.synth"));
		tSynth.setControl( cSynth );
		
		// Synth Gain -------------------------------------------------------
		Label lSynthGain = new Label( cSynth , SWT.NONE);
		lSynthGain.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lSynthGain.setText(TuxGuitar.getProperty("fluidsynth.settings.synth.gain"));
		
		final Scale sSynthGain = new Scale( cSynth , SWT.HORIZONTAL );
		sSynthGain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fillSynthGainScale( sSynthGain );
		
		// Synth Sample Rate -------------------------------------------------------
		final List synthSampleRateOptions = this.getSynthSampleRateOptions();
		
		Label lSynthSampleRate = new Label( cSynth , SWT.NONE);
		lSynthSampleRate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lSynthSampleRate.setText(TuxGuitar.getProperty("fluidsynth.settings.synth.sample-rate"));
		
		final Combo cSynthSampleRate = new Combo( cSynth , SWT.DROP_DOWN | SWT.READ_ONLY);
		cSynthSampleRate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		fillSynthSampleRateCombo( synthSampleRateOptions , cSynthSampleRate );
		
		// Synth Polyphony -------------------------------------------------------
		Label lSynthPolyphony = new Label( cSynth , SWT.NONE);
		lSynthPolyphony.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lSynthPolyphony.setText(TuxGuitar.getProperty("fluidsynth.settings.synth.polyphony"));
		
		final Spinner sSynthPolyphony = new Spinner( cSynth , SWT.BORDER );
		sSynthPolyphony.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fillSynthPolyphonySpinner( sSynthPolyphony );
		
		// Synth Reverb -------------------------------------------------------
		final Button bSynthReverbActive = new Button( cSynth , SWT.CHECK);
		bSynthReverbActive.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,2,1));
		bSynthReverbActive.setText(TuxGuitar.getProperty("fluidsynth.settings.synth.reverb.active"));
		fillSynthReverbActiveCheckbox(bSynthReverbActive);
		
		// Synth Chorus -------------------------------------------------------
		final Button bSynthChorusActive = new Button( cSynth , SWT.CHECK);
		bSynthChorusActive.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,2,1));
		bSynthChorusActive.setText(TuxGuitar.getProperty("fluidsynth.settings.synth.chorus.active"));
		fillSynthChorusActiveCheckbox(bSynthChorusActive);
		
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
				updateAudioSampleFormatSelection( audioSampleFormatOptions , cAudioSampleFormat.getSelectionIndex() );
				updateAudioPeriodSizeSelection( audioPeriodSizeOptions, cAudioPeriodSize.getSelectionIndex() );
				updateAudioPeriodCountSelection( audioPeriodCountOptions, cAudioPeriodCount.getSelectionIndex() );
				updateSynthSampleRateSelection( synthSampleRateOptions , cSynthSampleRate.getSelectionIndex() );
				updateSynthGainSelection( sSynthGain.getSelection() );
				updateSynthPolyphonySelection( sSynthPolyphony.getSelection() );
				updateSynthReverbActiveSelection( bSynthReverbActive.getSelection() );
				updateSynthChorusActiveSelection( bSynthChorusActive.getSelection() );
				
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
	
	protected GridData getTableData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumHeight = TABLE_HEIGHT;
		return data;
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
	
	protected List getAudioSampleFormatOptions(){
		return this.getSynth().getPropertyOptions(MidiSettings.AUDIO_SAMPLE_FORMAT);
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
	
	protected List getAudioPeriodCountOptions(){
		List options = new ArrayList();
		int[] range = getSynth().getIntegerPropertyRange( MidiSettings.AUDIO_PERIOD_COUNT );
		if( range.length == 2 && range[0] < range[1] ){
			int value = range[0];
			while( value <= range[1] ){
				options.add( new Integer(value) );
				value = (value * 2);
			}
		}
		return options;
	}
	
	protected List getSynthSampleRateOptions(){
		List options = new ArrayList();
		double[] range = getSynth().getDoublePropertyRange( MidiSettings.SYNTH_SAMPLE_RATE );
		double[] values = new double[]{ 22050f , 44100f , 48000f , 88200f , 96000f };
		if( range.length == 2 && range[0] < range[1] ){
			for( int i = 0 ; i < values.length ; i ++ ){
				if( values[ i ] >= range[0] && values[ i ] <= range[1] ){
					options.add( new Double( values[ i ] ) );
				}
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
	
	protected void fillAudioSampleFormatCombo( List options, Combo combo ){
		int selectedIndex = 0;
		String selectedValue = getStringValue( AUDIO_SAMPLE_FORMAT );
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
	
	protected void fillAudioPeriodCountCombo( List options, Combo combo ){
		int selectedIndex = 0;
		int selectedValue = getIntegerValue( AUDIO_PERIOD_COUNT );
		for(int i = 0 ; i < options.size(); i ++){
			int currentValue = ((Integer)options.get(i)).intValue();
			combo.add( Integer.toString(currentValue) );
			if( selectedValue == currentValue ){
				selectedIndex = i;
			}
		}
		combo.select( selectedIndex );
	}
	
	protected void fillSynthGainScale( Scale scale ){
		double[] range = getSynth().getDoublePropertyRange( MidiSettings.SYNTH_GAIN );
		if( range.length == 2 ){
			int value = (int)Math.round( getDoubleValue( MidiSettings.SYNTH_GAIN ) * 10f );
			int minimum = (int)Math.round( range[0] * 10 );
			int maximum = (int)Math.round( range[1] * 10 );
			if( minimum < maximum ){
				scale.setMinimum( minimum );
				scale.setMaximum( maximum );
				scale.setIncrement(1);
				scale.setPageIncrement(10);
				if( value >= minimum && value <= maximum ){
					scale.setSelection( value );
				}
			}
		}
	}
	
	protected void fillSynthSampleRateCombo( List options, Combo combo ){
		int selectedIndex = 0;
		double selectedValue = getDoubleValue( MidiSettings.SYNTH_SAMPLE_RATE );
		for(int i = 0 ; i < options.size(); i ++){
			double currentValue = ((Double)options.get(i)).doubleValue();
			combo.add( Double.toString(currentValue) );
			if( selectedValue == currentValue ){
				selectedIndex = i;
			}
		}
		combo.select( selectedIndex );
	}
	
	protected void fillSynthPolyphonySpinner( Spinner spinner ){
		int value = getIntegerValue( MidiSettings.SYNTH_POLYPHONY );
		int[] range = getSynth().getIntegerPropertyRange( MidiSettings.SYNTH_POLYPHONY );
		if( range.length == 2 && range[0] < range[1] ){
			spinner.setMinimum( range[0] );
			spinner.setMaximum( range[1] );
			spinner.setIncrement(1);
			spinner.setPageIncrement(1);
			if( value >= range[0] && value <= range[1] ){
				spinner.setSelection( value );
			}
		}
	}
	
	protected void fillSynthReverbActiveCheckbox( Button button ){
		button.setSelection( getBooleanValue( SYNTH_REVERB_ACTIVE ) );
	}
	
	protected void fillSynthChorusActiveCheckbox( Button button ){
		button.setSelection( getBooleanValue( SYNTH_CHORUS_ACTIVE ) );
	}
	
	protected void updateAudioDriverSelection( List options , int index ){
		if( index >=0 && index < options.size() ){
			setStringValue(MidiSettings.AUDIO_DRIVER, (( String )options.get( index )) ); 
		}
	}
	
	protected void updateAudioSampleFormatSelection( List options , int index ){
		if( index >=0 && index < options.size() ){
			setStringValue(MidiSettings.AUDIO_SAMPLE_FORMAT, (( String )options.get( index )) ); 
		}
	}
	
	protected void updateAudioPeriodSizeSelection( List options , int index ){
		if( index >=0 && index < options.size() ){
			setIntegerValue(MidiSettings.AUDIO_PERIOD_SIZE, (( Integer )options.get( index )).intValue() ); 
		}
	}
	
	protected void updateAudioPeriodCountSelection( List options , int index ){
		if( index >=0 && index < options.size() ){
			setIntegerValue(MidiSettings.AUDIO_PERIOD_COUNT, (( Integer )options.get( index )).intValue() );
		}
	}
	
	protected void updateSynthGainSelection( int value ){
		double doubleValue = ( value / 10.00 );
		double[] range = getSynth().getDoublePropertyRange( MidiSettings.SYNTH_GAIN );
		if( range.length == 2 ){
			int minimum = (int)Math.round( range[0] );
			int maximum = (int)Math.round( range[1] );
			if( minimum < maximum && doubleValue >= minimum && doubleValue <= maximum ){
				setDoubleValue( MidiSettings.SYNTH_GAIN , doubleValue );
			}
		}
	}
	
	protected void updateSynthSampleRateSelection( List options , int index ){
		if( index >=0 && index < options.size() ){
			setDoubleValue(MidiSettings.SYNTH_SAMPLE_RATE, (( Double )options.get( index )).doubleValue() );
		}
	}
	
	protected void updateSynthPolyphonySelection( int value ){
		int[] range = getSynth().getIntegerPropertyRange( MidiSettings.SYNTH_POLYPHONY );
		if( range.length == 2 && range[0] < range[1] && value >= range[0] && value <= range[1] ){
			if( value >= range[0] && value <= range[1] ){
				setIntegerValue( MidiSettings.SYNTH_POLYPHONY, value );
			}
		}
	}
	
	protected void updateSynthReverbActiveSelection( boolean value ){
		setBooleanValue( MidiSettings.SYNTH_REVERB_ACTIVE, value );
	}
	
	protected void updateSynthChorusActiveSelection( boolean value ){
		setBooleanValue( MidiSettings.SYNTH_CHORUS_ACTIVE, value );
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

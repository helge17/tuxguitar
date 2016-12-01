package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIFileChooser;
import org.herac.tuxguitar.ui.chooser.UIFileChooserHandler;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIScale;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UITabFolder;
import org.herac.tuxguitar.ui.widget.UITabItem;
import org.herac.tuxguitar.ui.widget.UITable;
import org.herac.tuxguitar.ui.widget.UITableItem;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class MidiOutputPortSettings extends MidiSettings {
	
	public MidiOutputPortSettings(MidiOutputPortProviderImpl provider){
		super( provider );
	}
	
	public void configure(UIWindow uiParent) {
		final UIFactory uiFactory = TGApplication.getInstance(this.getProvider().getContext()).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("fluidsynth.settings"));
		
		UITabFolder tabs = uiFactory.createTabFolder(dialog, false);
		dialogLayout.set(tabs, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 450f, 250f, null);
		
		// ----------------------------------------------------------------------
		UITabItem tSoundfonts = tabs.createTab(); 
		tSoundfonts.setText(TuxGuitar.getProperty("fluidsynth.settings.soundfonts"));
		
		UITableLayout cSoundfontsLayout = new UITableLayout();
		UIPanel cSoundfonts = uiFactory.createPanel(tSoundfonts, false);
		cSoundfonts.setLayout(cSoundfontsLayout);
		
		final UITable<String> soundfontsTable = uiFactory.createTable(cSoundfonts, true);
		cSoundfontsLayout.set(soundfontsTable, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		cSoundfontsLayout.set(soundfontsTable, UITableLayout.PACKED_WIDTH, 0f);
		cSoundfontsLayout.set(soundfontsTable, UITableLayout.PACKED_HEIGHT, 0f);
		
		soundfontsTable.setColumns(1);
		soundfontsTable.setColumnName(0, TuxGuitar.getProperty("fluidsynth.settings.soundfonts.list"));
		
		UITableLayout cSoundfontsButtonsLayout = new UITableLayout(0f);
		UIPanel cSoundfontsButtons = uiFactory.createPanel(cSoundfonts, false);
		cSoundfontsButtons.setLayout(cSoundfontsButtonsLayout);
		cSoundfontsLayout.set(cSoundfontsButtons, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true);
		
		UIButton buttonAdd = uiFactory.createButton(cSoundfontsButtons);
		buttonAdd.setText(TuxGuitar.getProperty("add"));
		buttonAdd.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				UIFileChooser uiFileChooser = uiFactory.createOpenFileChooser(dialog);
				uiFileChooser.choose(new UIFileChooserHandler() {
					public void onSelectFile(File file) {
						if( file != null ) {
							addMidiPort(soundfontsTable, file.getAbsolutePath());
						}
					}
				});
			}
		});
		cSoundfontsButtonsLayout.set(buttonAdd, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false, 1, 1, 80f, 25f, null);
		cSoundfontsButtonsLayout.set(buttonAdd, UITableLayout.MARGIN_TOP, 0f);
		cSoundfontsButtonsLayout.set(buttonAdd, UITableLayout.MARGIN_RIGHT, 0f);
		
		UIButton buttonDelete = uiFactory.createButton(cSoundfontsButtons);
		buttonDelete.setText(TuxGuitar.getProperty("remove"));
		buttonDelete.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				removeMidiPort(soundfontsTable);
			}
		});
		cSoundfontsButtonsLayout.set(buttonDelete, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false, 1, 1, 80f, 25f, null);
		cSoundfontsButtonsLayout.set(buttonDelete, UITableLayout.MARGIN_BOTTOM, 0f);
		cSoundfontsButtonsLayout.set(buttonDelete, UITableLayout.MARGIN_RIGHT, 0f);
		
		this.addMidiPorts(soundfontsTable);
		
		// ----------------------------------------------------------------------
		UITabItem tAudio = tabs.createTab();  
		tAudio.setText(TuxGuitar.getProperty("fluidsynth.settings.audio"));
		
		UITableLayout cAudioLayout = new UITableLayout();
		UIPanel cAudio = uiFactory.createPanel(tAudio, false);
		cAudio.setLayout(cAudioLayout);
		
		// Audio Driver  --------------------------------------------------------
		final List<String> audioDriverOptions = this.getAudioDriverOptions();
		
		UILabel lAudioDriver = uiFactory.createLabel(cAudio);
		lAudioDriver.setText(TuxGuitar.getProperty("fluidsynth.settings.audio.driver"));
		cAudioLayout.set(lAudioDriver, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIDropDownSelect<String> cAudioDriver = uiFactory.createDropDownSelect(cAudio);
		cAudioLayout.set(cAudioDriver, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		fillAudioDriverCombo( audioDriverOptions , cAudioDriver );
		
		// Audio Sample Format  -----------------------------------------------------
		final List<String> audioSampleFormatOptions = this.getAudioSampleFormatOptions();
		
		UILabel lAudioSampleFormat = uiFactory.createLabel(cAudio);
		lAudioSampleFormat.setText(TuxGuitar.getProperty("fluidsynth.settings.audio.sample-format"));
		cAudioLayout.set(lAudioSampleFormat, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIDropDownSelect<String> cAudioSampleFormat = uiFactory.createDropDownSelect(cAudio);
		cAudioLayout.set(cAudioSampleFormat, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		fillAudioSampleFormatCombo( audioSampleFormatOptions , cAudioSampleFormat );
		
		// Audio Period Size -------------------------------------------------------
		final List<Integer> audioPeriodSizeOptions = this.getAudioPeriodSizeOptions();
		
		UILabel lAudioPeriodSize = uiFactory.createLabel(cAudio);
		lAudioPeriodSize.setText(TuxGuitar.getProperty("fluidsynth.settings.audio.period-size"));
		cAudioLayout.set(lAudioPeriodSize, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIDropDownSelect<Integer> cAudioPeriodSize = uiFactory.createDropDownSelect(cAudio);
		cAudioLayout.set(cAudioPeriodSize, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		fillAudioPeriodSizeCombo( audioPeriodSizeOptions , cAudioPeriodSize );
		
		// Audio Period Count -------------------------------------------------------
		final List<Integer> audioPeriodCountOptions = this.getAudioPeriodCountOptions();
		
		UILabel lAudioPeriodCount = uiFactory.createLabel(cAudio);
		lAudioPeriodCount.setText(TuxGuitar.getProperty("fluidsynth.settings.audio.periods"));
		cAudioLayout.set(lAudioPeriodCount, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIDropDownSelect<Integer> cAudioPeriodCount = uiFactory.createDropDownSelect(cAudio);
		cAudioLayout.set(cAudioPeriodCount, 4, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		fillAudioPeriodCountCombo(audioPeriodCountOptions , cAudioPeriodCount);
		
		// ----------------------------------------------------------------------
		
		UITabItem tSynth = tabs.createTab();  
		tSynth.setText(TuxGuitar.getProperty("fluidsynth.settings.synth"));
		
		UITableLayout cSynthLayout = new UITableLayout();
		UIPanel cSynth = uiFactory.createPanel(tSynth, false);
		cSynth.setLayout(cSynthLayout);
		
		// Synth Gain -------------------------------------------------------
		UILabel lSynthGain = uiFactory.createLabel(cSynth);
		lSynthGain.setText(TuxGuitar.getProperty("fluidsynth.settings.synth.gain"));
		cSynthLayout.set(lSynthGain, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIScale sSynthGain = uiFactory.createHorizontalScale(cSynth);
		cSynthLayout.set(sSynthGain, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		fillSynthGainScale( sSynthGain );
		
		// Synth Sample Rate -------------------------------------------------------
		final List<Double> synthSampleRateOptions = this.getSynthSampleRateOptions();
		
		UILabel lSynthSampleRate = uiFactory.createLabel(cSynth);
		lSynthSampleRate.setText(TuxGuitar.getProperty("fluidsynth.settings.synth.sample-rate"));
		cSynthLayout.set(lSynthSampleRate, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIDropDownSelect<Double> cSynthSampleRate = uiFactory.createDropDownSelect(cSynth);
		cSynthLayout.set(cSynthSampleRate, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		fillSynthSampleRateCombo( synthSampleRateOptions , cSynthSampleRate );
		
		// Synth Polyphony -------------------------------------------------------
		UILabel lSynthPolyphony = uiFactory.createLabel(cSynth);
		lSynthPolyphony.setText(TuxGuitar.getProperty("fluidsynth.settings.synth.polyphony"));
		cSynthLayout.set(lSynthPolyphony, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UISpinner sSynthPolyphony = uiFactory.createSpinner(cSynth);
		cSynthLayout.set(sSynthPolyphony, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		fillSynthPolyphonySpinner( sSynthPolyphony );
		
		// Synth Reverb -------------------------------------------------------
		final UICheckBox bSynthReverbActive = uiFactory.createCheckBox(cSynth);
		bSynthReverbActive.setText(TuxGuitar.getProperty("fluidsynth.settings.synth.reverb.active"));
		cSynthLayout.set(bSynthReverbActive, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 2);
		
		fillSynthReverbActiveCheckbox(bSynthReverbActive);
		
		// Synth Chorus -------------------------------------------------------
		final UICheckBox bSynthChorusActive = uiFactory.createCheckBox(cSynth);
		bSynthChorusActive.setText(TuxGuitar.getProperty("fluidsynth.settings.synth.chorus.active"));
		cSynthLayout.set(bSynthChorusActive, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 2);
		
		fillSynthChorusActiveCheckbox(bSynthChorusActive);
		
		// ------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		final UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateSoundfontsSelection( soundfontsTable );
				updateAudioDriverSelection(cAudioDriver.getSelectedValue());
				updateAudioSampleFormatSelection(cAudioSampleFormat.getSelectedValue());
				updateAudioPeriodSizeSelection(cAudioPeriodSize.getSelectedValue());
				updateAudioPeriodCountSelection(cAudioPeriodCount.getSelectedValue());
				updateSynthSampleRateSelection(cSynthSampleRate.getSelectedValue() );
				updateSynthGainSelection( sSynthGain.getValue() );
				updateSynthPolyphonySelection( sSynthPolyphony.getValue() );
				updateSynthReverbActiveSelection( bSynthReverbActive.isSelected() );
				updateSynthChorusActiveSelection( bSynthChorusActive.isSelected() );
				
				new Thread( new Runnable() {
					public void run() {
						update();
					}
				}).start();
				
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	protected void addMidiPorts(UITable<String> table){
		Iterator<String> it = getSoundfonts().iterator();
		while(it.hasNext()){
			String path = (String)it.next();
			this.addMidiPort(table, path );
		}
	}
	
	protected void addMidiPort(UITable<String> table, String path){
		UITableItem<String> uiTableItem = new UITableItem<String>(path);
		uiTableItem.setText(0, path);
		
		table.addItem(uiTableItem);
	}
	
	protected void removeMidiPort(UITable<String> table){
		UITableItem<String> uiTableItem = table.getSelectedItem();
		if( uiTableItem != null){
			table.removeItem(uiTableItem);
		}
	}
	
	protected List<String> getAudioDriverOptions(){
		return this.getSynth().getPropertyOptions(MidiSettings.AUDIO_DRIVER);
	}
	
	protected List<String> getAudioSampleFormatOptions(){
		return this.getSynth().getPropertyOptions(MidiSettings.AUDIO_SAMPLE_FORMAT);
	}
	
	protected List<Integer> getAudioPeriodSizeOptions(){
		List<Integer> options = new ArrayList<Integer>();
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
	
	protected List<Integer> getAudioPeriodCountOptions(){
		List<Integer> options = new ArrayList<Integer>();
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
	
	protected List<Double> getSynthSampleRateOptions(){
		List<Double> options = new ArrayList<Double>();
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
	
	protected void fillAudioDriverCombo(List<String> options, UIDropDownSelect<String> combo) {
		String selectedValue = getStringValue( AUDIO_DRIVER );
		for(String option : options) {
			combo.addItem(new UISelectItem<String>(option, option));
		}
		combo.setSelectedValue(selectedValue);
	}
	
	protected void fillAudioSampleFormatCombo(List<String> options, UIDropDownSelect<String> combo) {
		String selectedValue = getStringValue( AUDIO_SAMPLE_FORMAT );
		for(String option : options) {
			combo.addItem(new UISelectItem<String>(option, option));
		}
		combo.setSelectedValue(selectedValue);
	}
	
	protected void fillAudioPeriodSizeCombo(List<Integer> options, UIDropDownSelect<Integer> combo){ 
		int selectedValue = getIntegerValue( AUDIO_PERIOD_SIZE );
		for(Integer option : options) {
			combo.addItem(new UISelectItem<Integer>(option.toString(), option));
		}
		combo.setSelectedValue(selectedValue);
	}
	
	protected void fillAudioPeriodCountCombo(List<Integer> options, UIDropDownSelect<Integer> combo){
		int selectedValue = getIntegerValue( AUDIO_PERIOD_COUNT );
		for(Integer option : options) {
			combo.addItem(new UISelectItem<Integer>(option.toString(), option));
		}
		combo.setSelectedValue(selectedValue);
	}
	
	protected void fillSynthGainScale(UIScale scale){
		double[] range = getSynth().getDoublePropertyRange( MidiSettings.SYNTH_GAIN );
		if( range.length == 2 ){
			int value = (int)Math.round( getDoubleValue( MidiSettings.SYNTH_GAIN ) * 10f );
			int minimum = (int)Math.round( range[0] * 10 );
			int maximum = (int)Math.round( range[1] * 10 );
			if( minimum < maximum ){
				scale.setMinimum( minimum );
				scale.setMaximum( maximum );
				scale.setIncrement(1);
				if( value >= minimum && value <= maximum ){
					scale.setValue( value );
				}
			}
		}
	}
	
	protected void fillSynthSampleRateCombo(List<Double> options, UIDropDownSelect<Double> combo ){
		Double selectedValue = getDoubleValue( MidiSettings.SYNTH_SAMPLE_RATE );
		for(Double option : options) {
			combo.addItem(new UISelectItem<Double>(option.toString(), option));
		}
		combo.setSelectedValue(selectedValue);
	}
	
	protected void fillSynthPolyphonySpinner(UISpinner spinner){
		int value = getIntegerValue( MidiSettings.SYNTH_POLYPHONY );
		int[] range = getSynth().getIntegerPropertyRange( MidiSettings.SYNTH_POLYPHONY );
		if( range.length == 2 && range[0] < range[1] ){
			spinner.setMinimum( range[0] );
			spinner.setMaximum( range[1] );
			spinner.setIncrement(1);
			if( value >= range[0] && value <= range[1] ){
				spinner.setValue( value );
			}
		}
	}
	
	protected void fillSynthReverbActiveCheckbox(UICheckBox button){
		button.setSelected( getBooleanValue( SYNTH_REVERB_ACTIVE ) );
	}
	
	protected void fillSynthChorusActiveCheckbox(UICheckBox button){
		button.setSelected( getBooleanValue( SYNTH_CHORUS_ACTIVE ) );
	}
	
	protected void updateAudioDriverSelection(String value){
		if( value != null ){
			setStringValue(MidiSettings.AUDIO_DRIVER, value); 
		}
	}
	
	protected void updateAudioSampleFormatSelection(String value){
		if( value != null ){
			setStringValue(MidiSettings.AUDIO_SAMPLE_FORMAT, value); 
		}
	}
	
	protected void updateAudioPeriodSizeSelection(Integer value){
		if( value != null ){
			setIntegerValue(MidiSettings.AUDIO_PERIOD_SIZE, value); 
		}
	}
	
	protected void updateAudioPeriodCountSelection(Integer value){
		if( value != null ){
			setIntegerValue(MidiSettings.AUDIO_PERIOD_COUNT, value);
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
	
	protected void updateSynthSampleRateSelection(Double value){
		if( value != null ){
			setDoubleValue(MidiSettings.SYNTH_SAMPLE_RATE, value);
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
	
	protected void updateSoundfontsSelection(UITable<String> table){
		List<String> soundfonts = new ArrayList<String>();
		int count = table.getItemCount();
		for(int i = 0 ; i < count; i ++){
			String soundfont = table.getItemValue(i);
			if( soundfont != null ){
				soundfonts.add(soundfont);
			}
		}
		setSoundfonts( soundfonts );
	}
	
	protected void update(){
		this.save();
		this.apply();
	}
}

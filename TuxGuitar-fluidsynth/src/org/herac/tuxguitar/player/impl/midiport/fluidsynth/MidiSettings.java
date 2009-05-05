package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.gui.system.config.TGConfigManager;
import org.herac.tuxguitar.gui.system.plugins.TGPluginConfigManager;

public class MidiSettings {
	
	public static final String AUDIO_DRIVER = "audio.driver";
	public static final String AUDIO_PERIOD_SIZE = "audio.period-size";
	public static final String SYNTH_GAIN = "synth.gain";
	public static final String SYNTH_REVERB_ACTIVE = "synth.reverb.active";
	public static final String SYNTH_CHORUS_ACTIVE = "synth.chorus.active";
	
	private TGConfigManager config;
	private MidiOutputPortProviderImpl provider;
	
	public MidiSettings(MidiOutputPortProviderImpl provider){
		this.provider = provider;
	}
	
	public MidiSynth getSynth(){
		return this.provider.getSynth();
	}
	
	public TGConfigManager getConfig(){
		if(this.config == null){
			this.config = new TGPluginConfigManager("tuxguitar-fluidsynth");
			this.config.init();
		}
		return this.config;
	}
	
	public double getDoubleValue( String property ){
		return getConfig().getDoubleConfigValue(property, this.getSynth().getDoubleProperty( property ));
	}
	
	public int getIntegerValue( String property ){
		return getConfig().getIntConfigValue(property, this.getSynth().getIntegerProperty( property ));
	}
	
	public String getStringValue( String property ){
		return getConfig().getStringConfigValue(property, this.getSynth().getStringProperty( property ));
	}
	
	public void setDoubleValue( String property , double value ){
		getConfig().setProperty( property , value );
	}
	
	public void setIntegerValue( String property , int value ){
		getConfig().setProperty( property , value );
	}
		
	public void setStringValue( String property , String value ){
		if( value == null ){
			getConfig().removeProperty( property );
		}else{
			getConfig().setProperty( property , value );
		}
	}
	
	public List getSoundfonts(){
		List ports = new ArrayList();
		TGConfigManager config = getConfig();
		
		int count = config.getIntConfigValue("soundfont.count");
		for(int i = 0; i < count;i ++){
			String path = config.getStringConfigValue("soundfont.path" + i);
			if(path != null && path.length() > 0 ){
				ports.add( path );
			}
		}
		return ports;
	}
	
	public void setSoundfonts(List soundfonts){
		TGConfigManager config = getConfig();
		config.setProperty("soundfont.count", soundfonts.size() );
		for( int i = 0 ; i < soundfonts.size() ; i ++ ){
			String path = (String)soundfonts.get( i );
			config.setProperty("soundfont.path" + i, path );
		}
	}
	
	public void save(){
		this.getConfig().save();
	}
	
	public void apply(){
		if(this.getSynth() != null && this.getSynth().isInitialized()){
			this.getSynth().setStringProperty( AUDIO_DRIVER, getStringValue( AUDIO_DRIVER) );
			this.getSynth().setIntegerProperty(AUDIO_PERIOD_SIZE, getIntegerValue( AUDIO_PERIOD_SIZE) );
			this.getSynth().setStringProperty(SYNTH_REVERB_ACTIVE, getStringValue( SYNTH_REVERB_ACTIVE) );
			this.getSynth().setStringProperty(SYNTH_CHORUS_ACTIVE, getStringValue( SYNTH_CHORUS_ACTIVE) );
			this.getSynth().setDoubleProperty(SYNTH_GAIN, getDoubleValue( SYNTH_GAIN ) );
			this.getSynth().reconnect();
		}
	}
}

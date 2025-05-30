package app.tuxguitar.player.impl.midiport.fluidsynth;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.util.configuration.TGConfigManager;

public class MidiSettings {

	public static final String AUDIO_DRIVER = "audio.driver";
	public static final String AUDIO_SAMPLE_FORMAT = "audio.sample-format";
	public static final String AUDIO_PERIOD_SIZE = "audio.period-size";
	public static final String AUDIO_PERIOD_COUNT = "audio.periods";
	public static final String SYNTH_GAIN = "synth.gain";
	public static final String SYNTH_POLYPHONY = "synth.polyphony";
	public static final String SYNTH_SAMPLE_RATE = "synth.sample-rate";
	public static final String SYNTH_REVERB_ACTIVE = "synth.reverb.active";
	public static final String SYNTH_CHORUS_ACTIVE = "synth.chorus.active";

	public static final String SYNTH_AUDIO_CHANNELS = "synth.audio-channels";
	public static final String SYNTH_AUDIO_GROUPS = "synth.audio-groups";

	private TGConfigManager config;
	private MidiOutputPortProviderImpl provider;

	private boolean restartSynth;

	public MidiSettings(MidiOutputPortProviderImpl provider){
		this.provider = provider;
	}

	public MidiOutputPortProviderImpl getProvider(){
		return this.provider;
	}

	public MidiSynth getSynth(){
		return this.provider.getSynth();
	}

	public TGConfigManager getConfig(){
		if( this.config == null ){
			this.config = new TGConfigManager(this.provider.getContext(), "tuxguitar-fluidsynth");
		}
		return this.config;
	}

	public double getDoubleValue( String property ){
		return getConfig().getDoubleValue(property, this.getSynth().getDoubleProperty( property ));
	}

	public int getIntegerValue( String property ){
		return getConfig().getIntegerValue(property, this.getSynth().getIntegerProperty( property ));
	}

	public String getStringValue( String property ){
		return getConfig().getStringValue(property, this.getSynth().getStringProperty( property ));
	}

	public boolean getBooleanValue( String property ){
		String value = this.getStringValue(property);
		return (value != null && value.equals("yes"));
	}

	public void setDoubleValue( String property , double value ){
		getConfig().setValue( property , value );
	}

	public void setIntegerValue( String property , int value ){
		getConfig().setValue( property , value );
	}

	public void setStringValue( String property , String value ){
		if( value == null ){
			getConfig().remove( property );
		}else{
			getConfig().setValue( property , value );
		}
	}

	public void setBooleanValue( String property , boolean value ){
		this.setStringValue(property, ( value ? "yes" : "no" ) );
	}

	public List<String> getSoundfonts(){
		List<String> paths = new ArrayList<String>();
		TGConfigManager config = getConfig();

		int count = config.getIntegerValue("soundfont.count");
		for(int i = 0; i < count;i ++){
			String path = config.getStringValue("soundfont.path" + i);
			if( path != null && path.length() > 0 ){
				paths.add( path );
			}
		}
		return paths;
	}

	public void setSoundfonts(List<String> soundfonts){
		TGConfigManager config = getConfig();
		config.setValue("soundfont.count", soundfonts.size() );
		for( int i = 0 ; i < soundfonts.size() ; i ++ ){
			String path = (String)soundfonts.get( i );
			config.setValue("soundfont.path" + i, path );
		}
	}

	public void save(){
		this.getConfig().save();
	}

	public void apply(){
		if(this.getSynth() != null && this.getSynth().isInitialized()){
			this.restartSynth = false;
			this.applyStringProperty( AUDIO_DRIVER );
			this.applyStringProperty( AUDIO_SAMPLE_FORMAT );
			this.applyIntegerProperty(AUDIO_PERIOD_SIZE );
			this.applyIntegerProperty(AUDIO_PERIOD_COUNT );
			this.applyDoubleProperty(SYNTH_GAIN );
			this.applyDoubleProperty(SYNTH_SAMPLE_RATE );
			this.applyStringProperty(SYNTH_REVERB_ACTIVE );
			this.applyStringProperty(SYNTH_CHORUS_ACTIVE );
			this.applyIntegerProperty(SYNTH_POLYPHONY );
			if( this.restartSynth ){
				this.getSynth().reconnect();
				this.restartSynth = false;
			}
		}
	}

	private void applyStringProperty( String property ){
		String newValue = this.getStringValue( property );
		String oldValue = this.getSynth().getStringProperty( property );
		if( newValue != null ){
			if( oldValue == null || !newValue.equals( oldValue ) ){
				this.getSynth().setStringProperty( property, newValue );
				this.restartSynth = (this.restartSynth || !this.getSynth().isRealtimeProperty( property ));
			}
		}
	}

	private void applyDoubleProperty( String property ){
		double newValue = this.getDoubleValue( property );
		double oldValue = this.getSynth().getDoubleProperty( property );
		if( newValue != oldValue ){
			this.getSynth().setDoubleProperty( property, newValue );
			this.restartSynth = (this.restartSynth || !this.getSynth().isRealtimeProperty( property ));
		}
	}

	private void applyIntegerProperty( String property ){
		int newValue = this.getIntegerValue( property );
		int oldValue = this.getSynth().getIntegerProperty( property );
		if( newValue != oldValue ){
			this.getSynth().setIntegerProperty( property, newValue );
			this.restartSynth = (this.restartSynth || !this.getSynth().isRealtimeProperty( property ));
		}
	}
}

package org.herac.tuxguitar.player.impl.jsa.midiport;

import java.io.File;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.gm.port.GMOutputPort;
import org.herac.tuxguitar.gm.port.GMReceiver;
import org.herac.tuxguitar.player.base.MidiControllers;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.impl.jsa.assistant.SBAssistant;
import org.herac.tuxguitar.player.impl.jsa.utils.MidiConfigUtils;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;

public class MidiPortSynthesizer extends GMOutputPort{
	
	private TGContext context;
	private String key;
	private String name;
	private Synthesizer synth;
	private GMReceiver receiver;
	private boolean synthesizerLoaded;
	private boolean soundbankLoaded;
	
	public MidiPortSynthesizer(TGContext context, Synthesizer synthesizer){
		this.context = context;
		this.key = synthesizer.getDeviceInfo().getName();
		this.name = synthesizer.getDeviceInfo().getName();
		this.synth = synthesizer;
		this.receiver = new MidiPortSynthesizerReceiver(this);
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void open(){
		getSynth();
	}
	
	public void close(){
		if(this.synth != null && this.synth.isOpen()){
			this.unloadSoundbank();
			this.synth.close();
		}
	}
	
	public GMReceiver getReceiver(){
		return this.receiver;
	}
	
	public void check() throws MidiPlayerException{
		if(!isSynthesizerLoaded()){
			throw new MidiPlayerException(TuxGuitar.getProperty("jsa.error.midi.unavailable"));
		}
		if(!isSoundbankLoaded( true )){
			throw new MidiPlayerException(TuxGuitar.getProperty("jsa.error.soundbank.unavailable"));
		}
	}
	
	public Synthesizer getSynth() {
		try {
			if(!this.synth.isOpen()){
				this.synth.open();
				if(!isSoundbankLoaded( false )){
					String path = MidiConfigUtils.getSoundbankPath(this.context);
					if( path != null ){
						this.loadSoundbank(new File(TGExpressionResolver.getInstance(this.context).resolve(path)));
					}
					
					if(!isSoundbankLoaded( true )){
						this.loadSoundbank(this.synth.getDefaultSoundbank());
					}
					
					if(!isSoundbankLoaded( true )){
						new SBAssistant(this.context, this).process();
					}
				}
			}
			this.synthesizerLoaded = this.synth.isOpen();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return this.synth;
	}
	
	public boolean loadSoundbank(File file){
		try {
			return loadSoundbank( MidiSystem.getSoundbank(file) );
		}catch (Throwable throwable) {
			new MidiPlayerException(TuxGuitar.getProperty("jsa.error.soundbank.custom"),throwable).printStackTrace();
		}
		return false;
	}
	
	public boolean loadSoundbank(Soundbank sb) {
		try {
			if (sb != null && getSynth().isSoundbankSupported(sb)){
				//unload the old soundbank
				this.unloadSoundbank();
				
				//load all soundbank instruments
				this.soundbankLoaded = getSynth().loadAllInstruments(sb);
			}
		}catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return this.soundbankLoaded;
	}
	
	public void unloadSoundbank(){
		try {
			this.soundbankLoaded = false;
			
			//unload all available instruments
			Instrument[] available = this.synth.getAvailableInstruments();
			if(available != null){
				for(int i = 0; i < available.length; i++){
					getSynth().unloadInstrument(available[i]);
				}
			}
			
			//unload all loaded instruments
			Instrument[] loaded = this.synth.getLoadedInstruments();
			if(loaded != null){
				for(int i = 0; i < loaded.length; i++){
					getSynth().unloadInstrument(loaded[i]);
				}
			}
		}catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public boolean isSynthesizerLoaded(){
		return this.synthesizerLoaded;
	}
	
	public boolean isSoundbankLoaded(boolean checkSynth){
		if( checkSynth ){
			Instrument[] loaded = this.synth.getLoadedInstruments();
			Instrument[] available = this.synth.getAvailableInstruments();
			this.soundbankLoaded = ( (loaded != null && loaded.length > 0) || (available != null && available.length > 0) );
		}
		return this.soundbankLoaded;
	}
}

class MidiPortSynthesizerReceiver implements GMReceiver{
	
	private MidiPortSynthesizer port;
	private MidiChannel[] channels;
	
	public MidiPortSynthesizerReceiver(MidiPortSynthesizer port){
		this.port = port;
	}
	
	private MidiChannel[] getChannels(){
		if(this.channels == null && this.port.getSynth() != null){
			this.channels = this.port.getSynth().getChannels();
		}
		return this.channels;
	}
	
	public void sendSystemReset(){
		if(getChannels() != null){
			for(int i = 0;i < getChannels().length; i ++){
				getChannels()[i].resetAllControllers();
			}
		}
	}
	
	public void sendAllNotesOff(){
		if(getChannels() != null){
			for(int channel = 0;channel < getChannels().length;channel ++){
				sendControlChange(channel, MidiControllers.ALL_NOTES_OFF,0);
			}
		}
	}
	
	public void sendNoteOn(int channel, int key, int velocity){
		if(getChannels() != null && channel >= 0 && channel < getChannels().length){
			getChannels()[channel].noteOn(key, velocity);
		}
	}
	
	public void sendNoteOff(int channel, int key, int velocity){
		if(getChannels() != null && channel >= 0 && channel < getChannels().length){
			getChannels()[channel].noteOff(key, velocity);
		}
	}
	
	public void sendControlChange(int channel, int controller, int value){
		if(getChannels() != null && channel >= 0 && channel < getChannels().length){
			getChannels()[channel].controlChange(controller, value);
		}
	}
	
	public void sendProgramChange(int channel, int value){
		if(getChannels() != null && channel >= 0 && channel < getChannels().length){
			getChannels()[channel].programChange(value);
		}
	}
	
	public void sendPitchBend(int channel, int value){
		if(getChannels() != null && channel >= 0 && channel < getChannels().length){
			getChannels()[channel].setPitchBend( (value * 128) );
		}
	}
}
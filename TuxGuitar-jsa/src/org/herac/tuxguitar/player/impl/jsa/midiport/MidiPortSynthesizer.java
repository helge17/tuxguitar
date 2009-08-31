package org.herac.tuxguitar.player.impl.jsa.midiport;

import java.io.File;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.player.base.MidiControllers;
import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiReceiver;
import org.herac.tuxguitar.player.impl.jsa.assistant.SBAssistant;
import org.herac.tuxguitar.player.impl.jsa.utils.MidiConfigUtils;

public class MidiPortSynthesizer implements MidiOutputPort{
	
	private String key;
	private String name;
	private Synthesizer synthesizer;
	private MidiReceiver receiver;
	private boolean synthesizerLoaded;
	private boolean soundbankLoaded;
	
	public MidiPortSynthesizer(Synthesizer synthesizer){
		this.key = synthesizer.getDeviceInfo().getName();
		this.name = synthesizer.getDeviceInfo().getName();
		this.synthesizer = synthesizer;
		this.receiver = new MidiPortSynthesizerReceiver(this);
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void open(){
		getSynthesizer();
	}
	
	public void close(){
		if(this.synthesizer != null && this.synthesizer.isOpen()){
			this.unloadSoundbank();
			this.synthesizer.close();
		}
	}
	
	public MidiReceiver getReceiver(){
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
	
	public Synthesizer getSynthesizer() {
		try {
			if(!this.synthesizer.isOpen()){
				this.synthesizer.open();
				if(!isSoundbankLoaded( false )){
					String path = MidiConfigUtils.getSoundbankPath();
					if(path != null){
						this.loadSoundbank(new File(path));
					}
					
					if(!isSoundbankLoaded( true )){
						this.loadSoundbank( this.synthesizer.getDefaultSoundbank() );
					}
					
					if(!isSoundbankLoaded( true )){
						new SBAssistant(this).process();
					}
				}
			}
			this.synthesizerLoaded = this.synthesizer.isOpen();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return this.synthesizer;
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
			if (sb != null && getSynthesizer().isSoundbankSupported(sb)){
				//unload the old soundbank
				this.unloadSoundbank();
				
				//load all soundbank instruments
				this.soundbankLoaded = getSynthesizer().loadAllInstruments(sb);
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
			Instrument[] available = this.synthesizer.getAvailableInstruments();
			if(available != null){
				for(int i = 0; i < available.length; i++){
					getSynthesizer().unloadInstrument(available[i]);
				}
			}
			
			//unload all loaded instruments
			Instrument[] loaded = this.synthesizer.getLoadedInstruments();
			if(loaded != null){
				for(int i = 0; i < loaded.length; i++){
					getSynthesizer().unloadInstrument(loaded[i]);
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
			Instrument[] loaded = this.synthesizer.getLoadedInstruments();
			Instrument[] available = this.synthesizer.getAvailableInstruments();
			this.soundbankLoaded = ( (loaded != null && loaded.length > 0) || (available != null && available.length > 0) );
		}
		return this.soundbankLoaded;
	}
}

class MidiPortSynthesizerReceiver implements MidiReceiver{
	
	private MidiPortSynthesizer port;
	private MidiChannel[] channels;
	
	public MidiPortSynthesizerReceiver(MidiPortSynthesizer port){
		this.port = port;
	}
	
	private MidiChannel[] getChannels(){
		if(this.channels == null && this.port.getSynthesizer() != null){
			this.channels = this.port.getSynthesizer().getChannels();
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
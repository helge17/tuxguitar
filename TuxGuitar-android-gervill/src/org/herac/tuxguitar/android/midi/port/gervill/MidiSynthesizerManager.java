package org.herac.tuxguitar.android.midi.port.gervill;

import java.io.InputStream;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

import org.herac.tuxguitar.gm.port.GMReceiver;
import org.herac.tuxguitar.player.base.MidiControllers;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.util.TGContext;

import com.sun.media.sound.AudioSynthesizer;
import com.sun.media.sound.ModelPatch;
import com.sun.media.sound.SF2Instrument;
import com.sun.media.sound.SoftSynthesizer;

public class MidiSynthesizerManager {
	
	private static final String SOUNDBANK_RESOUCE_PREFIX = "soundbank/instrument";
	
	private static final int PERCUSSION_BANK = 128;
	private static final int PERCUSSION_CHANNEL = 9;
	
	private static final int DEFAULT_INSTRUMENT_BANK = 0;
	private static final int DEFAULT_PERCUSSION_PROGRAM  = 0;
	
	private TGContext context;
	private AudioSynthesizer synth;
	private GMReceiver receiver;
	private MidiChannel[] channels;
	private boolean synthesizerLoaded;
	
	public MidiSynthesizerManager(TGContext context) {
		this.context = context;
		this.synth = new SoftSynthesizer();
		this.receiver = new MidiReceiverImpl(this);
	}
	
	public void open() {
		this.getSynth();
	}
	
	public void close(){
		if(this.synth != null && this.synth.isOpen()){
			this.unloadAllInstruments();
			this.synth.close();
		}
	}
	
	public GMReceiver getReceiver(){
		return this.receiver;
	}
	
	public MidiChannel[] getChannels(){
		if( this.channels == null && this.getSynth() != null ){
			this.channels = this.getSynth().getChannels();
		}
		return this.channels;
	}
	
	public MidiChannel getChannel(int index){
		if( this.getChannels() != null && index >= 0 && index < this.getChannels().length ){
			return this.getChannels()[index];
		}
		return null;
	}
	
	public Synthesizer getSynth() {
		try {
			if(!this.synth.isOpen()){
				this.synth.open();
			}
			this.synthesizerLoaded = this.synth.isOpen();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return this.synth;
	}
	
	public boolean isSynthesizerLoaded(){
		return this.synthesizerLoaded;
	}
	
	public InputStream findResource(Patch patch) {
		String resourceName = (SOUNDBANK_RESOUCE_PREFIX + "-" + patch.getBank() + "-" + patch.getProgram() + ".sf2");
		
		return TGResourceManager.getInstance(this.context).getResourceAsStream(resourceName);
	}
	
	public Instrument findInstrument(Patch patch) {
		Instrument instrument = null;
		try {
			Patch resourcePatch = patch;
			InputStream inputStream = this.findResource(resourcePatch);
			if( inputStream == null ) {
				resourcePatch = this.toDefaultPatch(patch);
				if( resourcePatch.getBank() != patch.getBank() || resourcePatch.getProgram() != patch.getProgram() ) {
					inputStream = this.findResource(resourcePatch);
				}
			}
			if( inputStream != null ) {
				Soundbank soundbank = MidiSystem.getSoundbank(inputStream);
				if( soundbank != null ) {
					instrument = soundbank.getInstrument(this.toModelPatch(resourcePatch));
				}
			}
			if( instrument != null ) {
				this.setInstrumentPatch(instrument, patch);
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return instrument;
	}
	
	public void loadInstrument(Patch patch) {
		if(!this.isLoadedInstrument(patch)) {
			Instrument instrument = this.findInstrument(patch);
			if( instrument != null ) {
				this.getSynth().loadInstrument(instrument);
			}
		}
	}
	
	public void unloadOrphanInstruments() {
		Instrument[] instruments = this.getSynth().getLoadedInstruments();
		if( instruments != null ){
			for(Instrument instrument : instruments) {
				if( this.isOrphanInstrument(instrument.getPatch()) ) {
					this.getSynth().unloadInstrument(instrument);
				}
			}
		}
	}
	
	public void unloadAllInstruments() {
		Instrument[] instruments = this.getSynth().getLoadedInstruments();
		if( instruments != null ){
			for(Instrument instrument : instruments) {
				this.getSynth().unloadInstrument(instrument);
			}
		}
	}
	
	public boolean isLoadedInstrument(Patch patch) {
		Instrument[] instruments = this.getSynth().getLoadedInstruments();
		for(Instrument instrument : instruments) {
			if( this.isSamePatch(instrument.getPatch(), patch) ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isOrphanInstrument(Patch patch) {
		if( this.getChannels() != null ){
			for(int channel = 0 ; channel < this.getChannels().length ; channel ++) {
				Patch midiChannelPatch = this.findCurrentPatch(channel);
				if( this.isSamePatch(midiChannelPatch, patch)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public Patch findCurrentPatch(int channel) {
		return new Patch(this.findCurrentBank(channel), this.findCurrentProgram(channel));
	}
	
	public int findCurrentBank(int channel) {
		if( channel == PERCUSSION_CHANNEL ) {
			return PERCUSSION_BANK;
		}
		MidiChannel midiChannel = this.getChannel(channel);
		if( midiChannel != null ) {
			return midiChannel.getController(MidiControllers.BANK_SELECT);
		}
		return 0;
	}
	
	public int findCurrentProgram(int channel) {
		MidiChannel midiChannel = this.getChannel(channel);
		if( midiChannel != null ) {
			return midiChannel.getProgram();
		}
		return 0;
	}
	
	public Patch toPatch(int bank, int program) {
		return new Patch(bank, program);
	}
	
	private Patch toDefaultPatch(Patch patch) {
		if( patch.getBank() == PERCUSSION_BANK && patch.getProgram() != DEFAULT_PERCUSSION_PROGRAM ) {
			return new Patch(patch.getBank(), DEFAULT_PERCUSSION_PROGRAM);
		}
		
		if( patch.getBank() != PERCUSSION_BANK && patch.getBank() != DEFAULT_INSTRUMENT_BANK ) {
			return new Patch(DEFAULT_INSTRUMENT_BANK, patch.getProgram());
		}
		return patch;
	}
	
	private ModelPatch toModelPatch(Patch patch) {
		if( patch instanceof ModelPatch ) {
			return (ModelPatch) patch;
		}
		return new ModelPatch(patch.getBank() == PERCUSSION_BANK ? 0 : patch.getBank(), patch.getProgram(), patch.getBank() == PERCUSSION_BANK);
	}
	
	private void setInstrumentPatch(Instrument instrument, Patch patch) {
		if( instrument instanceof SF2Instrument ) {
			((SF2Instrument) instrument).setPatch(this.toModelPatch(patch));
		}
	}
	
	private boolean isSamePatch(Patch p1, Patch p2) {
		ModelPatch mp1 = this.toModelPatch(p1);
		ModelPatch mp2 = this.toModelPatch(p2);
		
		return (mp1.getBank() == mp2.getBank() && mp1.getProgram() == mp2.getProgram() && mp1.isPercussion() == mp2.isPercussion());
	}
}

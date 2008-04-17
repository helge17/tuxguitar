package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiPortProvider;

public class MidiPortProviderImpl implements MidiPortProvider{

	private MidiSynth synth;
	private MidiPortSettings settings;
	
	public MidiPortProviderImpl(){
		super();
	}
	
	public List listPorts() throws MidiPlayerException {
		try{
			if(this.synth == null || !this.synth.isInitialized()){
				this.synth = new MidiSynth();
			}
			List ports = new ArrayList();
			Iterator it = getSettings().getSoundfonts().iterator();
			while(it.hasNext()){
				String path = (String)it.next();
				File soundfont = new File( path );
				if( soundfont.exists() && !soundfont.isDirectory() ){
					ports.add( new MidiPortImpl(this.synth, soundfont ) );
				}
			}
			return ports;
		}catch(Throwable throwable){
			throw new MidiPlayerException(throwable.getMessage(), throwable);
		}
	}
	
	public void closeAll() throws MidiPlayerException {
		try{
			if(this.synth != null && this.synth.isInitialized()){
				this.synth.finalize();
				this.synth = null;
			}
		}catch(Throwable throwable){
			throw new MidiPlayerException(throwable.getMessage(), throwable);
		}
	}
	
	public MidiPortSettings getSettings(){
		if(this.settings == null){
			this.settings = new MidiPortSettings();
		}
		return this.settings;
	}
}

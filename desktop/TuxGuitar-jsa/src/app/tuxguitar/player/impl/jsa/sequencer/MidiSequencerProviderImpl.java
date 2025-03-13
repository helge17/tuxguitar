package app.tuxguitar.player.impl.jsa.sequencer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiSequencer;
import app.tuxguitar.player.base.MidiSequencerProvider;

public class MidiSequencerProviderImpl implements MidiSequencerProvider{

	public MidiSequencerProviderImpl(){
		super();
	}

	public List<MidiSequencer> listSequencers() throws MidiPlayerException {
		try {
			List<MidiSequencer> sequencers = new ArrayList<MidiSequencer>();
			MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
			for(int i = 0; i < infos.length; i++){
				try {
					Iterator<MidiSequencer> it = sequencers.iterator();
					boolean exists = false;
					while(it.hasNext()){
						if( ((MidiSequencer)it.next()).getKey().equals(infos[i].getName()) ){
							exists = true;
							break;
						}
					}
					if(!exists){
						MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
						if(device instanceof Sequencer){
							sequencers.add(new MidiSequencerImpl((Sequencer)device));
						}
					}
				} catch (MidiUnavailableException e) {
					throw new MidiPlayerException(TuxGuitar.getProperty("jsa.error.midi.unavailable"),e);
				}
			}
			return sequencers;
		}catch (Throwable t) {
			throw new MidiPlayerException(TuxGuitar.getProperty("jsa.error.unknown"),t);
		}
	}

	public void closeAll() throws MidiPlayerException {
		// Not implemented
	}
}

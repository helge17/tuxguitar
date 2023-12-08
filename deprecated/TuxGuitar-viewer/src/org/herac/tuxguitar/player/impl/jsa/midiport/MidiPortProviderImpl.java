package org.herac.tuxguitar.player.impl.jsa.midiport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;

import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.base.MidiPlayerException;

public class MidiPortProviderImpl implements MidiOutputPortProvider{
	
	public MidiPortProviderImpl(){
		super();
	}
	
	public List<MidiOutputPort> listPorts() throws MidiPlayerException{
		try {
			List<MidiOutputPort> ports = new ArrayList<MidiOutputPort>();
			MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
			for(int i = 0; i < infos.length; i++){
				try {
					Iterator<MidiOutputPort> it = ports.iterator();
					boolean exists = false;
					while(it.hasNext()){
						if( ((MidiOutputPort)it.next()).getKey().equals(infos[i].getName()) ){
							exists = true;
							break;
						}
					}
					if(!exists){
						MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
						if(device.getMaxReceivers() == 0 || device instanceof Sequencer){
							continue;
						}
						if(device instanceof Synthesizer){
							ports.add(new MidiPortSynthesizer((Synthesizer)device));
						}
						else{
							ports.add(new MidiPortOut(device));
						}
					}
				} catch (MidiUnavailableException e) {
					throw new MidiPlayerException("jsa.error.midi.unavailable", e);
				}
			}
			return ports;
		}catch (Throwable t) {
			throw new MidiPlayerException("jsa.error.unknown", t);
		}
	}
	
	public void closeAll() {
		// Not implemented
	}
}

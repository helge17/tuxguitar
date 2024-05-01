package org.herac.tuxguitar.io.gervill;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Patch;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.herac.tuxguitar.player.base.MidiControllers;
import org.herac.tuxguitar.song.models.TGDuration;

public class MidiToAudioWriter {
	
	public static void write(OutputStream out, List<MidiEvent> events, MidiToAudioSettings settings) throws Throwable {
		MidiToAudioSynth.instance().openSynth();
		MidiToAudioSynth.instance().loadSoundbank(getPatchs(events), settings.getSoundbankPath());
		
		int usqTempo = 60000000 / 120;
		long previousTick = 0;
		long timePosition = 0;
		MidiToAudioWriter.sort(events);
		Receiver receiver = MidiToAudioSynth.instance().getReceiver();
		AudioInputStream stream = MidiToAudioSynth.instance().getStream();
		
		Iterator<MidiEvent> it = events.iterator();
		while(it.hasNext()){
			MidiEvent event = (MidiEvent)it.next();
			MidiMessage msg = event.getMessage();
			
			timePosition += ( (event.getTick() - previousTick) * usqTempo) / TGDuration.QUARTER_TIME;
			
			if (msg instanceof MetaMessage) {
				if (((MetaMessage) msg).getType() == 0x51) {
					byte[] data = ((MetaMessage) msg).getData();
					usqTempo = ((data[0] & 0xff) << 16) | ((data[1] & 0xff) << 8) | (data[2] & 0xff);
				}
			} else {
				receiver.send(msg, timePosition);
			}
			previousTick = event.getTick();
		}
		
		long duration = (long) (stream.getFormat().getFrameRate() * ( (timePosition / 1000000.0) ));
		
		AudioInputStream srcStream = new AudioInputStream(stream, stream.getFormat(), duration );
		AudioInputStream dstStream = AudioSystem.getAudioInputStream(settings.getFormat(), srcStream );
		AudioSystem.write(new AudioInputStream(dstStream, dstStream.getFormat(), duration ), settings.getType(), out);
		
		dstStream.close();
		srcStream.close();
		
		MidiToAudioSynth.instance().closeSynth();
	}
	
	private static void sort(List<MidiEvent> events){
		Collections.sort(events, new Comparator<MidiEvent>() {
			public int compare(MidiEvent e1, MidiEvent e2) {
				if( e1 != null && e2 != null ){
					if(e1.getTick() > e2.getTick()){
						return 1;
					}
					else if(e1.getTick() < e2.getTick()){
						return -1;
					}
				}
				return 0;
			}
		});
	}
	
	private static List<Patch> getPatchs(List<MidiEvent> events){
		Patch[] channels = new Patch[16];
		
		Iterator<MidiEvent> it = events.iterator();
		while(it.hasNext()){
			MidiEvent event = (MidiEvent)it.next();
			MidiMessage msg = event.getMessage();
			if( msg instanceof ShortMessage ){
				ShortMessage shortMessage = (ShortMessage)msg;
				
				int channel = shortMessage.getChannel();
				if( channel >= 0 && channel < channels.length ){
					int command = shortMessage.getCommand();
					int data1 = shortMessage.getData1();
					int data2 = shortMessage.getData2();
					int bank = (command == ShortMessage.CONTROL_CHANGE && data1 == MidiControllers.BANK_SELECT ? data2 : -1);
					int program = (command == ShortMessage.PROGRAM_CHANGE ? data1 : -1);
					if( bank >= 0 || program >= 0 ){
						if( bank < 0 ){
							bank = (channels[channel] != null ? channels[channel].getBank() : 0);
						}
						if( program < 0 ){
							program = (channels[channel] != null ? channels[channel].getProgram() : 0);
						}
						channels[channel] = new Patch(bank, program);
					}
				}
			}
		}
		List<Patch> patchs = new ArrayList<Patch>();
		for( int i = 0 ; i < channels.length ; i ++ ){
			if( channels[i] != null ){
				boolean patchExists = false;
				Iterator<Patch> patchIt = patchs.iterator();
				while( patchIt.hasNext() ){
					Patch patch = (Patch) patchIt.next();
					if( patch.getBank() == channels[i].getBank() && patch.getProgram() == channels[i].getProgram() ){
						patchExists = true;
					}
				}
				if(!patchExists ){
					patchs.add(channels[i]);
				}
			}
		}
		patchs.add(new Patch(128, 0));
		
		return patchs;
	}
}

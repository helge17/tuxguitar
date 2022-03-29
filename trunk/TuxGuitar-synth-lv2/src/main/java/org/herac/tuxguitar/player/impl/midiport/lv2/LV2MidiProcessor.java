package org.herac.tuxguitar.player.impl.midiport.lv2;

import javax.sound.midi.ShortMessage;

import org.herac.tuxguitar.midi.synth.TGMidiProcessor;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2World;
import org.herac.tuxguitar.util.TGContext;

public class LV2MidiProcessor extends LV2AudioProcessorWrapper implements TGMidiProcessor {
	
	public LV2MidiProcessor(TGContext context, LV2World world) {
		super(context, world);
	}
	
	@Override
	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) {
		this.queueMidiMessage(new byte[] {(byte) ShortMessage.NOTE_ON, (byte) 0, (byte) key, (byte) velocity});
	}

	@Override
	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) {
		this.queueMidiMessage(new byte[] {(byte) ShortMessage.NOTE_OFF, (byte) 0, (byte) key, (byte) velocity});
	}

	@Override
	public void sendPitchBend(int value, int voice, boolean bendMode) {
		this.queueMidiMessage(new byte[] {(byte) ShortMessage.PITCH_BEND, (byte) 0, (byte) 0, (byte) value});
	}

	@Override
	public void sendControlChange(int controller, int value) {
		this.queueMidiMessage(new byte[] {(byte) ShortMessage.CONTROL_CHANGE, (byte) 0, (byte) controller, (byte) value});
	}
}

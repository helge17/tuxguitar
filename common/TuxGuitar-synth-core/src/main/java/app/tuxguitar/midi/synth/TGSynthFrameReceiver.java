package app.tuxguitar.midi.synth;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiReceiver;

/**
 * Where the events go that reach the synthesizer's channels on a frame of the block about to be rendered: notes, bends
 * and controls reach the channel's source on that frame; a program change, a parameter, all notes off and the pitch
 * bend reset apply at the block's start. An event for a channel that is not open is dropped.
 */
final class TGSynthFrameReceiver implements MidiReceiver {

	private final TGSynthModel synthesizer;
	private final int frame;

	TGSynthFrameReceiver(TGSynthModel synthesizer, int frame) {
		this.synthesizer = synthesizer;
		this.frame = frame;
	}

	private interface Send {
		void to(TGSynthChannel channel) throws MidiPlayerException;
	}

	private void toChannel(int channelId, Send send) throws MidiPlayerException {
		TGSynthChannel channel = this.synthesizer.getChannelById(channelId);
		if (channel != null) {
			send.to(channel);
		}
	}

	private void toEveryChannel(Send send) throws MidiPlayerException {
		for (TGSynthChannel channel : this.synthesizer.channels()) {
			send.to(channel);
		}
	}

	public void sendNoteOn(int channel, int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException {
		this.toChannel(channel, to -> to.sendNoteOn(key, velocity, voice, bendMode, this.frame));
	}

	public void sendNoteOff(int channel, int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException {
		this.toChannel(channel, to -> to.sendNoteOff(key, velocity, voice, bendMode, this.frame));
	}

	public void sendPitchBend(int channel, int value, int voice, boolean bendMode) throws MidiPlayerException {
		this.toChannel(channel, to -> to.sendPitchBend(value, voice, bendMode, this.frame));
	}

	public void sendPitchBend(int channel, int value, double semitones, int voice, boolean bendMode) throws MidiPlayerException {
		this.toChannel(channel, to -> to.sendPitchBend(value, semitones, voice, bendMode, this.frame));
	}

	public void sendControlChange(int channel, int controller, int value) throws MidiPlayerException {
		this.toChannel(channel, to -> to.sendControlChange(controller, value, this.frame));
	}

	public void sendProgramChange(int channel, int value) throws MidiPlayerException {
		this.toChannel(channel, to -> to.sendProgramChange(value));
	}

	public void sendParameter(int channel, String key, String value) throws MidiPlayerException {
		this.toChannel(channel, to -> to.sendParameter(key, value));
	}

	public void sendAllNotesOff() throws MidiPlayerException {
		this.toEveryChannel(TGSynthChannel::sendAllNotesOff);
	}

	// as MidiChannelRouter resets them
	public void sendPitchBendReset() throws MidiPlayerException {
		this.toEveryChannel(to -> to.sendPitchBend(64, -1, false));
	}
}

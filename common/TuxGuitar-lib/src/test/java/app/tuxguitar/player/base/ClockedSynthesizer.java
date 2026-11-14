package app.tuxguitar.player.base;

import java.util.List;

/**
 * A synthesizer that plays on a sample clock, as the TuxGuitar Synthesizer does, with or without General MIDI drums of
 * its own. Each event sent to the receiver of a frame is written as one line, the frame first ("512 on 0 37 95 -1 0",
 * see MidiReceiverRecorder); each channel it opens writes its opening, the controls it is sent and its closing ("-2
 * opened", "-2 cc 7 127", "-2 closed"). It renders nothing by itself: a test runs its listener.
 */
class ClockedSynthesizer implements MidiSynthesizer, MidiSampleClock {

	private final List<String> received;
	private final boolean drums;
	MidiSampleClock.Listener listener;

	ClockedSynthesizer(List<String> received) {
		this(received, false);
	}

	ClockedSynthesizer(List<String> received, boolean drums) {
		this.received = received;
		this.drums = drums;
	}

	private final class Channel implements MidiChannel {
		private final int id;

		Channel(int id) {
			this.id = id;
		}

		public void sendControlChange(int controller, int value) { received.add(this.id + " cc " + controller + " " + value); }
		public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) { }
		public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) { }
		public void sendPitchBend(int value, int voice, boolean bendMode) { }
		public void sendProgramChange(int value) { }
		public void sendParameter(String key, String value) { }
		public void sendAllNotesOff() { }
	}

	public MidiChannel openChannel(int channelId) {
		this.received.add(channelId + " opened");
		return new Channel(channelId);
	}
	public void closeChannel(MidiChannel midiChannel) { this.received.add(((Channel) midiChannel).id + " closed"); }
	public boolean isChannelOpen(MidiChannel midiChannel) { return true; }
	public boolean isBusy() { return false; }
	public float getSampleRate() { return 44100; }
	public void setListener(Listener listener) { this.listener = listener; }
	public boolean hasDrums() { return this.drums; }

	public MidiReceiver at(int frame) {
		return new MidiReceiverRecorder(this.received, frame + " ");
	}
}

package app.tuxguitar.player.base;

import java.util.List;

/**
 * A synthesizer that plays on a sample clock, as the TuxGuitar Synthesizer does: it opens no channel, and each event
 * sent to the receiver of a frame is written as one line, the frame first ("512 on 0 37 95 -1 0", see
 * MidiReceiverRecorder). It renders nothing by itself: a test runs its listener.
 */
class ClockedSynthesizer implements MidiSynthesizer, MidiSampleClock {

	private final List<String> received;
	MidiSampleClock.Listener listener;

	ClockedSynthesizer(List<String> received) {
		this.received = received;
	}

	public MidiChannel openChannel(int channelId) { return null; }
	public void closeChannel(MidiChannel midiChannel) { }
	public boolean isChannelOpen(MidiChannel midiChannel) { return false; }
	public boolean isBusy() { return false; }
	public float getSampleRate() { return 44100; }
	public void setListener(Listener listener) { this.listener = listener; }

	public MidiReceiver at(int frame) {
		return new MidiReceiverRecorder(this.received, frame + " ");
	}
}

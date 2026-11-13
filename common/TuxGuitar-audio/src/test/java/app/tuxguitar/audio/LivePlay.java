package app.tuxguitar.audio;

import java.util.stream.IntStream;

import app.tuxguitar.audio.device.TGAudioDeviceException;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.player.base.MidiPlayer;

/**
 * The song played live, headless, as the app plays it on the TuxGuitar Synthesizer: the player started, its sequencer
 * on the synthesizer's sample clock, the synthesizer playing through the live audio device (TGLiveDevice) on a double
 * of the native backend (TestBackend) whose inputs deliver what the audio device of the scenario delivers and which
 * reports its latencies; the device asks for a block, then the play loop takes a turn, until a turn ends it. What the
 * device played is kept: the output, left and right.
 */
public final class LivePlay {

	private static final int MOST_BLOCKS = 10000;

	private LivePlay() {
	}

	// the player has started to play on the synthesizer
	public static float[][] play(MidiPlayer player, TGSynthModel synthesizer, TGAudioDevice device) throws TGAudioDeviceException {
		TestBackend backend = new TestBackend(device, device.getInputLatency(), device.getOutputLatency());
		try (TGLiveDevice live = TGLiveDevice.open(backend, 0, 2, 0)) {
			live.play(synthesizer);
			IntStream.range(0, MOST_BLOCKS).peek(block -> backend.getStream().run(1)).allMatch(block -> player.runPlayLoopProcess());
		}
		return backend.getStream().getPlayed();
	}
}

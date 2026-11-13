package app.tuxguitar.audio;

import java.util.function.IntPredicate;
import java.util.stream.IntStream;

import app.tuxguitar.audio.device.TGAudioDeviceException;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.util.TGContext;

/**
 * The song played live, headless, as the app plays it on the TuxGuitar Synthesizer: the player started, its sequencer
 * on the synthesizer's sample clock, the synthesizer playing through the live audio device (TGLiveDevice) on a double
 * of the native backend (TestBackend) whose inputs deliver what the audio device of the scenario delivers and which
 * reports its latencies; the device asks for a block, then the play loop takes a turn, until a turn ends it. As the
 * app runs it, the live device is the session's audio device meanwhile, and its input is metered and monitored
 * (TGLiveInput). What the device played is kept: the output, left and right.
 */
public final class LivePlay {

	private static final int MOST_BLOCKS = 10000;

	private LivePlay() {
	}

	// the player has started to play on the synthesizer
	public static float[][] play(TGContext context, MidiPlayer player, TGSynthModel synthesizer, TGAudioDevice device) throws TGAudioDeviceException {
		return run(context, synthesizer, device, block -> player.runPlayLoopProcess());
	}

	// TuxGuitar stopped while the device delivers the frames (whole blocks)
	public static float[][] listen(TGContext context, TGSynthModel synthesizer, TGAudioDevice device, int frames) throws TGAudioDeviceException {
		return run(context, synthesizer, device, block -> (block + 1) * TGBlocks.FRAMES < frames);
	}

	private static float[][] run(TGContext context, TGSynthModel synthesizer, TGAudioDevice device, IntPredicate turn) throws TGAudioDeviceException {
		TestBackend backend = new TestBackend(device, device.getInputLatency(), device.getOutputLatency());
		TGAudioSession session = TGAudioSession.getInstance(context);
		try (TGLiveDevice live = TGLiveDevice.open(backend, 0, 2, 0); TGLiveInput input = TGLiveInput.install(context, live)) {
			session.setDevice(live);
			live.play(synthesizer);
			IntStream.range(0, MOST_BLOCKS).peek(block -> backend.getStream().run(1)).allMatch(turn);
		} finally {
			session.setDevice(device);
		}
		return backend.getStream().getPlayed();
	}
}

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
 * of the native backend (TestBackend); the device asks for a block, then the play loop takes a turn, until a turn ends
 * it. As the app runs it, the live device is the session's audio device meanwhile, and its input is metered and
 * monitored (TGLiveInput). What the device played is kept: the output, left and right.
 */
public final class LivePlay {

	private static final int MOST_BLOCKS = 10000;

	private LivePlay() {
	}

	// the player has started to play on the synthesizer
	public static float[][] play(TGContext context, MidiPlayer player, TGSynthModel synthesizer, TestBackend backend) throws TGAudioDeviceException {
		return run(context, synthesizer, backend, () -> { }, block -> player.runPlayLoopProcess());
	}

	// TuxGuitar stopped while the device delivers the frames (whole blocks)
	public static float[][] listen(TGContext context, TGSynthModel synthesizer, TestBackend backend, int frames) throws TGAudioDeviceException {
		return run(context, synthesizer, backend, () -> { }, block -> (block + 1) * TGBlocks.FRAMES < frames);
	}

	// begins once the live device is the session's, then runs a block and a turn until a turn ends it (each turn told
	// how many blocks ran before its own)
	public static float[][] run(TGContext context, TGSynthModel synthesizer, TestBackend backend, Runnable begin, IntPredicate turn) throws TGAudioDeviceException {
		TGAudioSession session = TGAudioSession.getInstance(context);
		TGAudioDevice device = session.getDevice();
		try (TGLiveDevice live = TGLiveDevice.open(backend, 0, 2, 0); TGLiveInput input = TGLiveInput.install(context, live)) {
			session.setDevice(live);
			live.play(synthesizer);
			begin.run();
			IntStream.range(0, MOST_BLOCKS).peek(block -> backend.getStream().run(1)).allMatch(turn);
		} finally {
			session.setDevice(device);
		}
		return backend.getStream().getPlayed();
	}
}

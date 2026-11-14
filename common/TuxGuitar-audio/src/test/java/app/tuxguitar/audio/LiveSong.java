package app.tuxguitar.audio;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGPropertiesManager;

/**
 * A song recorded live, on a context of its own: 2 measures of 4/4 at 120 bpm (4 seconds) and an audio track on input
 * channel 1, armed. It is played by a double of the sequencer (TimelineSequencer) on a synthesizer through the live
 * device; the player itself does not run. By default the recording starts at once, and the song two waits later, from
 * its first measure.
 */
final class LiveSong {

	// lets the device run on a while: a block, or some milliseconds
	interface Wait {
		void next() throws Exception;
	}

	final TGContext context = new TGContext();
	final TGAudioSession session = TGAudioSession.getInstance(this.context);
	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = this.songManager.newSong();
	private final TGTrack track;
	// waits before the recording starts, and between its start and the song's; the measure the song plays from
	int idle = 0;
	int lead = 2;
	int from = 1;

	LiveSong() {
		TGPropertiesManager.getInstance(this.context).setPropertiesFactory(MapProperties::new);
		this.songManager.addNewMeasureBeforeEnd(this.song);
		this.track = this.songManager.addAudioTrack(this.song, TGAudioInput.mono(1));
		TGDocumentManager.getInstance(this.context).setSong(this.song);
		MidiPlayer.getInstance(this.context);
		this.session.arm(this.track);
	}

	// the song's frames at the rate: 4 seconds
	static int length(int sampleRate) {
		return 4 * sampleRate;
	}

	// the device is made the session's and the recording starts, then the song plays so many times back to back, until
	// it has played
	TGLiveRecording play(TGLiveDevice device, int times, Wait wait) throws Exception {
		TGSynthModel synthesizer = LiveOutput.synthesizer(this.context, device.getSampleRate());
		TimelineSequencer sequencer = new TimelineSequencer(this.song, device.getSampleRate(), this.from, times);
		synthesizer.setListener(sequencer);
		device.play(synthesizer);
		this.session.setDevice(device);
		waits(this.idle, wait);
		TGLiveRecording recording = TGLiveRecording.start(this.context, device);
		waits(this.lead, wait);
		sequencer.start();
		while (!sequencer.isDone()) {
			wait.next();
		}
		return recording;
	}

	private static void waits(int count, Wait wait) throws Exception {
		for (int i = 0; i < count; i++) {
			wait.next();
		}
	}

	// the recording ends on this thread, once it has; how many takes it made
	int endHere(TGLiveRecording recording) {
		return recording.end(this.songManager, this.song, List.of(this.track));
	}

	// the recording ends on a thread of its own, the device running on until it has
	int end(TGLiveRecording recording, Wait wait) throws Exception {
		AtomicInteger takes = new AtomicInteger(-1);
		Thread ending = new Thread(() -> takes.set(this.endHere(recording)));
		ending.start();
		while (ending.isAlive()) {
			wait.next();
		}
		return takes.get();
	}

	// played so many times, then the device runs on for as many waits as given before the recording ends
	int record(TGLiveDevice device, int times, int on, Wait wait) throws Exception {
		TGLiveRecording recording = this.play(device, times, wait);
		waits(on, wait);
		return this.end(recording, wait);
	}

	// the recording ends on this thread, interrupted: at once
	int endInterrupted(TGLiveRecording recording) {
		Thread.currentThread().interrupt();
		try {
			return this.endHere(recording);
		} finally {
			Thread.interrupted();
		}
	}

	// the samples of the take of that number
	float[] take(int number) {
		TGTake take = TGTakes.find(this.track, number);
		float[] samples = new float[take.countSamples()];
		for (int i = 0; i < samples.length; i++) {
			samples[i] = take.getSample(0, i);
		}
		return samples;
	}

	int countTakes() {
		return TGTakes.list(this.track).size();
	}
}

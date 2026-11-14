package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGTrack;

/**
 * What the app shows of its audio while it runs: whether a recording is under way, whether Record can start one, and
 * the input level of each armed track (its meter from -60 dBFS to 0 dBFS, the level, whether it has clipped). The song
 * has a guitar track and two audio tracks, "Voice" and "Bass"; the device delivers at 4 samples a second.
 */
class TestAudioStatus {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGAudioSession session = TGAudioSession.getInstance(this.fixture.context);
	private final TGTrack voice = this.fixture.songManager.addAudioTrack(this.fixture.song, TGAudioInput.mono(1));
	private final TGTrack bass = this.fixture.songManager.addAudioTrack(this.fixture.song, TGAudioInput.mono(2));

	TestAudioStatus() {
		TGDocumentManager.getInstance(this.fixture.context).setSong(this.fixture.song);
		this.voice.setName("Voice");
		this.bass.setName("Bass");
	}

	private void device() {
		this.session.setDevice(new TGAudioDevice() {
			public int getSampleRate() {
				return 4;
			}

			public float[] capture(int inputChannel, long from, int count) {
				return new float[count];
			}
		});
	}

	// "<state> <record or not>" then a line per meter: "<name> <percent> <level> <clipped or not>"
	private String status() {
		TGAudioStatus status = TGAudioStatus.of(this.fixture.context);
		String meters = status.getMeters().stream().map(meter -> " | " + meter.getTrack().getName() + " " + meter.getPercent() + " " + meter.getLevel() + (meter.hasClipped() ? " clipped" : "")).collect(Collectors.joining());
		return status.getState() + (status.canRecord() ? " record" : "") + meters;
	}

	@Test
	void withoutADeviceItCannotRecordAndNothingIsMetered() {
		this.session.arm(this.voice);
		assertEquals("audio.status.no-device", status());
	}

	@Test
	void withoutAnArmedTrackItCannotRecord() {
		device();
		assertEquals("audio.status.not-recording", status());
	}

	// half full scale is -6.0 dBFS; full scale has clipped; the guitar track cannot be armed, the Bass was never fed
	@Test
	void itCanRecordAndEachArmedTrackShowsItsLevelInTrackOrder() {
		device();
		this.session.arm(this.bass);
		this.session.arm(this.voice);
		this.session.getInputMeter(this.voice).deliver(new float[][] {{0.5f}});
		assertEquals("audio.status.not-recording record | Voice 90 -6.0 dBFS | Bass 0 -inf dBFS", status());
		this.session.getInputMeter(this.bass).deliver(new float[][] {{1f}});
		assertEquals("audio.status.not-recording record | Voice 90 -6.0 dBFS | Bass 100 0.0 dBFS clipped", status());
	}

	// from -60 dBFS (and quieter) to 0 dBFS (and louder: a float input can deliver more than full scale)
	@Test
	void theMeterRunsFromMinusSixtyToZeroDbfs() {
		device();
		this.session.arm(this.voice);
		this.session.getInputMeter(this.voice).deliver(new float[][] {{0.0001f}});
		String quieter = status();
		this.session.getInputMeter(this.voice).deliver(new float[][] {{0.001f}});
		String sixty = status();
		this.session.getInputMeter(this.voice).deliver(new float[][] {{0.0316f}});
		String thirty = status();
		this.session.getInputMeter(this.voice).deliver(new float[][] {{2f}});
		assertEquals("Voice 0 -80.0 dBFS | Voice 0 -60.0 dBFS | Voice 50 -30.0 dBFS | Voice 100 6.0 dBFS clipped", meter(quieter) + " | " + meter(sixty) + " | " + meter(thirty) + " | " + meter(status()));
	}

	private static String meter(String status) {
		return status.substring(status.indexOf(" | ") + 3);
	}

	// the settings of an audio track (monitoring, input channels): none for another track, or when there is no track
	@Test
	void onlyAnAudioTrackHasAudioTrackSettings() {
		TGTrack guitar = this.fixture.song.getTrack(0);
		assertEquals("guitar false | Voice true | none false", "guitar " + TGAudioStatus.hasTrackSettings(guitar) + " | Voice " + TGAudioStatus.hasTrackSettings(this.voice) + " | none " + TGAudioStatus.hasTrackSettings(null));
	}

	@Test
	void whileRecordingItCannotRecordAgain() {
		device();
		this.session.arm(this.voice);
		this.session.setRecording(true);
		assertEquals("audio.status.recording | Voice 0 -inf dBFS", status());
	}
}

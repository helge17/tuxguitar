package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.util.TGContext;

/**
 * The round trip, which places a take earlier: the output latency, the plugin delay compensation of what is heard,
 * and the input latency the audio device reports, plus the recording offset, a setting that corrects an input latency
 * reported wrong. The device here reports 300 samples of input latency and 1024 of output latency.
 */
public class TestRoundTrip {

	private final TGAudioSession session = TGAudioSession.getInstance(new TGContext());

	public TestRoundTrip() {
		session.setDevice(new SilentDevice() {
			public int getInputLatency() {
				return 300;
			}

			public int getOutputLatency() {
				return 1024;
			}
		});
	}

	// no offset until it is set, then it is added
	@Test
	public void theRoundTripIsTheReportedLatenciesAndTheRecordingOffset() {
		String unset = session.getRecordingOffset() + " " + session.getRoundTrip(0);
		session.setRecordingOffset(64);

		assertEquals("0 1324, 64 1388", unset + ", " + session.getRecordingOffset() + " " + session.getRoundTrip(0));
	}

	// what is heard comes out of the output that much later still: Gervill's 600 samples at 44100 Hz, say
	@Test
	public void thePluginDelayCompensationOfWhatIsHeardIsAddedToTheRoundTrip() {
		assertEquals(1924, session.getRoundTrip(600));
	}

	// a device that reports no latency has none: the round trip is the offset alone
	@Test
	public void aDeviceThatReportsNoLatencyHasARoundTripOfTheOffsetAlone() {
		session.setDevice(new SilentDevice());
		session.setRecordingOffset(64);

		assertEquals(64, session.getRoundTrip(0));
	}
}

package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.util.TGContext;

/**
 * The round trip, which places a take earlier: the output latency and the input latency the audio device reports,
 * plus the recording offset, a setting that corrects an input latency reported wrong. The device here reports 300
 * samples of input latency and 1024 of output latency.
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

	@Test
	public void theRoundTripIsTheReportedLatenciesWithoutAnOffset() {
		assertEquals("0 1324", session.getRecordingOffset() + " " + session.getRoundTrip());
	}

	@Test
	public void theRecordingOffsetIsAddedToTheRoundTrip() {
		session.setRecordingOffset(64);

		assertEquals("64 1388", session.getRecordingOffset() + " " + session.getRoundTrip());
	}

	// a device that reports no latency has none: the round trip is the offset alone
	@Test
	public void aDeviceThatReportsNoLatencyHasARoundTripOfTheOffsetAlone() {
		session.setDevice(new SilentDevice());
		session.setRecordingOffset(64);

		assertEquals(64, session.getRoundTrip());
	}
}

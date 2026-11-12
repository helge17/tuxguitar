package app.tuxguitar.audio.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.audio.TGAudioDevice;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.audio.TGInputMeter;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGTrack;

/**
 * Resetting a track's clip indicator clears it on an audio track; a guitar track has no input level, so it is refused.
 * A track reads as whether its input has clipped, then "success" when the action reported it.
 */
public class TestResetClipIndicatorAction {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGAudioSession session = TGAudioSession.getInstance(fixture.context);

	public TestResetClipIndicatorAction() {
		session.setDevice(new TGAudioDevice() {
			public int getSampleRate() {
				return 4;
			}

			public float[] capture(int inputChannel, long from, int count) {
				return new float[count];
			}
		});
	}

	private String reset(TGTrack track) {
		TGInputMeter meter = session.getInputMeter(track);
		meter.deliver(new float[][] {{1f}});
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		fixture.execute(new TGResetClipIndicatorAction(fixture.context));
		return (meter.hasClipped() ? "clipped" : "not clipped") + (fixture.outcome().isEmpty() ? "" : " " + fixture.outcome());
	}

	@Test
	public void anAudioTracksClipIndicatorIsCleared() {
		assertEquals("not clipped success", reset(fixture.songManager.addAudioTrack(fixture.song, TGAudioInput.mono(1))));
	}

	@Test
	public void aGuitarTrackIsRefused() {
		assertEquals("clipped", reset(fixture.song.getTrack(0)));
	}
}

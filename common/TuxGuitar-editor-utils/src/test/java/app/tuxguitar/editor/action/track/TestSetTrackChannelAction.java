package app.tuxguitar.editor.action.track;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins TGSetTrackChannelAction: the caret track moves to the given channel, or to none. An audio track has a channel
 * of its own: it moves to no other channel, and no other track moves to an audio channel. Without a caret track
 * nothing changes.
 *
 * The song has channel 1 (the track's) and channel 2. It reads back as the track's channel id, then " / success" when
 * the action reported it.
 */
public class TestSetTrackChannelAction {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGTrack track = fixture.song.getTrack(0);
	private final TGChannel other = fixture.songManager.addChannel(fixture.song);

	private String setChannel(TGChannel channel) {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
		fixture.execute(new TGSetTrackChannelAction(fixture.context));
		return track.getChannelId() + fixture.outcome().replace("success", " / success");
	}

	private String setChannelOfAudioTrack(TGChannel channel) {
		fixture.song.getChannel(0).setAudioInput(TGAudioInput.mono(1));
		return setChannel(channel);
	}

	private String setAudioChannel() {
		other.setAudioInput(TGAudioInput.mono(1));
		return setChannel(other);
	}

	private String setWithoutTrack() {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, other);
		fixture.execute(new TGSetTrackChannelAction(fixture.context));
		return track.getChannelId() + fixture.outcome();
	}

	@TestFactory
	public List<DynamicTest> settingTheChannel() {
		return Arrays.asList(
			reads("the track moves to the channel", "2 / success", () -> {
				TestSetTrackChannelAction test = new TestSetTrackChannelAction();
				return test.setChannel(test.other);
			}),
			reads("the track moves to no channel", "-1 / success", () -> new TestSetTrackChannelAction().setChannel(null)),
			reads("an audio track keeps its channel", "1", () -> {
				TestSetTrackChannelAction test = new TestSetTrackChannelAction();
				return test.setChannelOfAudioTrack(test.other);
			}),
			reads("an audio track keeps its channel, even for none", "1", () -> new TestSetTrackChannelAction().setChannelOfAudioTrack(null)),
			reads("no track moves to an audio channel", "1", () -> new TestSetTrackChannelAction().setAudioChannel()),
			reads("without a caret track nothing changes", "1", () -> new TestSetTrackChannelAction().setWithoutTrack()));
	}
}

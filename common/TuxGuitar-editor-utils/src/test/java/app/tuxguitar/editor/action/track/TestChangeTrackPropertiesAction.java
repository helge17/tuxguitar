package app.tuxguitar.editor.action.track;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGColor;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins TGChangeTrackPropertiesAction: it runs set info, change tuning and set channel on the caret track, each only
 * when its update flag is true.
 *
 * The context asks for the name "Lead", offset 2, four strings and channel 7. The track ("track 1", offset 0, six
 * strings, channel 1) reads back as "name / offset / string count / channel".
 */
public class TestChangeTrackPropertiesAction {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGTrack track = fixture.song.getTrack(0);

	public TestChangeTrackPropertiesAction() {
		fixture.caret(fixture.beat(track.getMeasure(0), 0), 1);
		TGChannel channel = fixture.songManager.createChannel();
		channel.setChannelId(7);
		fixture.actionContext.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_NAME, "Lead");
		fixture.actionContext.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_OFFSET, 2);
		fixture.actionContext.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_COLOR, TGColor.newColor(10, 20, 30));
		fixture.actionContext.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_STRINGS, fixture.songManager.createDefaultInstrumentStrings(4));
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
		fixture.map(new TGSetTrackInfoAction(fixture.context), new TGChangeTrackTuningAction(fixture.context), new TGSetTrackChannelAction(fixture.context));
	}

	private String change(Boolean info, Boolean tuning, Boolean channel) {
		fixture.actionContext.setAttribute(TGChangeTrackPropertiesAction.ATTRIBUTE_UPDATE_INFO, info);
		fixture.actionContext.setAttribute(TGChangeTrackPropertiesAction.ATTRIBUTE_UPDATE_TUNING, tuning);
		fixture.actionContext.setAttribute(TGChangeTrackPropertiesAction.ATTRIBUTE_UPDATE_CHANNEL, channel);
		fixture.execute(new TGChangeTrackPropertiesAction(fixture.context));
		return track.getName() + " / " + track.getOffset() + " / " + track.stringCount() + " / " + track.getChannelId();
	}

	@TestFactory
	public List<DynamicTest> eachFlagRunsItsUpdate() {
		return Arrays.asList(
			changing("info", "Lead / 2 / 6 / 1", true, false, null),
			changing("tuning, which sets the offset too", "track 1 / 2 / 4 / 1", null, true, false),
			changing("channel", "track 1 / 0 / 6 / 7", false, null, true),
			changing("all of them", "Lead / 2 / 4 / 7", true, true, true),
			changing("none of them", "track 1 / 0 / 6 / 1", null, null, null));
	}

	private static DynamicTest changing(String name, String expected, Boolean info, Boolean tuning, Boolean channel) {
		return reads(name, expected, () -> new TestChangeTrackPropertiesAction().change(info, tuning, channel));
	}
}

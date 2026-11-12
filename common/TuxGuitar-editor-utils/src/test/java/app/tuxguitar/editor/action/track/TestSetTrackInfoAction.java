package app.tuxguitar.editor.action.track;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGColor;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins TGSetTrackInfoAction and TGSetTrackNameAction.
 *
 * Set info gives the caret track the name, color and offset, and, on a pitched track, the max fret, dropping the notes
 * above it. Set name keeps the track's color and offset and renames it through set info; it needs a name.
 * Without a caret track neither changes anything.
 *
 * The track ("track 1", offset 0, red, max fret 29) holds one beat, with notes at fret 3 and fret 20; it reads back as
 * "name / offset / r,g,b / max fret / notes".
 */
public class TestSetTrackInfoAction {

	private static final String UNCHANGED = "track 1 / 0 / 255,0,0 / 29 / s1f3 s2f20";

	private static final Consumer<TGActionContext> NAME_ONLY = context -> context.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_NAME, "Lead");
	private static final Consumer<TGActionContext> INFO = NAME_ONLY.andThen(context -> {
		context.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_OFFSET, 2);
		context.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_COLOR, TGColor.newColor(10, 20, 30));
	});
	private static final Consumer<TGActionContext> INFO_AND_MAX_FRET = INFO.andThen(context -> context.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_MAXFRET, 12));

	private static final Consumer<SongActionFixture> PITCHED = fixture -> {
	};
	private static final Consumer<SongActionFixture> PERCUSSION = fixture -> fixture.song.getChannel(0).setBank(TGChannel.DEFAULT_PERCUSSION_BANK);
	private static final Consumer<SongActionFixture> NO_CARET_TRACK = fixture -> fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, null);

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGTrack track = fixture.song.getTrack(0);
	private final TGBeat beat = fixture.beat(track.getMeasure(0), 0);

	public TestSetTrackInfoAction() {
		fixture.note(beat, 1, 3);
		fixture.note(beat, 2, 20);
		fixture.caret(beat, 1);
		fixture.map(new TGSetTrackInfoAction(fixture.context));
	}

	private String set(Consumer<SongActionFixture> given, Consumer<TGActionContext> attributes, boolean byName) {
		given.accept(fixture);
		attributes.accept(fixture.actionContext);
		fixture.execute(byName ? new TGSetTrackNameAction(fixture.context) : new TGSetTrackInfoAction(fixture.context));
		TGColor color = track.getColor();
		return track.getName() + " / " + track.getOffset() + " / " + color.getR() + "," + color.getG() + "," + color.getB()
				+ " / " + track.getMaxFret() + " / " + SongActionFixture.describe(beat);
	}

	@TestFactory
	public List<DynamicTest> settingTheInfo() {
		return Arrays.asList(
			setting("a pitched track takes the max fret and drops the notes above it", "Lead / 2 / 10,20,30 / 12 / s1f3", PITCHED, INFO_AND_MAX_FRET),
			setting("without max fret the max fret and notes stay", "Lead / 2 / 10,20,30 / 29 / s1f3 s2f20", PITCHED, INFO),
			setting("a percussion track ignores the max fret", "Lead / 2 / 10,20,30 / 29 / s1f3 s2f20", PERCUSSION, INFO_AND_MAX_FRET),
			setting("without caret track nothing changes", UNCHANGED, NO_CARET_TRACK, INFO_AND_MAX_FRET));
	}

	@TestFactory
	public List<DynamicTest> settingTheName() {
		return Arrays.asList(
			naming("the track keeps its offset and color", "Lead / 0 / 255,0,0 / 29 / s1f3 s2f20", PITCHED, NAME_ONLY),
			naming("without a name nothing changes", UNCHANGED, PITCHED, context -> {
			}),
			naming("without caret track nothing changes", UNCHANGED, NO_CARET_TRACK, NAME_ONLY));
	}

	private static DynamicTest naming(String name, String expected, Consumer<SongActionFixture> given, Consumer<TGActionContext> attributes) {
		return reads(name, expected, () -> new TestSetTrackInfoAction().set(given, attributes, true));
	}

	private static DynamicTest setting(String name, String expected, Consumer<SongActionFixture> given, Consumer<TGActionContext> attributes) {
		return reads(name, expected, () -> new TestSetTrackInfoAction().set(given, attributes, false));
	}
}

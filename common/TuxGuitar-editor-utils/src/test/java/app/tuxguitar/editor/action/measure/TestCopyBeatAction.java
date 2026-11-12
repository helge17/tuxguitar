package app.tuxguitar.editor.action.measure;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.clipboard.TGClipboard;
import app.tuxguitar.song.helpers.TGStoredBeatList;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;

/**
 * Pins TGCopyBeatAction: the clipboard takes a copy of the caret beat, with the tuning of the caret track and whether
 * it is a percussion track. Without a caret beat or a caret track the clipboard stays empty.
 *
 * The caret beat holds notes on strings 1 and 6 of a guitar in standard tuning. The clipboard reads back as
 * "beats / tuning / percussion", "empty" when it has nothing.
 */
public class TestCopyBeatAction {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGBeat beat = fixture.beat(fixture.song.getTrack(0).getMeasure(0), 0);

	public TestCopyBeatAction() {
		fixture.note(beat, 1, 3);
		fixture.note(beat, 6, 5);
		fixture.caret(beat, 1);
	}

	private String copy(Consumer<SongActionFixture> given) {
		given.accept(fixture);
		fixture.execute(new TGCopyBeatAction(fixture.context));
		TGClipboard clipboard = TGClipboard.getInstance(fixture.context);
		if (!clipboard.hasContents()) {
			return "empty";
		}
		TGStoredBeatList stored = clipboard.getBeats();
		StringBuilder beats = new StringBuilder();
		for (TGBeat copied : stored.getBeats()) {
			beats.append(copied == beat ? "same beat " : "").append(SongActionFixture.describe(copied));
		}
		return beats + " / " + stored.getStringValues() + " / " + stored.isPercussionTrack();
	}

	@TestFactory
	public List<DynamicTest> copyingTheCaretBeat() {
		return Arrays.asList(
			copying("the clipboard takes a copy with the tuning", "s1f3 s6f5 / [64, 59, 55, 50, 45, 40] / false", fixture -> {
			}),
			copying("a percussion track is marked so", "s1f3 s6f5 / [64, 59, 55, 50, 45, 40] / true", fixture -> fixture.song.getChannel(0).setBank(TGChannel.DEFAULT_PERCUSSION_BANK)),
			copying("without caret beat the clipboard stays empty", "empty", fixture -> fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, null)),
			copying("without caret track the clipboard stays empty", "empty", fixture -> fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, null)));
	}

	private static DynamicTest copying(String name, String expected, Consumer<SongActionFixture> given) {
		return reads(name, expected, () -> new TestCopyBeatAction().copy(given));
	}
}

package app.tuxguitar.editor.action.note;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGNote;

/**
 * Pins TGToggleNoteEnharmonicAction: the note under the caret toggles between its two enharmonic spellings, unless the
 * caret track is a percussion track; a missing caret track counts as pitched. Without a caret note nothing changes.
 *
 * The caret note reads back as "alt" when spelled the alternative way, "plain" otherwise.
 */
public class TestToggleNoteEnharmonicAction {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGNote note = fixture.note(fixture.beat(fixture.song.getTrack(0).getMeasure(0), 0), 1, 1);

	public TestToggleNoteEnharmonicAction() {
		fixture.caret(note.getVoice().getBeat(), 1);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, note);
	}

	private String toggle(Consumer<SongActionFixture> given) {
		given.accept(fixture);
		fixture.execute(new TGToggleNoteEnharmonicAction(fixture.context));
		return note.isAltEnharmonic() ? "alt" : "plain";
	}

	@TestFactory
	public List<DynamicTest> togglingTheSpelling() {
		return Arrays.asList(
			toggling("a plain note turns alternative", "alt", fixture -> {
			}),
			toggling("an alternative note turns plain", "plain", fixture -> fixture.actionContext.<TGNote>getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE).toggleAltEnharmonic()),
			toggling("without caret track the note toggles", "alt", fixture -> fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, null)),
			toggling("a percussion track is left alone", "plain", fixture -> fixture.song.getChannel(0).setBank(TGChannel.DEFAULT_PERCUSSION_BANK)),
			toggling("without caret note nothing changes", "plain", fixture -> fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, null)));
	}

	private static DynamicTest toggling(String name, String expected, Consumer<SongActionFixture> given) {
		return reads(name, expected, () -> new TestToggleNoteEnharmonicAction().toggle(given));
	}
}

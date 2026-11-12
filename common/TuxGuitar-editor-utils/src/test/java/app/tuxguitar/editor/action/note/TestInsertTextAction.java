package app.tuxguitar.editor.action.note;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGText;

/**
 * Pins TGInsertTextAction: the caret beat takes the text, replacing the one it had; an empty text removes it. Without
 * a text or without a caret beat nothing changes.
 *
 * The caret beat holds the text "old"; it reads back as its text, "-" for none.
 */
public class TestInsertTextAction {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGBeat beat = fixture.beat(fixture.song.getTrack(0).getMeasure(0), 0);

	public TestInsertTextAction() {
		fixture.caret(beat, 1);
		TGText old = fixture.songManager.getFactory().newText();
		old.setValue("old");
		beat.setText(old);
	}

	private String insert(String text) {
		fixture.actionContext.setAttribute(TGInsertTextAction.ATTRIBUTE_TEXT_VALUE, text);
		fixture.execute(new TGInsertTextAction(fixture.context));
		return beat.isTextBeat() ? beat.getText().getValue() : "-";
	}

	@TestFactory
	public List<DynamicTest> insertingText() {
		return Arrays.asList(
			reads("the caret beat takes the text", "verse", () -> new TestInsertTextAction().insert("verse")),
			reads("an empty text removes it", "-", () -> new TestInsertTextAction().insert("")),
			reads("without a text nothing changes", "old", () -> new TestInsertTextAction().insert(null)),
			reads("without caret beat nothing changes", "old", () -> {
				TestInsertTextAction test = new TestInsertTextAction();
				test.fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, null);
				return test.insert("verse");
			}));
	}
}

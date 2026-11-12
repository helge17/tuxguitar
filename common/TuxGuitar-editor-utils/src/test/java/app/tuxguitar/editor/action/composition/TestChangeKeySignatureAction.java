package app.tuxguitar.editor.action.composition;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGBeatRange;

/**
 * Pins TGChangeKeySignatureAction: with "apply to selection" and a non-empty beat selection, the measures of the
 * selected beats take the key signature; otherwise the caret measure (the second one) does, and with "apply to end"
 * every measure after it too. Without a caret measure nothing changes.
 *
 * The track has three measures, each holding one beat; it reads back as the key signature of each measure.
 */
public class TestChangeKeySignatureAction {

	private static final int KEY = 3;

	private static final Consumer<SongActionFixture> NO_SELECTION = fixture -> {
	};
	private static final Consumer<SongActionFixture> FIRST_AND_LAST_SELECTED = fixture -> fixture.selectBeats(
			fixture.song.getTrack(0).getMeasure(0).getBeat(0), fixture.song.getTrack(0).getMeasure(2).getBeat(0));
	private static final Consumer<SongActionFixture> EMPTY_SELECTION = fixture -> fixture.actionContext.setAttribute(
			TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE, TGBeatRange.empty());
	private static final Consumer<SongActionFixture> NO_CARET_MEASURE = fixture -> fixture.actionContext.setAttribute(
			TGDocumentContextAttributes.ATTRIBUTE_MEASURE, null);

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGTrack track = fixture.song.getTrack(0);

	public TestChangeKeySignatureAction() {
		fixture.measureCount(3);
		for (int m = 0; m < 3; m++) {
			fixture.beat(track.getMeasure(m), 0);
		}
		fixture.caret(track.getMeasure(1).getBeat(0), 1);
		fixture.actionContext.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_KEY_SIGNATURE, KEY);
	}

	private String change(Consumer<SongActionFixture> given, Boolean applyToSelection, Boolean applyToEnd) {
		given.accept(fixture);
		fixture.actionContext.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_APPLY_TO_SELECTION, applyToSelection);
		fixture.actionContext.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		fixture.execute(new TGChangeKeySignatureAction(fixture.context));
		return fixture.measures(measure -> String.valueOf(measure.getKeySignature()));
	}

	@TestFactory
	public List<DynamicTest> changingTheKeySignature() {
		return Arrays.asList(
			changing("the measures of the selection take the key", "3 | 0 | 3", FIRST_AND_LAST_SELECTED, true, true),
			changing("without apply to selection the caret measure takes the key", "0 | 3 | 0", FIRST_AND_LAST_SELECTED, false, false),
			changing("a missing apply to selection means the caret measure", "0 | 3 | 0", FIRST_AND_LAST_SELECTED, null, false),
			changing("an empty selection falls back on the caret measure", "0 | 3 | 0", EMPTY_SELECTION, true, false),
			changing("a missing selection falls back on the caret measure", "0 | 3 | 0", NO_SELECTION, true, false),
			changing("apply to end also changes the measures after the caret", "0 | 3 | 3", NO_SELECTION, false, true),
			changing("a missing apply to end changes the caret measure only", "0 | 3 | 0", NO_SELECTION, false, null),
			changing("without caret measure nothing changes", "0 | 0 | 0", NO_CARET_MEASURE, false, true));
	}

	private static DynamicTest changing(String name, String expected, Consumer<SongActionFixture> given, Boolean applyToSelection, Boolean applyToEnd) {
		return reads(name, expected, () -> new TestChangeKeySignatureAction().change(given, applyToSelection, applyToEnd));
	}
}

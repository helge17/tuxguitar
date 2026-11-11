package app.tuxguitar.editor.action.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGMeasureHeader;

/**
 * Pins TGRepeatAlternativeAction: the caret measure gets the given alternative endings (a bit per ending, the first
 * ending in bit 0). When a measure without endings gets endings that leave out the first one, the measure before it
 * also gets a repeat close (played twice), unless it already has one.
 *
 * The song has three measures, read back as "<endings>/<repeat close>" each, separated by " | ".
 */
public class TestRepeatAlternativeAction {

	private static final int FIRST_ENDING = 1;
	private static final int SECOND_ENDING = 2;

	private final SongActionFixture fixture = new SongActionFixture();

	public TestRepeatAlternativeAction() {
		fixture.songManager.addNewMeasureBeforeEnd(fixture.song);
		fixture.songManager.addNewMeasureBeforeEnd(fixture.song);
		TGActionManager.getInstance(fixture.context).mapAction(TGRepeatCloseAction.NAME, new TGRepeatCloseAction(fixture.context));
	}

	private TGMeasureHeader header(int number) {
		return fixture.song.getMeasureHeader(number - 1);
	}

	private String setEndings(int measure, int endings) {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header(measure));
		fixture.actionContext.setAttribute(TGRepeatAlternativeAction.ATTRIBUTE_REPEAT_ALTERNATIVE, endings);
		fixture.execute(new TGRepeatAlternativeAction(fixture.context));
		List<String> headers = new ArrayList<String>();
		for (int number = 1; number <= fixture.song.countMeasureHeaders(); number++) {
			headers.add(header(number).getRepeatAlternative() + "/" + header(number).getRepeatClose());
		}
		return String.join(" | ", headers);
	}

	@TestFactory
	public List<DynamicTest> settingTheEndings() {
		return Arrays.asList(
			setting("endings without the first close the repeat before", "0/1 | 2/0 | 0/0",
					test -> test.setEndings(2, SECOND_ENDING)),
			setting("the first ending leaves the measure before open", "0/0 | 1/0 | 0/0",
					test -> test.setEndings(2, FIRST_ENDING)),
			setting("endings with the first among them leave the measure before open", "0/0 | 3/0 | 0/0",
					test -> test.setEndings(2, FIRST_ENDING | SECOND_ENDING)),
			setting("changing existing endings leaves the measure before open", "0/0 | 2/0 | 0/0",
					test -> test.withEndings(2, FIRST_ENDING).setEndings(2, SECOND_ENDING)),
			setting("removing the endings leaves the measure before open", "0/0 | 0/0 | 0/0",
					test -> test.withEndings(2, SECOND_ENDING).setEndings(2, 0)),
			setting("an existing repeat close is kept", "0/3 | 2/0 | 0/0",
					test -> test.withRepeatClose(1, 3).setEndings(2, SECOND_ENDING)),
			setting("the first measure has no measure before to close", "2/0 | 0/0 | 0/0",
					test -> test.setEndings(1, SECOND_ENDING)),
			setting("no endings on a measure without endings change nothing", "0/0 | 0/0 | 0/0",
					test -> test.setEndings(2, 0)));
	}

	// a case run on a new song: setting the endings gives the song read back
	private static DynamicTest setting(String name, String expected, Function<TestRepeatAlternativeAction, String> setEndings) {
		return DynamicTest.dynamicTest(name, () -> assertEquals(expected, setEndings.apply(new TestRepeatAlternativeAction())));
	}

	private TestRepeatAlternativeAction withEndings(int measure, int endings) {
		header(measure).setRepeatAlternative(endings);
		return this;
	}

	private TestRepeatAlternativeAction withRepeatClose(int measure, int repeatClose) {
		header(measure).setRepeatClose(repeatClose);
		return this;
	}

	@Test
	public void theCaretStaysOnTheMeasureAfterClosingTheOneBefore() {
		setEndings(3, SECOND_ENDING);

		assertSame(header(3), fixture.actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER));
	}
}

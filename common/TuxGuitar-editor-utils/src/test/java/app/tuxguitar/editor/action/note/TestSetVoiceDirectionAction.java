package app.tuxguitar.editor.action.note;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.graphics.control.TGBeatGroup;
import app.tuxguitar.graphics.control.TGVoiceImpl;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

/**
 * Pins TGSetVoiceUpAction, TGSetVoiceDownAction and TGSetVoiceAutoAction: the stem direction (up, down, or none for
 * auto) goes to every voice of the caret voice's beat group, when the caret voice has notes and a beat group.
 * Otherwise nothing changes.
 *
 * The caret voice holds a note and shares its beat group with a partner voice; both read back as their directions,
 * "caret partner". Each action starts from a direction other than its own, so a change shows.
 */
public class TestSetVoiceDirectionAction {

	private static final String[] NAMES = { "none", "up", "down" };

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGVoiceImpl caret = new TGVoiceImpl(fixture.songManager.getFactory(), 0);
	private final TGVoiceImpl partner = new TGVoiceImpl(fixture.songManager.getFactory(), 0);
	private final TGBeatGroup group = new TGBeatGroup(0);

	public TestSetVoiceDirectionAction() {
		caret.setEmpty(false);
		caret.addNote(fixture.songManager.getFactory().newNote());
		caret.setBeatGroup(group);
		group.getVoices().add(caret);
		group.getVoices().add(partner);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, caret);
	}

	private String setDirection(Action action) {
		fixture.execute(action.create.apply(fixture.context));
		return NAMES[caret.getDirection()] + " " + NAMES[partner.getDirection()];
	}

	// an action, the direction it sets and the direction its cases start from
	private static final class Action {
		final String name;
		final Function<TGContext, TGActionBase> create;
		final int direction;
		final int start;

		Action(String name, Function<TGContext, TGActionBase> create, int direction, int start) {
			this.name = name;
			this.create = create;
			this.direction = direction;
			this.start = start;
		}
	}

	private static final Action[] ACTIONS = {
		new Action("up", TGSetVoiceUpAction::new, TGVoice.DIRECTION_UP, TGVoice.DIRECTION_NONE),
		new Action("down", TGSetVoiceDownAction::new, TGVoice.DIRECTION_DOWN, TGVoice.DIRECTION_NONE),
		new Action("auto", TGSetVoiceAutoAction::new, TGVoice.DIRECTION_NONE, TGVoice.DIRECTION_UP),
	};

	@TestFactory
	public List<DynamicTest> settingTheDirection() {
		List<DynamicTest> tests = new ArrayList<DynamicTest>();
		for (Action action : ACTIONS) {
			String set = NAMES[action.direction] + " " + NAMES[action.direction];
			String unchanged = NAMES[action.start] + " " + NAMES[action.start];
			tests.add(setting(action, "goes to the whole beat group", set, test -> {}));
			tests.add(setting(action, "leaves an empty voice alone", unchanged, test -> test.caret.setEmpty(true)));
			tests.add(setting(action, "leaves a rest voice alone", unchanged, test -> test.caret.clearNotes()));
			tests.add(setting(action, "leaves a voice without beat group alone", unchanged, test -> test.caret.setBeatGroup(null)));
			tests.add(setting(action, "needs a caret voice", unchanged,
					test -> test.fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, null)));
		}
		return tests;
	}

	// a case run on new voices: the action, after the arrangement, gives the directions read back
	private static DynamicTest setting(Action action, String name, String expected, Consumer<TestSetVoiceDirectionAction> arrange) {
		return DynamicTest.dynamicTest(action.name + " " + name, () -> {
			TestSetVoiceDirectionAction test = new TestSetVoiceDirectionAction();
			test.caret.setDirection(action.start);
			test.partner.setDirection(action.start);
			arrange.accept(test);
			assertEquals(expected, test.setDirection(action));
		});
	}
}

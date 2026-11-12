package app.tuxguitar.editor.action.effect;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

/**
 * Pins the actions that give the selected notes an effect with settings (bend, grace, harmonic, trill, tremolo bar,
 * tremolo picking): every selected note takes the effect from the action context, a missing one clearing it. Without
 * a selection, or with an empty one, nothing changes.
 *
 * Two beats hold a note each and the first beat a second note, which is never selected. They read back as
 * "first | second", a note holding the effect of the action context ending in "*", one holding another in "+".
 */
public class TestChangeNoteEffectAction {

	/** An effect action, the attribute it reads, a new effect for it, and the effect it gives a note. */
	private static class Change {
		final String name;
		final Function<TGContext, TGActionBase> action;
		final String attribute;
		final Function<TGFactory, Object> newEffect;
		final Function<TGNoteEffect, Object> effect;

		Change(String name, Function<TGContext, TGActionBase> action, String attribute, Function<TGFactory, Object> newEffect, Function<TGNoteEffect, Object> effect) {
			this.name = name;
			this.action = action;
			this.attribute = attribute;
			this.newEffect = newEffect;
			this.effect = effect;
		}
	}

	private static final Change BEND = new Change("bend", TGChangeBendNoteAction::new, TGChangeBendNoteAction.ATTRIBUTE_EFFECT, TGFactory::newEffectBend, TGNoteEffect::getBend);

	private static final List<Change> CHANGES = Arrays.asList(
		BEND,
		new Change("grace", TGChangeGraceNoteAction::new, TGChangeGraceNoteAction.ATTRIBUTE_EFFECT, TGFactory::newEffectGrace, TGNoteEffect::getGrace),
		new Change("harmonic", TGChangeHarmonicNoteAction::new, TGChangeHarmonicNoteAction.ATTRIBUTE_EFFECT, TGFactory::newEffectHarmonic, TGNoteEffect::getHarmonic),
		new Change("trill", TGChangeTrillNoteAction::new, TGChangeTrillNoteAction.ATTRIBUTE_EFFECT, TGFactory::newEffectTrill, TGNoteEffect::getTrill),
		new Change("tremolo bar", TGChangeTremoloBarAction::new, TGChangeTremoloBarAction.ATTRIBUTE_EFFECT, TGFactory::newEffectTremoloBar, TGNoteEffect::getTremoloBar),
		new Change("tremolo picking", TGChangeTremoloPickingAction::new, TGChangeTremoloPickingAction.ATTRIBUTE_EFFECT, TGFactory::newEffectTremoloPicking, TGNoteEffect::getTremoloPicking));

	private static final Consumer<TestChangeNoteEffectAction> NO_SELECTION = test -> {
	};
	private static final Consumer<TestChangeNoteEffectAction> SELECTION = test -> test.fixture.select(test.selection.toArray(new TGNote[0]));

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGMeasure measure = fixture.song.getTrack(0).getMeasure(0);
	private final TGBeat first = fixture.beat(measure, 0);
	private final TGBeat second = fixture.beat(measure, 1);
	private final List<TGNote> selection = Arrays.asList(fixture.note(first, 3, 5), fixture.note(second, 2, 7));

	public TestChangeNoteEffectAction() {
		fixture.note(first, 1, 0);
	}

	private String change(Change change, Object given, Consumer<TestChangeNoteEffectAction> selecting) {
		selecting.accept(this);
		fixture.actionContext.setAttribute(change.attribute, given);
		fixture.execute(change.action.apply(fixture.context));
		Function<TGNote, String> mark = note -> mark(change.effect.apply(note.getEffect()), given);
		return SongActionFixture.describe(first, mark) + " | " + SongActionFixture.describe(second, mark);
	}

	// with a new effect of the action
	private String change(Change change, Consumer<TestChangeNoteEffectAction> selecting) {
		return change(change, change.newEffect.apply(fixture.songManager.getFactory()), selecting);
	}

	private static String mark(Object held, Object given) {
		if (held == null) {
			return "";
		}
		return held == given ? "*" : "+";
	}

	@TestFactory
	public List<DynamicTest> eachActionGivesTheSelectedNotesItsEffect() {
		return CHANGES.stream().map(change -> reads(change.name, "s1f0 s3f5* | s2f7*", () -> new TestChangeNoteEffectAction().change(change, SELECTION)))
				.collect(Collectors.toList());
	}

	@TestFactory
	public List<DynamicTest> withoutSelectionEachActionChangesNothing() {
		return CHANGES.stream().map(change -> reads(change.name, "s1f0 s3f5 | s2f7", () -> new TestChangeNoteEffectAction().change(change, NO_SELECTION)))
				.collect(Collectors.toList());
	}

	@TestFactory
	public List<DynamicTest> forTheSelection() {
		return Arrays.asList(
			reads("a missing effect clears it", "s1f0 s3f5 | s2f7", () -> {
				TestChangeNoteEffectAction test = new TestChangeNoteEffectAction();
				test.selection.forEach(note -> note.getEffect().setBend(test.fixture.songManager.getFactory().newEffectBend()));
				return test.change(BEND, null, SELECTION);
			}),
			reads("an empty one changes nothing", "s1f0 s3f5 | s2f7", () -> new TestChangeNoteEffectAction().change(BEND, test -> test.fixture.actionContext.setAttribute(
					TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE, TGNoteRange.empty()))));
	}
}

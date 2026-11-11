package app.tuxguitar.editor.action.effect;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

/**
 * Pins the note effect toggles: the selected notes all get the effect, or all lose it when they all had it. Without
 * a selection nothing changes. Most effects leave a percussion track alone; accents and ghost notes apply there too.
 *
 * The shared decision is pinned once, through fade-in; then each action is shown to toggle its own effect.
 * Two beats are read back as "first | second", a note with the effect ending in "*".
 */
public class TestToggleNoteEffectAction {

	/** An effect toggle action, the effect it reads, and whether it acts on a percussion track. */
	private static class Toggle {
		final String name;
		final Function<TGContext, TGActionBase> action;
		final Predicate<TGNoteEffect> effect;
		final boolean onPercussion;

		Toggle(String name, Function<TGContext, TGActionBase> action, Predicate<TGNoteEffect> effect, boolean onPercussion) {
			this.name = name;
			this.action = action;
			this.effect = effect;
			this.onPercussion = onPercussion;
		}
	}

	private static final Toggle FADE_IN = new Toggle("fade-in", TGChangeFadeInAction::new, TGNoteEffect::isFadeIn, false);

	private static final List<Toggle> TOGGLES = Arrays.asList(
		FADE_IN,
		new Toggle("hammer", TGChangeHammerNoteAction::new, TGNoteEffect::isHammer, false),
		new Toggle("let ring", TGChangeLetRingAction::new, TGNoteEffect::isLetRing, false),
		new Toggle("palm mute", TGChangePalmMuteAction::new, TGNoteEffect::isPalmMute, false),
		new Toggle("popping", TGChangePoppingAction::new, TGNoteEffect::isPopping, false),
		new Toggle("slapping", TGChangeSlappingAction::new, TGNoteEffect::isSlapping, false),
		new Toggle("slide", TGChangeSlideNoteAction::new, TGNoteEffect::isSlide, false),
		new Toggle("staccato", TGChangeStaccatoAction::new, TGNoteEffect::isStaccato, false),
		new Toggle("tapping", TGChangeTappingAction::new, TGNoteEffect::isTapping, false),
		new Toggle("vibrato", TGChangeVibratoNoteAction::new, TGNoteEffect::isVibrato, false),
		new Toggle("accentuated", TGChangeAccentuatedNoteAction::new, TGNoteEffect::isAccentuatedNote, true),
		new Toggle("ghost", TGChangeGhostNoteAction::new, TGNoteEffect::isGhostNote, true),
		new Toggle("heavy accentuated", TGChangeHeavyAccentuatedNoteAction::new, TGNoteEffect::isHeavyAccentuatedNote, true));

	/** A song with two beats and the caret on the first one. */
	private static class Song {
		final SongActionFixture fixture = new SongActionFixture();
		final TGMeasure measure = fixture.song.getTrack(0).getMeasure(0);
		final TGBeat first = fixture.beat(measure, 0);
		final TGBeat second = fixture.beat(measure, 1);

		Song() {
			fixture.caret(first, 3);
		}

		TGNote fadingIn(TGBeat beat, int string, int fret) {
			TGNote note = fixture.note(beat, string, fret);
			note.getEffect().setFadeIn(true);
			return note;
		}

		void percussion() {
			fixture.song.getChannel(0).setBank(TGChannel.DEFAULT_PERCUSSION_BANK);
		}

		String run(Toggle toggle) {
			fixture.execute(toggle.action.apply(fixture.context));
			Function<TGNote, String> mark = note -> toggle.effect.test(note.getEffect()) ? "*" : "";
			return SongActionFixture.describe(first, mark) + " | " + SongActionFixture.describe(second, mark);
		}
	}

	private final Song song = new Song();

	@Test
	public void aSelectionWithoutTheEffectGetsItEverywhere() {
		song.fixture.select(song.fixture.note(song.first, 3, 5), song.fixture.note(song.second, 2, 7));

		assertEquals("s3f5* | s2f7*", song.run(FADE_IN));
	}

	@Test
	public void aSelectionPartlyWithTheEffectGetsItEverywhere() {
		song.fixture.select(song.fixture.note(song.first, 3, 5), song.fadingIn(song.second, 2, 7));

		assertEquals("s3f5* | s2f7*", song.run(FADE_IN));
	}

	@Test
	public void aSelectionAllWithTheEffectLosesIt() {
		song.fixture.select(song.fadingIn(song.first, 3, 5), song.fadingIn(song.second, 2, 7));

		assertEquals("s3f5 | s2f7", song.run(FADE_IN));
	}

	@Test
	public void notesOutsideTheSelectionAreLeftAlone() {
		song.fixture.note(song.first, 3, 5);
		song.fixture.select(song.fixture.note(song.second, 2, 7));

		assertEquals("s3f5 | s2f7*", song.run(FADE_IN));
	}

	@Test
	public void withoutSelectionNothingChanges() {
		song.fixture.note(song.first, 3, 5);

		assertEquals("s3f5 | ", song.run(FADE_IN));
	}

	@Test
	public void anEmptySelectionChangesNothing() {
		song.fixture.note(song.first, 3, 5);
		song.fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE, TGNoteRange.empty());

		assertEquals("s3f5 | ", song.run(FADE_IN));
	}

	@TestFactory
	public Stream<DynamicTest> eachActionTogglesItsOwnEffectOnAndOff() {
		return TOGGLES.stream().map(toggle -> DynamicTest.dynamicTest(toggle.name, () -> {
			Song fresh = new Song();
			fresh.fixture.select(fresh.fixture.note(fresh.first, 3, 5));

			assertEquals("s3f5* | ", fresh.run(toggle));
			assertEquals("s3f5 | ", fresh.run(toggle));
		}));
	}

	@TestFactory
	public Stream<DynamicTest> onlyAccentsAndGhostNotesApplyOnAPercussionTrack() {
		return TOGGLES.stream().map(toggle -> DynamicTest.dynamicTest(toggle.name, () -> {
			Song fresh = new Song();
			fresh.percussion();
			fresh.fixture.select(fresh.fixture.note(fresh.first, 3, 5));

			assertEquals(toggle.onPercussion ? "s3f5* | " : "s3f5 | ", fresh.run(toggle));
		}));
	}
}

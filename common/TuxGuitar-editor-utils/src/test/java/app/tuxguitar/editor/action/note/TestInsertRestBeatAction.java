package app.tuxguitar.editor.action.note;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins TGInsertRestBeatAction, at the caret beat and voice. In free edition mode a rest as long as the caret voice
 * goes in front of it, the beats from there moving right. Otherwise an empty caret voice gets a rest of the duration
 * to set, and a caret voice with a duration moves the beats from the caret on right by its length.
 *
 * The measure is full with four quarter beats, two notes then two rests, the caret on the first; the duration to set
 * is an eighth. Each beat, in start order, reads back as "@start in eighths voice 0 / voice 1", a voice as its duration then "r" for a rest or its
 * notes, "-" when empty.
 */
public class TestInsertRestBeatAction {

	private static final Consumer<TestInsertRestBeatAction> AT_THE_CARET = test -> {
	};

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGMeasure measure = fixture.song.getTrack(0).getMeasure(0);
	private final TGBeat first = fixture.beat(measure, 0);

	public TestInsertRestBeatAction() {
		fixture.note(first, 1, 0);
		fixture.note(fixture.beat(measure, 1), 1, 1);
		fixture.beat(measure, 2);
		fixture.beat(measure, 3);
		fixture.caret(first, 1);
		TGDuration eighth = fixture.songManager.getFactory().newDuration();
		eighth.setValue(TGDuration.EIGHTH);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION, eighth);
	}

	private String insert(Consumer<TestInsertRestBeatAction> given) {
		given.accept(this);
		fixture.execute(new TGInsertRestBeatAction(fixture.context));
		List<TGBeat> byStart = new ArrayList<TGBeat>(measure.getBeats());
		byStart.sort(Comparator.comparingLong(TGBeat::getStart));
		List<String> beats = new ArrayList<String>();
		for (TGBeat beat : byStart) {
			beats.add("@" + (beat.getStart() - measure.getStart()) / (TGDuration.QUARTER_TIME / 2) + " " + read(beat.getVoice(0)) + " / " + read(beat.getVoice(1)));
		}
		return String.join(" | ", beats);
	}

	private static String read(TGVoice voice) {
		if (voice.isEmpty()) {
			return "-";
		}
		return voice.getDuration().getValue() + (voice.isRestVoice() ? "r" : SongActionFixture.describe(voice.getBeat()));
	}

	@TestFactory
	public List<DynamicTest> insertingARest() {
		return Arrays.asList(
			inserting("in free edition mode a rest goes in front, overfilling the measure", "@0 4r / - | @2 4s1f0 / - | @4 4s1f1 / - | @6 4r / - | @8 4r / -", test -> test.fixture.songManager.setFreeEditionMode(true)),
			inserting("a caret voice with a duration moves the beats right, the last rest dropping out", "@0 4r / - | @2 4s1f0 / - | @4 4s1f1 / - | @6 4r / -", AT_THE_CARET),
			inserting("an empty caret voice gets a rest, then rests to the measure end", "@0 4s1f0 / 8r | @1 - / 8r | @2 4s1f1 / 8r | @3 - / 8r | @4 4r / 8r | @5 - / 8r | @6 4r / 8r | @7 - / 8r", test -> test.fixture.actionContext.setAttribute(
					TGDocumentContextAttributes.ATTRIBUTE_VOICE, test.first.getVoice(1))));
	}

	private static DynamicTest inserting(String name, String expected, Consumer<TestInsertRestBeatAction> given) {
		return reads(name, expected, () -> new TestInsertRestBeatAction().insert(given));
	}
}

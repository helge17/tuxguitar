package app.tuxguitar.song.managers;

import static app.tuxguitar.song.managers.MeasureFixture.QUARTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGPickStroke;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.TGText;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins TGMeasureManager.moveVoice: a voice moves from its beat to another one of a 4/4 measure, taking along the
 * text of its beat when no other voice is there, and the chord and strokes when no other voice holds notes.
 * A beat is "position:<voice 0><voice 1> [text] [chord] [stroke <direction>] [pick <direction>]",
 * position in quarters, a voice "n" (notes), "r" (rest) or "_" (empty).
 */
public class TestMoveVoice {

	private final MeasureFixture fixture = new MeasureFixture();

	private static String kind(TGVoice voice) {
		return voice.isEmpty() ? "_" : (voice.isRestVoice() ? "r" : "n");
	}

	private String describe(TGBeat beat) {
		StringBuilder described = new StringBuilder();
		described.append((beat.getPreciseStart() - fixture.measure.getPreciseStart()) / QUARTER).append(':');
		described.append(kind(beat.getVoice(0))).append(kind(beat.getVoice(1)));
		described.append(beat.isTextBeat() ? " text" : "").append(beat.isChordBeat() ? " chord" : "");
		described.append(beat.getStroke().getDirection() != TGStroke.STROKE_NONE ? " stroke " + beat.getStroke().getDirection() : "");
		described.append(beat.getPickStroke().getDirection() != TGPickStroke.PICK_STROKE_NONE ? " pick " + beat.getPickStroke().getDirection() : "");
		return described.toString();
	}

	private String beats() {
		List<TGBeat> beats = new ArrayList<TGBeat>(fixture.measure.getBeats());
		beats.sort(Comparator.comparingLong(TGBeat::getPreciseStart));
		List<String> described = new ArrayList<String>();
		for (TGBeat beat : beats) {
			described.add(describe(beat));
		}
		return String.join(", ", described);
	}

	// the beat at quarter 0 with a text, a chord, a stroke down and a pick stroke up
	private TGBeat decorated() {
		TGBeat beat = fixture.beatAt(0);
		TGText text = fixture.factory.newText();
		text.setValue("text");
		beat.setText(text);
		beat.setChord(fixture.factory.newChord(6));
		beat.getStroke().setDirection(TGStroke.STROKE_DOWN);
		beat.getPickStroke().setDirection(TGPickStroke.PICK_STROKE_UP);
		return beat;
	}

	private String moveVoice0ToTheSecondQuarter() {
		TGVoice voice = fixture.beatAt(0).getVoice(0);
		fixture.manager.moveVoice(voice, fixture.beatAt(QUARTER));
		return beats();
	}

	@Test
	public void aVoiceAloneInItsBeatTakesEverythingAlongAndItsBeatIsRemoved() {
		fixture.note(0, 4, 0);
		decorated();

		assertEquals("1:n_ text chord stroke -1 pick 1", moveVoice0ToTheSecondQuarter());
	}

	@Test
	public void aVoiceBesideARestLeavesTheTextButTakesTheChordAndStrokes() {
		fixture.note(0, 4, 0);
		fixture.rest(0, 4, 1);
		decorated();

		assertEquals("0:_r text, 1:n_ chord stroke -1 pick 1", moveVoice0ToTheSecondQuarter());
	}

	@Test
	public void aVoiceBesideNotesLeavesEverythingBehind() {
		fixture.note(0, 4, 0);
		fixture.note(0, 4, 1);
		decorated();

		assertEquals("0:_n text chord stroke -1 pick 1, 1:n_", moveVoice0ToTheSecondQuarter());
	}

	@Test
	public void aBeatWithoutChordOrStrokesLeavesThoseOfTheTargetBeat() {
		fixture.note(0, 4, 0);
		TGBeat target = fixture.beatAt(QUARTER);
		target.setChord(fixture.factory.newChord(6));
		target.getStroke().setDirection(TGStroke.STROKE_UP);
		target.getPickStroke().setDirection(TGPickStroke.PICK_STROKE_DOWN);
		TGChord chord = target.getChord();

		String moved = moveVoice0ToTheSecondQuarter();

		assertEquals("1:n_ chord stroke 1 pick -1 same chord true", moved + " same chord " + (target.getChord() == chord));
	}

	@Test
	public void aVoiceNoLongerHeldByItsBeatJustJoinsTheTarget() {
		fixture.note(0, 4, 0);
		TGBeat beat = decorated();
		TGVoice detached = beat.getVoice(0);
		beat.setVoice(0, fixture.factory.newVoice(0));
		beat.getVoice(1).setEmpty(false);
		detached.setBeat(beat);

		fixture.manager.moveVoice(detached, fixture.beatAt(QUARTER));

		assertEquals("0:_r text chord stroke -1 pick 1, 1:n_", beats());
	}

	@Test
	public void aVoiceMovedToItsOwnBeatStaysPut() {
		fixture.note(0, 4, 0);
		decorated();

		fixture.manager.moveVoice(fixture.beatAt(0).getVoice(0), fixture.beatAt(0));

		assertEquals("0:n_ text chord stroke -1 pick 1", beats());
	}
}

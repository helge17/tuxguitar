package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.effects.TGEffectBend;
import app.tuxguitar.song.models.effects.TGEffectGrace;
import app.tuxguitar.song.models.effects.TGEffectHarmonic;
import app.tuxguitar.song.models.effects.TGEffectTremoloBar;
import app.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import app.tuxguitar.song.models.effects.TGEffectTrill;
import org.junit.jupiter.api.Test;

/**
 * Characterization of the MIDI notes MidiSequenceParser produces for each note, per note effect.
 * Ticks: the first measure starts at 960 and a quarter lasts 960 ticks (see ParserFixture).
 */
public class TestSequenceParserNotes {

	private static final String INITIAL_BEND = "bend 960 t1 c1 64 s-1 b0";

	private final ParserFixture song = new ParserFixture();

	// ---- expected event lines (format: see MidiEventRecorder) ----

	private static String[] note(long start, long end, int key, int velocity, int voice, int bendMode) {
		String rest = " t1 c1 k" + key + " v" + velocity + " s" + voice + " b" + bendMode;
		return new String[] {"on " + start + rest, "off " + end + rest};
	}

	// default velocity (95), string 1, no bend mode
	private static String[] note(long start, long end, int key) {
		return note(start, end, key, 95, 1, 0);
	}

	private static String bend(long tick, int value) {
		return "bend " + tick + " t1 c1 " + value + " s1 b1";
	}

	private static List<String> events(Object... items) {
		List<String> events = new ArrayList<String>();
		for (Object item : items) {
			if (item instanceof String[]) {
				events.addAll(Arrays.asList((String[]) item));
			} else {
				events.add((String) item);
			}
		}
		return events;
	}

	// ---- plain notes ----

	@Test
	public void plainNoteIsKeyOfStringPlusFret() {
		song.quarter(1, 0);
		assertEquals(events(note(960, 1920, 64)), song.notes());
	}

	@Test
	public void keyAddsTransposeAndTrackOffsetAndUsesStringAsMidiVoice() {
		song.transpose = 2;
		song.track.setOffset(12);
		song.quarter(2, 3);
		assertEquals(events(note(960, 1920, 76, 95, 2, 0)), song.notes());
	}

	@Test
	public void keyIsClampedTo127() {
		song.transpose = 100;
		song.quarter(1, 0);
		assertEquals(events(note(960, 1920, 127)), song.notes());
	}

	@Test
	public void tiedNoteExtendsThePreviousNoteInsteadOfPlaying() {
		song.quarter(1, 5);
		song.note(4, TGDuration.QUARTER, 1, 5).setTiedNote(true);
		assertEquals(events(note(960, 2880, 69)), song.notes());
	}

	@Test
	public void aChainOfTiedNotesLastsAsLongAsAllOfThem() {
		song.quarter(1, 5);
		song.note(4, TGDuration.QUARTER, 1, 5).setTiedNote(true);
		song.note(8, TGDuration.QUARTER, 1, 5).setTiedNote(true);
		assertEquals(events(note(960, 3840, 69)), song.notes());
	}

	// on the second pass of a repeat the tie is measured from the repeated note
	@Test
	public void aNoteTiedOverTheBarLastsAsLongOnEveryPassOfARepeat() {
		song.measure(0).getHeader().setRepeatOpen(true);
		song.measure(1).getHeader().setRepeatClose(1);
		song.note(12, TGDuration.QUARTER, 1, 5);
		song.note(1, 0, TGDuration.QUARTER, 1, 5, 0).setTiedNote(true);
		assertEquals(events(note(3840, 5760, 69), note(11520, 13440, 69)), song.notes());
	}

	@Test
	public void voicesArePlayedInOrder() {
		song.note(0, 0, TGDuration.QUARTER, 1, 0, 0);
		song.note(0, 0, TGDuration.HALF, 2, 0, 1);
		assertEquals(events(note(960, 1920, 64), note(960, 2880, 59, 95, 2, 0)), song.notes());
	}

	@Test
	public void chordNotesArePlayedInVoiceOrder() {
		song.quarter(3, 0);
		song.quarter(1, 0);
		assertEquals(events(note(960, 1920, 55, 95, 3, 0), note(960, 1920, 64)), song.notes());
	}

	// quarters at fret 0 on the strings (a negative string: a tied note) of the first beat, stroked with value 8
	// (60 ticks from one string to the next): the notes played
	private List<String> stroked(int direction, int... strings) {
		for (int string : strings) {
			song.quarter(Math.abs(string), 0).setTiedNote(string < 0);
		}
		TGStroke stroke = song.beat(0, 0).getStroke();
		stroke.setDirection(direction);
		stroke.setValue(8);
		return song.notes();
	}

	@Test
	public void aDownStrokeDelaysHigherStringsAndAnUpStrokeLowerOnes() {
		List<String> down = stroked(TGStroke.STROKE_DOWN, 1, 2);
		song.beat(0, 0).getVoice(0).getNotes().clear();

		assertEquals(events(note(1020, 1920, 64), note(960, 1920, 59, 95, 2, 0)), down);
		assertEquals(events(note(960, 1920, 59, 95, 2, 0), note(1020, 1920, 55, 95, 3, 0)), stroked(TGStroke.STROKE_UP, 2, 3));
	}

	@Test
	public void tiedNotesTakeNoPlaceInTheStroke() {
		assertEquals(events(note(1080, 1920, 64), note(1020, 1920, 55, 95, 3, 0), note(960, 1920, 40, 95, 6, 0)),
				stroked(TGStroke.STROKE_DOWN, 1, -2, 3, 6));
	}

	@Test
	public void aBeatWithoutStrokeAfterAStrokedOneIsNotDelayed() {
		song.note(4, TGDuration.QUARTER, 1, 0);
		song.note(4, TGDuration.QUARTER, 2, 0);
		assertEquals(events(note(1020, 1920, 64), note(960, 1920, 59, 95, 2, 0),
				note(1920, 2880, 64), note(1920, 2880, 59, 95, 2, 0)), stroked(TGStroke.STROKE_DOWN, 1, 2));
	}

	// ---- default controls ----

	// with default controls, every channel gets its pitch bend range (12 semitones) on the info track, at the song start
	@Test
	public void defaultControlsSetThePitchBendRangeOfEveryChannel() {
		List<String> without = song.parse().events("cc");
		song.flags = MidiSequenceParser.ADD_DEFAULT_CONTROLS;
		List<String> infoTrack = new ArrayList<String>(song.parse().events("cc"));
		infoTrack.removeIf(event -> !event.contains(" t0 "));

		assertEquals(List.of(), without);
		assertEquals(List.of("cc 960 t0 c1 101=0", "cc 960 t0 c1 100=0", "cc 960 t0 c1 6=12", "cc 960 t0 c1 38=0"), infoTrack);
	}

	// ---- metronome ----

	private static String[] tick(long start, long end) {
		String rest = " t2 c0 k37 v95 s-1 b0";
		return new String[] {"on " + start + rest, "off " + end + rest};
	}

	@Test
	public void metronomeTicksEveryBeatOfTheTimeSignatureOnTheLastTrack() {
		song.flags = MidiSequenceParser.ADD_METRONOME;
		song.song.getMeasureHeader(0).getTimeSignature().setNumerator(3);
		song.song.getMeasureHeader(0).getTimeSignature().getDenominator().setValue(TGDuration.EIGHTH);
		assertEquals(events(tick(960, 1440), tick(1440, 1920), tick(1920, 2400)), song.notes());
	}

	@Test
	public void noMetronomeWithoutItsFlagOrWithoutItsChannel() {
		song.metronomeChannelId = 9;
		List<String> withoutFlag = song.notes();
		song.flags = MidiSequenceParser.ADD_METRONOME;
		song.metronomeChannelId = -1;
		assertEquals(List.of(List.of(), List.of()), List.of(withoutFlag, song.notes()));
	}

	// ---- duration effects ----

	@Test
	public void deadNoteIsShortened() {
		song.quarter(1, 0).getEffect().setDeadNote(true);
		assertEquals(events(note(960, 1020, 64)), song.notes());
	}

	@Test
	public void palmMuteIsShortened() {
		song.quarter(1, 0).getEffect().setPalmMute(true);
		assertEquals(events(note(960, 1080, 64)), song.notes());
	}

	@Test
	public void staccatoHalvesTheDuration() {
		song.quarter(1, 0).getEffect().setStaccato(true);
		assertEquals(events(note(960, 1440, 64)), song.notes());
	}

	// ---- velocity effects ----

	@Test
	public void ghostNoteIsSofter() {
		song.quarter(1, 0).getEffect().setGhostNote(true);
		assertEquals(events(note(960, 1920, 64, 79, 1, 0)), song.notes());
	}

	@Test
	public void accentuatedNoteIsLouder() {
		song.quarter(1, 0).getEffect().setAccentuatedNote(true);
		assertEquals(events(note(960, 1920, 64, 111, 1, 0)), song.notes());
	}

	@Test
	public void heavyAccentuatedNoteIsLouderStillAndClamped() {
		song.quarter(1, 0).getEffect().setHeavyAccentuatedNote(true);
		assertEquals(events(note(960, 1920, 64, 127, 1, 0)), song.notes());
	}

	@Test
	public void noteAfterHammerIsSofter() {
		song.quarter(1, 0).getEffect().setHammer(true);
		song.note(4, TGDuration.QUARTER, 1, 2);
		assertEquals(events(note(960, 1920, 64), note(1920, 2880, 66, 70, 1, 0)), song.notes());
	}

	@Test
	public void aHammerNoteIsNotSofterItself() {
		song.quarter(1, 0).getEffect().setHammer(true);
		assertEquals(events(note(960, 1920, 64)), song.notes());
	}

	@Test
	public void noteAfterAHammerInThePreviousMeasureIsSofter() {
		song.note(0, TGDuration.QUARTER, 2, 0);
		song.note(12, TGDuration.QUARTER, 1, 0).getEffect().setHammer(true);
		song.note(1, 0, TGDuration.QUARTER, 1, 2, 0);
		assertEquals(events(note(960, 1920, 59, 95, 2, 0), note(3840, 4800, 64), note(4800, 5760, 66, 70, 1, 0)), song.notes());
	}

	// played from the second measure, whose first note is a hammer
	@Test
	public void noteAfterAHammerInTheFirstMeasurePlayedIsSofter() {
		song.sHeader = 2;
		song.note(1, 0, TGDuration.QUARTER, 1, 0, 0).getEffect().setHammer(true);
		song.note(1, 4, TGDuration.QUARTER, 1, 2, 0);
		assertEquals(events(note(4800, 5760, 64), note(5760, 6720, 66, 70, 1, 0)), song.notes());
	}

	@Test
	public void noteAfterHammerIsNotSofterOnPercussion() {
		song.percussion();
		song.quarter(1, 0).getEffect().setHammer(true);
		song.note(4, TGDuration.QUARTER, 1, 2);
		assertEquals(events(note(960, 1920, 64), note(1920, 2880, 66)), song.notes());
	}

	// ---- fade in ----

	@Test
	public void fadeInRaisesExpressionOverTheNoteWithMixerMessages() {
		song.flags = MidiSequenceParser.ADD_MIXER_MESSAGES;
		song.quarter(1, 0).getEffect().setFadeIn(true);
		MidiEventRecorder recorder = song.parse();
		List<String> expression = new ArrayList<String>();
		for (String event : recorder.events("cc")) {
			if (event.contains(" " + MidiControllers.EXPRESSION + "=")) {
				expression.add(event);
			}
		}
		// track setup (127), then 31..126 every 10 ticks, then 127 at the note end
		assertEquals(98, expression.size());
		assertEquals("cc 960 t1 c1 11=127", expression.get(0));
		assertEquals("cc 960 t1 c1 11=31", expression.get(1));
		assertEquals("cc 970 t1 c1 11=32", expression.get(2));
		assertEquals("cc 1910 t1 c1 11=126", expression.get(96));
		assertEquals("cc 1920 t1 c1 11=127", expression.get(97));
		assertEquals(events(note(960, 1920, 64)), recorder.events("on", "off"));
	}

	@Test
	public void fadeInNeedsMixerMessages() {
		song.quarter(1, 0).getEffect().setFadeIn(true);
		MidiEventRecorder recorder = song.parse();
		assertEquals(events(), recorder.events("cc"));
		assertEquals(events(note(960, 1920, 64)), recorder.events("on", "off"));
	}

	// ---- grace ----

	private TGEffectGrace grace(TGNote note, int fret, int duration) {
		TGEffectGrace grace = song.factory.newEffectGrace();
		grace.setFret(fret);
		grace.setDuration(duration);
		grace.setDynamic(80);
		note.getEffect().setGrace(grace);
		return grace;
	}

	@Test
	public void graceIsPlayedBeforeTheBeatWithoutTranspose() {
		song.transpose = 2;
		grace(song.note(4, TGDuration.QUARTER, 1, 5), 3, TGEffectGrace.DURATION_THIRTY_SECOND);
		assertEquals(events(note(1800, 1920, 67, 80, 1, 1), note(1920, 2880, 71, 95, 1, 1)), song.notes());
	}

	@Test
	public void graceThatWouldStartBeforeTheSongIsPlayedOnTheBeat() {
		grace(song.quarter(1, 5), 3, TGEffectGrace.DURATION_THIRTY_SECOND);
		assertEquals(events(note(960, 1080, 67, 80, 1, 1), note(1080, 1920, 69, 95, 1, 1)), song.notes());
	}

	@Test
	public void graceStartingExactlyAtTheSongStartIsPlayedBeforeTheBeat() {
		grace(song.note(1, TGDuration.SIXTEENTH, 1, 5), 3, TGEffectGrace.DURATION_SIXTEENTH);
		assertEquals(events(note(960, 1200, 67, 80, 1, 1), note(1200, 1440, 69, 95, 1, 1)), song.notes());
	}

	@Test
	public void graceOnBeatDelaysTheNote() {
		grace(song.note(4, TGDuration.QUARTER, 1, 5), 3, TGEffectGrace.DURATION_THIRTY_SECOND).setOnBeat(true);
		assertEquals(events(note(1920, 2040, 67, 80, 1, 1), note(2040, 2880, 69, 95, 1, 1)), song.notes());
	}

	@Test
	public void deadGraceIsShortened() {
		grace(song.note(4, TGDuration.QUARTER, 1, 5), 3, TGEffectGrace.DURATION_THIRTY_SECOND).setDead(true);
		assertEquals(events(note(1800, 1860, 67, 80, 1, 1), note(1920, 2880, 69, 95, 1, 1)), song.notes());
	}

	@Test
	public void graceOnPercussionHasNoBendMode() {
		song.percussion();
		grace(song.note(4, TGDuration.QUARTER, 1, 5), 3, TGEffectGrace.DURATION_THIRTY_SECOND);
		assertEquals(events(note(1800, 1920, 67, 80, 1, 0), note(1920, 2880, 69)), song.notes());
	}

	// ---- trill and tremolo picking ----

	private void trill(TGNote note, int fret, int durationValue) {
		TGEffectTrill trill = song.factory.newEffectTrill();
		trill.setFret(fret);
		trill.getDuration().setValue(durationValue);
		note.getEffect().setTrill(trill);
	}

	private void tremoloPicking(TGNote note, int durationValue) {
		TGEffectTremoloPicking tremoloPicking = song.factory.newEffectTremoloPicking();
		tremoloPicking.getDuration().setValue(durationValue);
		note.getEffect().setTremoloPicking(tremoloPicking);
	}

	@Test
	public void trillAlternatesNoteAndTrillFretUntilTheNoteEnds() {
		trill(song.quarter(1, 0), 2, TGDuration.SIXTEENTH);
		assertEquals(events(note(960, 1200, 64), note(1200, 1440, 66), note(1440, 1680, 64), note(1680, 1919, 66)), song.notes());
	}

	@Test
	public void trillKeepsBendModeOfGraceAndIgnoresTranspose() {
		song.transpose = 1;
		TGNote note = song.note(4, TGDuration.QUARTER, 1, 0);
		trill(note, 2, TGDuration.EIGHTH);
		grace(note, 3, TGEffectGrace.DURATION_THIRTY_SECOND);
		assertEquals(events(note(1800, 1920, 67, 80, 1, 1), note(1920, 2400, 65, 95, 1, 1), note(2400, 2879, 66, 95, 1, 1)), song.notes());
	}

	@Test
	public void trillIsIgnoredOnPercussion() {
		song.percussion();
		trill(song.quarter(1, 0), 2, TGDuration.SIXTEENTH);
		assertEquals(events(note(960, 1920, 64)), song.notes());
	}

	@Test
	public void tremoloPickingRepeatsTheNoteUntilItEnds() {
		tremoloPicking(song.quarter(1, 0), TGDuration.EIGHTH);
		assertEquals(events(note(960, 1440, 64), note(1440, 1919, 64)), song.notes());
	}

	@Test
	public void tremoloPickingAlsoPlaysOnPercussion() {
		song.percussion();
		tremoloPicking(song.quarter(1, 0), TGDuration.SIXTEENTH);
		assertEquals(events(note(960, 1200, 64), note(1200, 1440, 64), note(1440, 1680, 64), note(1680, 1919, 64)), song.notes());
	}

	@Test
	public void tremoloPickingStopsWhenLessThanTenTicksRemain() {
		// a 64th lasts 60 ticks: 16 repetitions, the last one shortened to 59 ticks
		tremoloPicking(song.quarter(1, 0), TGDuration.SIXTY_FOURTH);
		List<String> notes = song.notes();
		assertEquals(32, notes.size());
		assertEquals("on 1860 t1 c1 k64 v95 s1 b0", notes.get(30));
		assertEquals("off 1919 t1 c1 k64 v95 s1 b0", notes.get(31));
	}

	// ---- pitch effects ----

	private TGEffectBend bendEffect(int position, int value) {
		TGEffectBend bend = song.factory.newEffectBend();
		bend.addPoint(position, value);
		return bend;
	}

	@Test
	public void bendAddsPitchBendsAndBendMode() {
		song.quarter(1, 0).getEffect().setBend(bendEffect(3, 4));
		assertEquals(events(INITIAL_BEND, bend(1200, 75), bend(1920, 64), note(960, 1920, 64, 95, 1, 1)), song.notesAndBends());
	}

	@Test
	public void bendWinsOverVibrato() {
		TGNote note = song.quarter(1, 0);
		note.getEffect().setVibrato(true);
		note.getEffect().setBend(bendEffect(3, 4));
		assertEquals(events(INITIAL_BEND, bend(1200, 75), bend(1920, 64), note(960, 1920, 64, 95, 1, 1)), song.notesAndBends());
	}

	@Test
	public void tremoloBarAddsPitchBendsAndBendMode() {
		TGEffectTremoloBar tremoloBar = song.factory.newEffectTremoloBar();
		tremoloBar.addPoint(6, 1);
		TGNote note = song.quarter(1, 0);
		note.getEffect().setVibrato(true);
		note.getEffect().setTremoloBar(tremoloBar);
		assertEquals(events(INITIAL_BEND, bend(1440, 69), bend(1920, 64), note(960, 1920, 64, 95, 1, 1)), song.notesAndBends());
	}

	// ---- pitch curves: a bend or tremolo bar through several points, on a quarter note from 960 to 1920 ----

	// adds the points, given as position, value pairs
	private static void points(BiConsumer<Integer, Integer> addPoint, int... positionsAndValues) {
		for (int i = 0; i < positionsAndValues.length; i += 2) {
			addPoint.accept(positionsAndValues[i], positionsAndValues[i + 1]);
		}
	}

	private List<String> bentQuarter(int... positionsAndValues) {
		TGEffectBend bend = song.factory.newEffectBend();
		points(bend::addPoint, positionsAndValues);
		song.quarter(1, 0).getEffect().setBend(bend);
		return song.notesAndBends();
	}

	private List<String> tremoloBarQuarter(int... positionsAndValues) {
		TGEffectTremoloBar tremoloBar = song.factory.newEffectTremoloBar();
		points(tremoloBar::addPoint, positionsAndValues);
		song.quarter(1, 0).getEffect().setTremoloBar(tremoloBar);
		return song.notesAndBends();
	}

	// the events of the quarter note played with these bends
	private static List<String> quarterWith(String... bends) {
		return events(INITIAL_BEND, bends, note(960, 1920, 64, 95, 1, 1));
	}

	@Test
	public void bendRisesOneStepAtATimeToTheNextPoint() {
		assertEquals(quarterWith(bend(960, 64),
				bend(1056, 65), bend(1152, 66), bend(1248, 67), bend(1344, 68), bend(1440, 69),
				bend(1440, 69), bend(1920, 64)), bentQuarter(0, 0, 6, 2));
	}

	@Test
	public void bendHoldsBetweenEqualPointsThenFallsOneStepAtATime() {
		assertEquals(quarterWith(bend(960, 69), bend(1440, 69),
				bend(1536, 68), bend(1632, 67), bend(1728, 66), bend(1824, 65), bend(1920, 64),
				bend(1920, 64), bend(1920, 64)), bentQuarter(0, 2, 6, 2, 12, 0));
	}

	@Test
	public void bendStepsAreWholeTicksApart() {
		// 80 ticks for 11 steps: 7 ticks each
		assertEquals(quarterWith(bend(960, 64),
				bend(967, 65), bend(974, 66), bend(981, 67), bend(988, 68), bend(995, 69), bend(1002, 70),
				bend(1009, 71), bend(1016, 72), bend(1023, 73), bend(1030, 74), bend(1037, 75),
				bend(1040, 75), bend(1920, 64)), bentQuarter(0, 0, 1, 4));
	}

	@Test
	public void tremoloBarRisesAndFallsOneStepAtATime() {
		assertEquals(quarterWith(bend(960, 64),
				bend(1056, 65), bend(1152, 66), bend(1248, 67), bend(1344, 68), bend(1440, 69),
				bend(1440, 69),
				bend(1536, 68), bend(1632, 67), bend(1728, 66), bend(1824, 65), bend(1920, 64),
				bend(1920, 64), bend(1920, 64)), tremoloBarQuarter(0, 0, 6, 1, 12, 0));
	}

	@Test
	public void tremoloBarStepsStopAtTheHighestPitch() {
		assertEquals(quarterWith(bend(960, 124),
				bend(1040, 125), bend(1120, 126), bend(1200, 127), bend(1280, 127), bend(1360, 127), bend(1440, 127),
				bend(1440, 127), bend(1920, 64)), tremoloBarQuarter(0, 11, 6, 12));
	}

	@Test
	public void aTremoloBarHeldAboveThePitchRangeStepsTowardsItsValueOutsideIt() {
		// 12 is pitch 130 (played 127), -12 is pitch -2 (played 0): the steps go from the played pitch to the value
		assertEquals(quarterWith(bend(960, 127), bend(1280, 127), bend(1600, 127), bend(1920, 127), bend(1920, 127),
				bend(1920, 64)), tremoloBarQuarter(0, 12, 12, 12));
	}

	@Test
	public void aTremoloBarHeldBelowThePitchRangeStepsTowardsItsValueOutsideIt() {
		assertEquals(quarterWith(bend(960, 0), bend(1440, 0), bend(1920, 0), bend(1920, 0),
				bend(1920, 64)), tremoloBarQuarter(0, -12, 12, -12));
	}

	@Test
	public void slideBendsTowardsTheNextNoteInTheNextMeasure() {
		TGNote note = song.note(12, TGDuration.QUARTER, 1, 0);
		note.getEffect().setVibrato(true);
		note.getEffect().setSlide(true);
		song.note(1, 0, TGDuration.QUARTER, 1, 2, 0);
		assertEquals(events(INITIAL_BEND,
				bend(3960, 65), bend(4080, 66), bend(4200, 68), bend(4320, 69),
				bend(4440, 70), bend(4560, 72), bend(4680, 73), bend(4800, 75), bend(4800, 64),
				note(3840, 4800, 64, 95, 1, 1), note(4800, 5760, 66)), song.notesAndBends());
	}

	@Test
	public void slideBendsTowardsTheNextNoteOnTheString() {
		TGNote note = song.quarter(1, 0);
		note.getEffect().setVibrato(true);
		note.getEffect().setSlide(true);
		song.note(4, TGDuration.QUARTER, 1, 2);
		assertEquals(events(INITIAL_BEND,
				bend(1080, 65), bend(1200, 66), bend(1320, 68), bend(1440, 69),
				bend(1560, 70), bend(1680, 72), bend(1800, 73), bend(1920, 75), bend(1920, 64),
				note(960, 1920, 64, 95, 1, 1), note(1920, 2880, 66)), song.notesAndBends());
	}

	@Test
	public void slideToNothingOnlySetsBendMode() {
		song.quarter(1, 0).getEffect().setSlide(true);
		assertEquals(events(INITIAL_BEND, note(960, 1920, 64, 95, 1, 1)), song.notesAndBends());
	}

	@Test
	public void vibratoOscillatesThePitch() {
		song.quarter(1, 0).getEffect().setVibrato(true);
		assertEquals(events(INITIAL_BEND,
				bend(1120, 64), bend(1280, 65), bend(1440, 64), bend(1600, 65), bend(1760, 64), bend(1920, 65), bend(1920, 64),
				note(960, 1920, 64, 95, 1, 1)), song.notesAndBends());
	}

	@Test
	public void pitchEffectsAreIgnoredOnPercussion() {
		song.percussion();
		song.quarter(1, 0).getEffect().setBend(bendEffect(3, 4));
		song.quarter(2, 0).getEffect().setVibrato(true);
		TGEffectTremoloBar tremoloBar = song.factory.newEffectTremoloBar();
		tremoloBar.addPoint(6, 1);
		song.quarter(3, 0).getEffect().setTremoloBar(tremoloBar);
		song.quarter(4, 0).getEffect().setSlide(true);
		song.note(4, TGDuration.QUARTER, 4, 2);
		assertEquals(events(INITIAL_BEND,
				note(960, 1920, 64), note(960, 1920, 59, 95, 2, 0), note(960, 1920, 55, 95, 3, 0), note(960, 1920, 50, 95, 4, 0),
				note(1920, 2880, 52, 95, 4, 0)), song.notesAndBends());
	}

	// ---- harmonics ----

	private void harmonic(TGNote note, int type, int data) {
		TGEffectHarmonic harmonic = song.factory.newEffectHarmonic();
		harmonic.setType(type);
		harmonic.setData(data);
		note.getEffect().setHarmonic(harmonic);
	}

	@Test
	public void naturalHarmonicAtTwelfthFretPlaysTheOctaveBelowSoftly() {
		harmonic(song.quarter(1, 12), TGEffectHarmonic.TYPE_NATURAL, 0);
		assertEquals(events(note(960, 1920, 64, 31, 1, 0), note(960, 1920, 76)), song.notes());
	}

	@Test
	public void naturalHarmonicAtSeventhFretRaisesBy19Frets() {
		harmonic(song.quarter(1, 7), TGEffectHarmonic.TYPE_NATURAL, 0);
		assertEquals(events(note(960, 1920, 71, 31, 1, 0), note(960, 1920, 83)), song.notes());
	}

	@Test
	public void naturalHarmonicAtNinthFretRaisesBy28Frets() {
		harmonic(song.quarter(1, 9), TGEffectHarmonic.TYPE_NATURAL, 0);
		assertEquals(events(note(960, 1920, 80, 31, 1, 0), note(960, 1920, 92)), song.notes());
	}

	@Test
	public void naturalHarmonicOnOtherFretKeepsTheKey() {
		harmonic(song.quarter(1, 1), TGEffectHarmonic.TYPE_NATURAL, 0);
		assertEquals(events(note(960, 1920, 53, 31, 1, 0), note(960, 1920, 65)), song.notes());
	}

	@Test
	public void artificialHarmonicRaisesByItsData() {
		harmonic(song.quarter(1, 0), TGEffectHarmonic.TYPE_ARTIFICIAL, 3);
		assertEquals(events(note(960, 1920, 71, 31, 1, 0), note(960, 1920, 83)), song.notes());
	}

	@Test
	public void semiHarmonicAlsoPlaysTheFrettedNote() {
		harmonic(song.quarter(1, 0), TGEffectHarmonic.TYPE_SEMI, 0);
		assertEquals(events(note(960, 1920, 64, 47, 1, 0), note(960, 1920, 64, 31, 1, 0), note(960, 1920, 76)), song.notes());
	}

	@Test
	public void harmonicVelocitiesNeverGoBelowMinimum() {
		TGNote note = song.quarter(1, 0);
		note.setVelocity(20);
		harmonic(note, TGEffectHarmonic.TYPE_SEMI, 0);
		assertEquals(events(note(960, 1920, 64, 15, 1, 0), note(960, 1920, 64, 15, 1, 0), note(960, 1920, 76, 20, 1, 0)), song.notes());
	}

	@Test
	public void harmonicKeepsBendModeOfPitchEffect() {
		TGNote note = song.quarter(1, 0);
		note.getEffect().setSlide(true);
		harmonic(note, TGEffectHarmonic.TYPE_ARTIFICIAL, 0);
		assertEquals(events(note(960, 1920, 64, 31, 1, 1), note(960, 1920, 76, 95, 1, 1)), song.notes());
	}

	@Test
	public void harmonicOctaveBelowIsSkippedWhenNotAboveKeyZero() {
		song.transpose = -40;
		harmonic(song.quarter(6, 0), TGEffectHarmonic.TYPE_ARTIFICIAL, 0);
		assertEquals(events(note(960, 1920, 12, 95, 6, 0)), song.notes());
	}

	@Test
	public void harmonicOctaveBelowIsPlayedWhenAboveKeyZero() {
		song.transpose = -39;
		harmonic(song.quarter(6, 0), TGEffectHarmonic.TYPE_ARTIFICIAL, 0);
		assertEquals(events(note(960, 1920, 1, 31, 6, 0), note(960, 1920, 13, 95, 6, 0)), song.notes());
	}

	@Test
	public void harmonicIsIgnoredOnPercussion() {
		song.percussion();
		harmonic(song.quarter(1, 0), TGEffectHarmonic.TYPE_SEMI, 0);
		assertEquals(events(note(960, 1920, 64)), song.notes());
	}

	// ---- triplet feel ----

	private void tripletFeel(int tripletFeel) {
		song.measure(0).getHeader().setTripletFeel(tripletFeel);
	}

	@Test
	public void eighthTripletFeelSwingsAPairOfEighths() {
		tripletFeel(TGMeasureHeader.TRIPLET_FEEL_EIGHTH);
		song.note(0, TGDuration.EIGHTH, 1, 0);
		song.note(2, TGDuration.EIGHTH, 1, 2);
		assertEquals(events(note(960, 1600, 64), note(1600, 1920, 66)), song.notes());
	}

	@Test
	public void eighthTripletFeelSwingsLoneEighths() {
		tripletFeel(TGMeasureHeader.TRIPLET_FEEL_EIGHTH);
		song.note(0, TGDuration.EIGHTH, 1, 0);
		song.note(6, TGDuration.EIGHTH, 1, 2);
		assertEquals(events(note(960, 1600, 64), note(2560, 2880, 66)), song.notes());
	}

	@Test
	public void eighthTripletFeelSwingsFirstEighthFollowedByAGap() {
		tripletFeel(TGMeasureHeader.TRIPLET_FEEL_EIGHTH);
		song.note(0, TGDuration.EIGHTH, 1, 0);
		song.note(4, TGDuration.QUARTER, 2, 0);
		assertEquals(events(note(960, 1600, 64), note(1920, 2880, 59, 95, 2, 0)), song.notes());
	}

	@Test
	public void eighthTripletFeelSwingsSecondEighthAfterAGap() {
		tripletFeel(TGMeasureHeader.TRIPLET_FEEL_EIGHTH);
		song.note(0, TGDuration.SIXTEENTH, 2, 0);
		song.note(6, TGDuration.EIGHTH, 1, 0);
		assertEquals(events(note(960, 1200, 59, 95, 2, 0), note(2560, 2880, 64)), song.notes());
	}

	@Test
	public void eighthTripletFeelKeepsEighthsNextToOtherDurations() {
		tripletFeel(TGMeasureHeader.TRIPLET_FEEL_EIGHTH);
		song.note(0, TGDuration.EIGHTH, 1, 0);
		song.note(2, TGDuration.SIXTEENTH, 2, 0);
		song.note(5, TGDuration.SIXTEENTH, 3, 0);
		song.note(6, TGDuration.EIGHTH, 4, 0);
		assertEquals(events(note(960, 1440, 64), note(1440, 1680, 59, 95, 2, 0), note(2160, 2400, 55, 95, 3, 0),
				note(2400, 2880, 50, 95, 4, 0)), song.notes());
	}

	@Test
	public void eighthTripletFeelKeepsOffBeatEighthsAndOtherDurations() {
		tripletFeel(TGMeasureHeader.TRIPLET_FEEL_EIGHTH);
		song.note(1, TGDuration.EIGHTH, 1, 0);
		song.note(4, TGDuration.QUARTER, 2, 0);
		assertEquals(events(note(1200, 1680, 64), note(1920, 2880, 59, 95, 2, 0)), song.notes());
	}

	@Test
	public void sixteenthTripletFeelSwingsAPairOfSixteenths() {
		tripletFeel(TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH);
		song.note(0, TGDuration.SIXTEENTH, 1, 0);
		song.note(1, TGDuration.SIXTEENTH, 1, 2);
		assertEquals(events(note(960, 1280, 64), note(1280, 1440, 66)), song.notes());
	}

	@Test
	public void sixteenthTripletFeelKeepsSixteenthsNextToOtherDurations() {
		tripletFeel(TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH);
		song.note(0, TGDuration.SIXTEENTH, 1, 0);
		song.note(1, TGDuration.EIGHTH, 2, 0);
		// overlapping eighth: the only way, on a sixteenth grid, for a sixteenth to directly follow another duration
		song.note(2, TGDuration.EIGHTH, 4, 0);
		song.note(3, TGDuration.SIXTEENTH, 3, 0);
		assertEquals(events(note(960, 1200, 64), note(1200, 1680, 59, 95, 2, 0), note(1440, 1920, 50, 95, 4, 0),
				note(1680, 1920, 55, 95, 3, 0)), song.notes());
	}

	@Test
	public void sixteenthTripletFeelSwingsFirstSixteenthFollowedByAGap() {
		tripletFeel(TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH);
		song.note(0, TGDuration.SIXTEENTH, 1, 0);
		song.note(2, TGDuration.EIGHTH, 2, 0);
		song.note(5, TGDuration.SIXTEENTH, 3, 0);
		assertEquals(events(note(960, 1280, 64), note(1440, 1920, 59, 95, 2, 0), note(2240, 2400, 55, 95, 3, 0)), song.notes());
	}

	@Test
	public void sixteenthTripletFeelSwingsLoneSixteenths() {
		tripletFeel(TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH);
		song.note(2, TGDuration.SIXTEENTH, 1, 0);
		song.note(7, TGDuration.SIXTEENTH, 2, 0);
		assertEquals(events(note(1440, 1760, 64), note(2720, 2880, 59, 95, 2, 0)), song.notes());
	}

	@Test
	public void noTripletFeelKeepsEighths() {
		song.note(0, TGDuration.EIGHTH, 1, 0);
		song.note(2, TGDuration.EIGHTH, 1, 2);
		assertEquals(events(note(960, 1440, 64), note(1440, 1920, 66)), song.notes());
	}
}

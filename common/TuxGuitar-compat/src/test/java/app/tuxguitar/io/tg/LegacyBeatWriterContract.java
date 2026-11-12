package app.tuxguitar.io.tg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.TGText;
import app.tuxguitar.song.models.TGVoice;

/**
 * How the 1.0 - 1.5 writers store beats: a beat stores a voice's duration and flags only
 * when they differ from the ones last written for that voice, plus its stroke, chord and text.
 * Each format's test extends this and pins its own exact bytes.
 * (start ticks: the first measure starts at 960, a quarter is 960 ticks)
 */
public abstract class LegacyBeatWriterContract {

	/** What the 1.1 - 1.5 readers see of songWithEveryBeatFeature: everything. */
	protected static final List<String> EVERY_BEAT_FEATURE = Arrays.asList(
			"+0 v0:d8 s1f5 up v1:d4 s6f3 down stroke1:16 chord[Am 1 [0, 1, 2, 2, 0, -1]] text[intro]",
			"+480 v0:d8 s2f7",
			"+960 v0:d8 s3f3 v1:d2 rest stroke-1:8",
			"+1440 v0:d4. rest",
			"+2880 v0:d4 s1f0 v1:d4 s6f3");

	private final TGFactory factory = new TGFactory();

	protected abstract TGSongWriter newWriter();

	protected abstract TGSongReader newReader();

	/** SHA-256 of songWithEveryBeatFeature written by this format's writer, captured before refactoring. */
	protected abstract String goldenSha256();

	/** What this format's reader sees of songWithEveryBeatFeature. */
	protected List<String> expectedBeats() {
		return EVERY_BEAT_FEATURE;
	}

	/**
	 * One 4/4 measure. Voice 0: three eighth notes, a dotted quarter rest and a quarter note;
	 * voice 1: a quarter note, a half rest and a quarter note. Directions, strokes, a chord and
	 * a text on some beats; the third beat keeps voice 0's duration and flags.
	 */
	private TGSong songWithEveryBeatFeature() {
		TGSong song = LegacyFormatFixture.emptySong(this.factory);
		TGMeasure measure = LegacyFormatFixture.firstMeasure(song);
		long eighth = TGDuration.QUARTER_TIME / 2;

		TGBeat first = this.beat(measure, 0, TGDuration.EIGHTH, 1, 5);
		first.getVoice(0).setDirection(TGVoice.DIRECTION_UP);
		LegacyFormatFixture.addNote(this.factory, first.getVoice(1), TGDuration.QUARTER, 6, 3);
		first.getVoice(1).setDirection(TGVoice.DIRECTION_DOWN);
		this.stroke(first, TGStroke.STROKE_UP, TGDuration.SIXTEENTH);
		first.setChord(this.chord("Am", 1, 0, 1, 2, 2, 0, -1));
		TGText text = this.factory.newText();
		text.setValue("intro");
		first.setText(text);

		this.beat(measure, eighth, TGDuration.EIGHTH, 2, 7);

		TGBeat third = this.beat(measure, 2 * eighth, TGDuration.EIGHTH, 3, 3);
		LegacyFormatFixture.makeRest(third.getVoice(1), TGDuration.HALF);
		this.stroke(third, TGStroke.STROKE_DOWN, TGDuration.EIGHTH);

		TGBeat fourth = LegacyFormatFixture.addBeat(this.factory, measure, 3 * eighth);
		LegacyFormatFixture.makeRest(fourth.getVoice(0), TGDuration.QUARTER);
		fourth.getVoice(0).getDuration().setDotted(true);

		TGBeat last = this.beat(measure, 6 * eighth, TGDuration.QUARTER, 1, 0);
		LegacyFormatFixture.addNote(this.factory, last.getVoice(1), TGDuration.QUARTER, 6, 3);
		return song;
	}

	/** A beat whose voice 0 holds one note. */
	private TGBeat beat(TGMeasure measure, long offset, int duration, int string, int fret) {
		TGBeat beat = LegacyFormatFixture.addBeat(this.factory, measure, offset);
		LegacyFormatFixture.addNote(this.factory, beat.getVoice(0), duration, string, fret);
		return beat;
	}

	private void stroke(TGBeat beat, int direction, int value) {
		beat.getStroke().setDirection(direction);
		beat.getStroke().setValue(value);
	}

	private TGChord chord(String name, int firstFret, int... frets) {
		TGChord chord = this.factory.newChord(frets.length);
		chord.setName(name);
		chord.setFirstFret(firstFret);
		for (int string = 0; string < frets.length; string++) {
			chord.addFretValue(string, frets[string]);
		}
		return chord;
	}

	@Test
	public void everyBeatFeatureSurvivesRoundTrip() throws Exception {
		TGSong read = LegacyFormatFixture.roundTrip(this.newWriter(), this.newReader(), this.factory, this.songWithEveryBeatFeature());
		assertEquals(this.expectedBeats(), LegacyFormatFixture.describeBeatFeatures(read));
	}

	@Test
	public void writtenBytesMatchGolden() throws Exception {
		byte[] bytes = LegacyFormatFixture.write(this.newWriter(), this.factory, this.songWithEveryBeatFeature());
		assertEquals(this.goldenSha256(), LegacyFormatFixture.sha256(bytes));
	}
}

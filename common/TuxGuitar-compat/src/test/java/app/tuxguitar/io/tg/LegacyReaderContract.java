package app.tuxguitar.io.tg;

import static app.tuxguitar.io.tg.LegacyFormatFixture.describeBeats;
import static app.tuxguitar.io.tg.LegacyFormatFixture.describeHeaders;
import static app.tuxguitar.io.tg.LegacyFormatFixture.firstMeasure;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGVoice;

/**
 * What the 0.8 and 0.9 readers share: measure headers and the way components
 * (one note or rest each) are grouped into beats. Each format's test extends this
 * and says how its tracks and notes are encoded.
 * (start ticks: the first measure starts at 960, a quarter is 960 ticks)
 */
public abstract class LegacyReaderContract {

	protected static final int NOTE = 0x01;
	protected static final int TIED = 0x04;
	protected static final int EFFECT = 0x08;
	protected static final int NEXT_BEAT = 0x10;
	protected static final int NEXT_DURATION = 0x20;

	private static final int ALL_HEADER_FLAGS = 0x3F;
	private static final int TIME_SIGNATURE_TEMPO_AND_TRIPLET_FEEL = 0x23;

	private final TGFactory factory = new TGFactory();

	protected abstract TGSongReader newReader();

	protected abstract String version();

	/** Writes what the format puts before a track's name. */
	protected abstract void trackHead(LegacyStreamEncoder encoder);

	/** Writes the bytes that follow a component header that has only the NOTE and TIED flags. */
	protected abstract void note(LegacyStreamEncoder encoder, int value, int string);

	protected TGSong read(LegacyStreamEncoder encoder) throws Exception {
		return LegacyFormatFixture.read(this.newReader(), this.factory, encoder.toByteArray());
	}

	/** A song of one 4/4 measure and one track, written up to that measure's components. */
	protected LegacyStreamEncoder measureOf(int componentCount) {
		LegacyStreamEncoder encoder = new LegacyStreamEncoder(this.version()).songInfo(1).bytes(0, 1);
		this.trackHead(encoder);
		return encoder.string("track").channel().bytes(0).shorts(componentCount);
	}

	protected TGSong readMeasure(LegacyStreamEncoder encoder) throws Exception {
		return this.read(encoder.trackTail());
	}

	private List<String> readHeaders(LegacyStreamEncoder encoder) throws Exception {
		return describeHeaders(this.read(encoder.bytes(0)));
	}

	@Test
	public void headerWithoutFlagsKeepsTheDefaults() throws Exception {
		LegacyStreamEncoder encoder = new LegacyStreamEncoder(this.version()).songInfo(1).bytes(0);
		assertEquals(Arrays.asList("#1 @960 4/4 q120 open=false close=0 triplet=1"), this.readHeaders(encoder));
	}

	@Test
	public void headerWithEveryFlagReadsEveryField() throws Exception {
		LegacyStreamEncoder encoder = new LegacyStreamEncoder(this.version()).songInfo(1)
				.bytes(ALL_HEADER_FLAGS, 3, 0, 8).shorts(90, 2).string("Intro").shorts(10, 20, 30).bytes(2);
		assertEquals(Arrays.asList("#1 @960 3/8 q90 open=true close=2 triplet=2 marker[Intro #1 10,20,30]"),
				this.readHeaders(encoder));
	}

	@Test
	public void headerWithoutFlagsInheritsTimeSignatureTempoAndTripletFeel() throws Exception {
		LegacyStreamEncoder encoder = new LegacyStreamEncoder(this.version()).songInfo(2)
				.bytes(TIME_SIGNATURE_TEMPO_AND_TRIPLET_FEEL, 3, 0, 8).shorts(90).bytes(3)
				.bytes(0);
		assertEquals(Arrays.asList(
				"#1 @960 3/8 q90 open=false close=0 triplet=3",
				"#2 @2400 3/8 q90 open=false close=0 triplet=3"), this.readHeaders(encoder));
	}

	@Test
	public void componentsJoinBeatsAndCarryTheirDurationForward() throws Exception {
		LegacyStreamEncoder encoder = this.measureOf(5);
		encoder.bytes(NOTE | NEXT_DURATION, 0, 8);
		this.note(encoder, 5, 1);
		encoder.bytes(NOTE | TIED);
		this.note(encoder, 7, 2);
		encoder.bytes(NOTE | NEXT_BEAT);
		this.note(encoder, 3, 3);
		encoder.bytes(NEXT_BEAT | NEXT_DURATION, 1, 4);
		encoder.bytes(NEXT_BEAT);
		TGSong song = this.readMeasure(encoder);
		assertEquals(Arrays.asList("+0 d8 s1f5 s2f7", "+480 d8 s3f3", "+960 d4. rest", "+2400 d4. rest"), describeBeats(song));
		TGVoice chord = firstMeasure(song).getBeat(0).getVoice(0);
		assertFalse(chord.getNote(0).isTiedNote());
		assertTrue(chord.getNote(1).isTiedNote());
	}

	@Test
	public void firstComponentWithoutDurationIsAQuarter() throws Exception {
		LegacyStreamEncoder encoder = this.measureOf(1);
		encoder.bytes(NOTE);
		this.note(encoder, 2, 1);
		assertEquals(Arrays.asList("+0 d4 s1f2"), describeBeats(this.readMeasure(encoder)));
	}
}

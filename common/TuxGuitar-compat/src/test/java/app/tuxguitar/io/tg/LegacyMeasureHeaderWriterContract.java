package app.tuxguitar.io.tg;

import static app.tuxguitar.io.tg.LegacyFormatFixture.describeHeaders;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGColor;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;

/**
 * How the 1.0 - 1.3 writers store measure headers: a header only stores the time signature,
 * tempo and triplet feel when they differ from the previous header (the first header always
 * stores time signature and tempo), plus its repeats and marker. Each format's test extends
 * this and pins its own exact bytes.
 * (start ticks: the first measure starts at 960, a quarter is 960 ticks)
 */
public abstract class LegacyMeasureHeaderWriterContract {

	private final TGFactory factory = new TGFactory();

	protected abstract TGSongWriter newWriter();

	protected abstract TGSongReader newReader();

	/** SHA-256 of songWithEveryHeaderChange written by this format's writer, captured before refactoring. */
	protected abstract String goldenSha256();

	private TGSong roundTrip(TGSong song) throws Exception {
		return LegacyFormatFixture.roundTrip(this.newWriter(), this.newReader(), this.factory, song);
	}

	/**
	 * Five measures: a first one with a repeat open and a marker, then one change per measure
	 * (repeat close, numerator and alternative ending, denominator and tempo, triplet feel).
	 */
	private TGSong songWithEveryHeaderChange() {
		TGSongManager manager = new TGSongManager(this.factory);
		TGSong song = manager.newSong();
		TGMeasureHeader first = song.getMeasureHeader(0);
		first.setRepeatOpen(true);
		first.setMarker(this.marker("Intro"));
		this.addMeasure(manager, song).setRepeatClose(1);
		TGMeasureHeader third = this.addMeasure(manager, song);
		third.getTimeSignature().setNumerator(3);
		third.setRepeatAlternative(1);
		TGMeasureHeader fourth = this.addMeasure(manager, song);
		fourth.getTimeSignature().getDenominator().setValue(TGDuration.EIGHTH);
		fourth.getTempo().setQuarterValue(90);
		this.addMeasure(manager, song).setTripletFeel(TGMeasureHeader.TRIPLET_FEEL_EIGHTH);
		return song;
	}

	/** Adds a measure holding one quarter rest: the legacy readers cannot read a measure without beats. */
	private TGMeasureHeader addMeasure(TGSongManager manager, TGSong song) {
		TGMeasureHeader header = manager.addNewMeasureBeforeEnd(song);
		TGMeasure measure = song.getTrack(0).getMeasure(header.getNumber() - 1);
		TGBeat beat = this.factory.newBeat();
		beat.setPreciseStart(TGDuration.toPreciseTime(header.getStart()));
		measure.addBeat(beat);
		LegacyFormatFixture.makeRest(beat.getVoice(0), TGDuration.QUARTER);
		return header;
	}

	private TGMarker marker(String title) {
		TGMarker marker = this.factory.newMarker();
		marker.setMeasure(1);
		marker.setTitle(title);
		marker.setColor(TGColor.newColor(10, 20, 30));
		return marker;
	}

	@Test
	public void everyHeaderChangeSurvivesRoundTrip() throws Exception {
		assertEquals(Arrays.asList(
				"#1 @960 4/4 q120 open=true close=0 triplet=1 marker[Intro #1 10,20,30]",
				"#2 @4800 4/4 q120 open=false close=1 triplet=1",
				"#3 @8640 3/4 q120 open=false close=0 triplet=1 alternative=1",
				"#4 @11520 3/8 q90 open=false close=0 triplet=1",
				"#5 @12960 3/8 q90 open=false close=0 triplet=2"),
				describeHeaders(this.roundTrip(this.songWithEveryHeaderChange())));
	}

	@Test
	public void firstHeaderStoresItsTripletFeel() throws Exception {
		TGSong song = LegacyFormatFixture.emptySong(this.factory);
		song.getMeasureHeader(0).setTripletFeel(TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH);
		assertEquals(Arrays.asList("#1 @960 4/4 q120 open=false close=0 triplet=3"), describeHeaders(this.roundTrip(song)));
	}

	@Test
	public void writtenBytesMatchGolden() throws Exception {
		byte[] bytes = LegacyFormatFixture.write(this.newWriter(), this.factory, this.songWithEveryHeaderChange());
		assertEquals(this.goldenSha256(), LegacyFormatFixture.sha256(bytes));
	}
}

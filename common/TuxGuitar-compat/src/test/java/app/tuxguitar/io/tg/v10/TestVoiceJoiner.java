package app.tuxguitar.io.tg.v10;

import static app.tuxguitar.io.tg.LegacyFormatFixture.addBeat;
import static app.tuxguitar.io.tg.LegacyFormatFixture.addNote;
import static app.tuxguitar.io.tg.LegacyFormatFixture.describeBeats;
import static app.tuxguitar.io.tg.LegacyFormatFixture.emptySong;
import static app.tuxguitar.io.tg.LegacyFormatFixture.firstMeasure;
import static app.tuxguitar.io.tg.LegacyFormatFixture.makeRest;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.tg.LegacyFormatFixture;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;

/*
 * Format 1.0 has a single voice per beat: TGSongWriterImpl.TGVoiceJoiner merges
 * all voices into voice 0 and fixes beat durations before writing.
 * These tests check what the 1.0 reader sees after a write.
 * (offsets are ticks from the measure start, quarter = 960, 4/4 measure = 3840)
 */
public class TestVoiceJoiner {

	private static final long QUARTER = TGDuration.QUARTER_TIME;

	/** SHA-256 of all scenario songs written in order, captured before refactoring. */
	private static final String GOLDEN_SHA256 = "831fe9bc413ec400663f117270b01c41428869ce9cd31d1c8dbb65eac1acc811";

	private final TGFactory factory = new TGFactory();

	private List<String> roundTrip(TGSong song) throws Exception {
		byte[] bytes = LegacyFormatFixture.write(new TGSongWriterImpl(), this.factory, song);
		return describeBeats(LegacyFormatFixture.read(new TGSongReaderImpl(), this.factory, bytes));
	}

	private TGSong mergesSecondVoice() {
		TGSong song = emptySong(this.factory);
		TGMeasure measure = firstMeasure(song);
		TGBeat first = addBeat(this.factory, measure, 0);
		addNote(this.factory, first.getVoice(0), TGDuration.QUARTER, 1, 1);
		addNote(this.factory, first.getVoice(1), TGDuration.HALF, 2, 2);
		TGBeat second = addBeat(this.factory, measure, 2 * QUARTER);
		addNote(this.factory, second.getVoice(0), TGDuration.QUARTER, 3, 3);
		return song;
	}

	@Test
	public void mergesSecondVoiceIntoFirstAndKeepsLongestFittingDuration() throws Exception {
		assertEquals(Arrays.asList("+0 d2 s1f1 s2f2", "+1920 d4 s3f3"), roundTrip(mergesSecondVoice()));
	}

	private TGSong secondVoiceOnly() {
		TGSong song = emptySong(this.factory);
		TGMeasure measure = firstMeasure(song);
		TGBeat first = addBeat(this.factory, measure, 0);
		addNote(this.factory, first.getVoice(1), TGDuration.HALF, 2, 2);
		TGBeat second = addBeat(this.factory, measure, 2 * QUARTER);
		addNote(this.factory, second.getVoice(0), TGDuration.HALF, 3, 3);
		addNote(this.factory, second.getVoice(1), TGDuration.QUARTER, 4, 4);
		return song;
	}

	@Test
	public void movesSecondVoiceNotesIntoEmptyFirstVoice() throws Exception {
		assertEquals(Arrays.asList("+0 d2 s2f2", "+1920 d2 s3f3 s4f4"), roundTrip(secondVoiceOnly()));
	}

	private TGSong emptyBeatInBetween() {
		TGSong song = emptySong(this.factory);
		TGMeasure measure = firstMeasure(song);
		addNote(this.factory, addBeat(this.factory, measure, 0).getVoice(0), TGDuration.QUARTER, 1, 1);
		addBeat(this.factory, measure, QUARTER);
		addNote(this.factory, addBeat(this.factory, measure, 2 * QUARTER).getVoice(0), TGDuration.WHOLE, 2, 2);
		return song;
	}

	@Test
	public void dropsBeatWhoseVoicesAreAllEmptyThenStillFitsTheBeatsAfterIt() throws Exception {
		// the gap left by the dropped beat is not representable: the reader packs beats.
		// the whole note after the dropped beat is still cut to the 2 quarters left in the measure
		assertEquals(Arrays.asList("+0 d4 s1f1", "+960 d2 s2f2"), roundTrip(emptyBeatInBetween()));
	}

	private TGSong equallyLongVoicesWithDifferentTuplets() {
		TGSong song = emptySong(this.factory);
		TGMeasure measure = firstMeasure(song);
		TGBeat first = addBeat(this.factory, measure, 0);
		addNote(this.factory, first.getVoice(0), TGDuration.QUARTER, 1, 1);
		first.getVoice(0).getDuration().getDivision().setEnters(3);
		first.getVoice(0).getDuration().getDivision().setTimes(2);
		addNote(this.factory, first.getVoice(1), TGDuration.QUARTER, 2, 2);
		first.getVoice(1).getDuration().getDivision().setEnters(6);
		first.getVoice(1).getDuration().getDivision().setTimes(4);
		long tupletQuarter = first.getVoice(0).getDuration().getTime();
		addNote(this.factory, addBeat(this.factory, measure, tupletQuarter).getVoice(0), TGDuration.QUARTER, 3, 3);
		return song;
	}

	@Test
	public void keepsFirstVoiceDurationWhenVoicesLastEqually() throws Exception {
		// a 3:2 quarter and a 6:4 quarter both last 640 ticks: the tie goes to the lower voice
		assertEquals(Arrays.asList("+0 d4/3:2 s1f1 s2f2", "+640 d4 s3f3"), roundTrip(equallyLongVoicesWithDifferentTuplets()));
	}

	private TGSong overlappingNotesOutOfOrder() {
		TGSong song = emptySong(this.factory);
		TGMeasure measure = firstMeasure(song);
		addNote(this.factory, addBeat(this.factory, measure, QUARTER).getVoice(0), TGDuration.QUARTER, 2, 2);
		addNote(this.factory, addBeat(this.factory, measure, 0).getVoice(0), TGDuration.HALF, 1, 1);
		return song;
	}

	@Test
	public void ordersBeatsAndShortensPreviousNoteThatOverlapsNextNote() throws Exception {
		assertEquals(Arrays.asList("+0 d4 s1f1", "+960 d4 s2f2"), roundTrip(overlappingNotesOutOfOrder()));
	}

	private TGSong restOverlappedByPreviousNote() {
		TGSong song = emptySong(this.factory);
		TGMeasure measure = firstMeasure(song);
		addNote(this.factory, addBeat(this.factory, measure, 0).getVoice(0), TGDuration.HALF, 1, 1);
		makeRest(addBeat(this.factory, measure, QUARTER).getVoice(0), TGDuration.QUARTER);
		addNote(this.factory, addBeat(this.factory, measure, 2 * QUARTER).getVoice(0), TGDuration.QUARTER, 2, 2);
		return song;
	}

	@Test
	public void dropsRestOverlappedByPreviousNote() throws Exception {
		assertEquals(Arrays.asList("+0 d2 s1f1", "+1920 d4 s2f2"), roundTrip(restOverlappedByPreviousNote()));
	}

	private TGSong lastNoteOverrunsMeasure() {
		TGSong song = emptySong(this.factory);
		TGMeasure measure = firstMeasure(song);
		addNote(this.factory, addBeat(this.factory, measure, 0).getVoice(0), TGDuration.HALF, 1, 1);
		addNote(this.factory, addBeat(this.factory, measure, 2 * QUARTER).getVoice(0), TGDuration.WHOLE, 2, 2);
		return song;
	}

	@Test
	public void shortensLastNoteToMeasureEnd() throws Exception {
		assertEquals(Arrays.asList("+0 d2 s1f1", "+1920 d2 s2f2"), roundTrip(lastNoteOverrunsMeasure()));
	}

	private TGSong lastRestOverrunsMeasure() {
		TGSong song = emptySong(this.factory);
		TGMeasure measure = firstMeasure(song);
		addNote(this.factory, addBeat(this.factory, measure, 0).getVoice(0), TGDuration.HALF, 1, 1);
		makeRest(addBeat(this.factory, measure, 2 * QUARTER).getVoice(0), TGDuration.WHOLE);
		return song;
	}

	@Test
	public void dropsLastRestThatOverrunsMeasure() throws Exception {
		assertEquals(Arrays.asList("+0 d2 s1f1"), roundTrip(lastRestOverrunsMeasure()));
	}

	@Test
	public void writtenBytesMatchGolden() throws Exception {
		ByteArrayOutputStream all = new ByteArrayOutputStream();
		for (TGSong song : Arrays.asList(mergesSecondVoice(), secondVoiceOnly(), emptyBeatInBetween(),
				overlappingNotesOutOfOrder(), restOverlappedByPreviousNote(), lastNoteOverrunsMeasure(),
				lastRestOverrunsMeasure(), equallyLongVoicesWithDifferentTuplets())) {
			all.write(LegacyFormatFixture.write(new TGSongWriterImpl(), this.factory, song));
		}
		assertEquals(GOLDEN_SHA256, LegacyFormatFixture.sha256(all.toByteArray()));
	}
}

package app.tuxguitar.io.tg.v11;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.tg.LegacyFormatFixture;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGSong;

/* Pins how the 1.1 writer stores note effects: what the 1.1 reader gets back, and the exact bytes. */
public class TestWriteNoteEffect {

	/* format 1.1 has no let ring flag: it is dropped */
	private static final boolean WITH_LET_RING = false;

	/** SHA-256 of LegacyFormatFixture.songWithEveryEffect written by this writer, captured before refactoring. */
	private static final String GOLDEN_SHA256 = "2c047174b85618e3fecf8fd43dbc02cfd88b327954171364dc59bd887ceb796a";

	private final TGFactory factory = new TGFactory();

	@Test
	public void everyNoteEffectSurvivesRoundTrip() throws Exception {
		TGSong song = LegacyFormatFixture.songWithEveryEffect(this.factory);
		List<String> expected = LegacyFormatFixture.describeNotes(song, WITH_LET_RING);
		assertEquals(9, expected.size());
		assertTrue(expected.get(0).contains("bend[0:0][6:4][12:1]"), expected.get(0));

		byte[] bytes = LegacyFormatFixture.write(new TGSongWriterImpl(), this.factory, song);
		TGSong read = LegacyFormatFixture.read(new TGSongReaderImpl(), this.factory, bytes);

		assertEquals(expected, LegacyFormatFixture.describeNotes(read, WITH_LET_RING));
	}

	@Test
	public void writtenBytesMatchGolden() throws Exception {
		TGSong song = LegacyFormatFixture.songWithEveryEffect(this.factory);
		byte[] bytes = LegacyFormatFixture.write(new TGSongWriterImpl(), this.factory, song);
		assertEquals(GOLDEN_SHA256, LegacyFormatFixture.sha256(bytes));
	}
}

package app.tuxguitar.io.tg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGSong;

/**
 * How the 1.0 - 1.5 writers store note effects: what the matching reader gets back of
 * LegacyFormatFixture.songWithEveryEffect, and the exact bytes. Each format's test extends this.
 */
public abstract class LegacyNoteEffectWriterContract {

	private final TGFactory factory = new TGFactory();

	protected abstract TGSongWriter newWriter();

	protected abstract TGSongReader newReader();

	/** Whether the format stores the let ring flag (1.0 and 1.1 drop it). */
	protected abstract boolean withLetRing();

	/** SHA-256 of LegacyFormatFixture.songWithEveryEffect written by this format's writer, captured before refactoring. */
	protected abstract String goldenSha256();

	@Test
	public void everyNoteEffectSurvivesRoundTrip() throws Exception {
		TGSong song = LegacyFormatFixture.songWithEveryEffect(this.factory);
		List<String> expected = LegacyFormatFixture.describeNotes(song, this.withLetRing());
		assertEquals(9, expected.size());
		assertTrue(expected.get(0).contains("bend[0:0][6:4][12:1]"), expected.get(0));

		TGSong read = LegacyFormatFixture.roundTrip(this.newWriter(), this.newReader(), this.factory, song);

		assertEquals(expected, LegacyFormatFixture.describeNotes(read, this.withLetRing()));
	}

	@Test
	public void writtenBytesMatchGolden() throws Exception {
		byte[] bytes = LegacyFormatFixture.write(this.newWriter(), this.factory, LegacyFormatFixture.songWithEveryEffect(this.factory));
		assertEquals(this.goldenSha256(), LegacyFormatFixture.sha256(bytes));
	}
}

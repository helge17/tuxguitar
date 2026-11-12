package app.tuxguitar.io.tg.v15;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.tg.LegacyFormatFixture;
import app.tuxguitar.io.tg.LegacyStreamEncoder;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGSong;

/**
 * Reads a hand-encoded 1.5 file whose first measure header stores nothing: the writers always store
 * the first header's time signature and tempo, so only such a file shows what the reader does without
 * a previous header to take them from.
 */
public class TestReadMeasureHeader {

	@Test
	public void firstHeaderThatStoresNothingKeepsTheDefaults() throws Exception {
		LegacyStreamEncoder encoder = new LegacyStreamEncoder(TGSongReaderImpl.SUPPORTED_FORMAT.getVersion())
				.string("name").string("artist").string("album").string("author")
				.string("date").string("copyright").string("writer").string("transcriber").ints(0);
		// no channels, one measure header without flags, no tracks
		encoder.bytes(0).shorts(1).bytes(0).bytes(0);
		TGSong song = LegacyFormatFixture.read(new TGSongReaderImpl(), new TGFactory(), encoder.toByteArray());
		assertEquals(Arrays.asList("#1 @960 4/4 q120 open=false close=0 triplet=1"), LegacyFormatFixture.describeHeaders(song));
	}
}

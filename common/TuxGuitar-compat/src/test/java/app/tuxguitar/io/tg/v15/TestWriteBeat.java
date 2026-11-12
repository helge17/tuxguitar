package app.tuxguitar.io.tg.v15;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyBeatWriterContract;
import app.tuxguitar.io.tg.LegacyFormatFixture;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGSong;

/** Pins how the 1.5 writer stores beats. */
public class TestWriteBeat extends LegacyBeatWriterContract {

	protected TGSongWriter newWriter() {
		return new TGSongWriterImpl();
	}

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	protected String goldenSha256() {
		return "865d81bd0f302cd54510c27be42b17aeaf85bf728fdb204b38b4a63cd45da08c";
	}

	/** 1.5 writes a measure without beats as one beat without voices, which reads back as an empty beat. */
	@Test
	public void aMeasureWithoutBeatsIsWrittenAsAnEmptyBeat() throws Exception {
		TGFactory factory = new TGFactory();
		TGSong song = LegacyFormatFixture.emptySong(factory);
		byte[] bytes = LegacyFormatFixture.write(this.newWriter(), factory, song);
		TGSong read = LegacyFormatFixture.read(this.newReader(), factory, bytes);
		assertEquals("e0246c8fa6b891f477b9b4ca00469aa44c3e2ea1d306329efaaf7f516ef9ca1b", LegacyFormatFixture.sha256(bytes));
		assertEquals(Arrays.asList("+0"), LegacyFormatFixture.describeBeatFeatures(read));
	}
}

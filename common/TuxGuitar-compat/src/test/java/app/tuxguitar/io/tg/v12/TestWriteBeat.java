package app.tuxguitar.io.tg.v12;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyBeatWriterContract;

/** Pins how the 1.2 writer stores beats. */
public class TestWriteBeat extends LegacyBeatWriterContract {

	protected TGSongWriter newWriter() {
		return new TGSongWriterImpl();
	}

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	protected String goldenSha256() {
		return "19a999cfc062a29d473c8832f0472e9d764be206660896ba8fc020bdb8442512";
	}
}

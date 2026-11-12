package app.tuxguitar.io.tg.v13;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyBeatWriterContract;

/** Pins how the 1.3 writer stores beats. */
public class TestWriteBeat extends LegacyBeatWriterContract {

	protected TGSongWriter newWriter() {
		return new TGSongWriterImpl();
	}

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	protected String goldenSha256() {
		return "86be01f8f6a8791dd0a5935b3f85e0b2250404da3018273fe2919fb421bae37c";
	}
}

package app.tuxguitar.io.tg.v11;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyMeasureHeaderWriterContract;

/** Pins how the 1.1 writer stores measure headers. */
public class TestWriteMeasureHeader extends LegacyMeasureHeaderWriterContract {

	protected TGSongWriter newWriter() {
		return new TGSongWriterImpl();
	}

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	protected String goldenSha256() {
		return "0a4e119703ed44d774275e33d9168a9ec39d0c49a1a9993946c568b8171684f7";
	}
}

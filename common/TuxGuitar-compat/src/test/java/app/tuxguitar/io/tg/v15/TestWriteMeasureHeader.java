package app.tuxguitar.io.tg.v15;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyMeasureHeaderWriterContract;

/** Pins how the 1.5 writer stores measure headers. */
public class TestWriteMeasureHeader extends LegacyMeasureHeaderWriterContract {

	protected TGSongWriter newWriter() {
		return new TGSongWriterImpl();
	}

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	protected String goldenSha256() {
		return "56c91c7ae3af4fbb1ac4ec81b898f3c37d58bf763a1e983311c449428b227948";
	}
}

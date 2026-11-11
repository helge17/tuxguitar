package app.tuxguitar.io.tg.v10;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyMeasureHeaderWriterContract;

/** Pins how the 1.0 writer stores measure headers. */
public class TestWriteMeasureHeader extends LegacyMeasureHeaderWriterContract {

	protected TGSongWriter newWriter() {
		return new TGSongWriterImpl();
	}

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	protected String goldenSha256() {
		return "b108040ff4ace7458ee929bd5053d89921d10df9109edd1b74f58fb425673e00";
	}
}

package app.tuxguitar.io.tg.v12;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyMeasureHeaderWriterContract;

/** Pins how the 1.2 writer stores measure headers. */
public class TestWriteMeasureHeader extends LegacyMeasureHeaderWriterContract {

	protected TGSongWriter newWriter() {
		return new TGSongWriterImpl();
	}

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	protected String goldenSha256() {
		return "e4cc2fb2561ee396924cd2582f033d274b868101a44163d5ee453beb7806d39c";
	}
}

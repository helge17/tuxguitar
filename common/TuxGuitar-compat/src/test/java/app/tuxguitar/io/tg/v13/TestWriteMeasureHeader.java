package app.tuxguitar.io.tg.v13;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyMeasureHeaderWriterContract;

/** Pins how the 1.3 writer stores measure headers. */
public class TestWriteMeasureHeader extends LegacyMeasureHeaderWriterContract {

	protected TGSongWriter newWriter() {
		return new TGSongWriterImpl();
	}

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	protected String goldenSha256() {
		return "14998cf658038c8e4a37724828a36078dcd53b1f100dc0d5e578bfa50bfdcb0d";
	}
}

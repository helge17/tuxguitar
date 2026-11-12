package app.tuxguitar.io.tg.v11;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyBeatWriterContract;

/** Pins how the 1.1 writer stores beats. */
public class TestWriteBeat extends LegacyBeatWriterContract {

	protected TGSongWriter newWriter() {
		return new TGSongWriterImpl();
	}

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	protected String goldenSha256() {
		return "6155d7e1936ccb9852da912735a6c0634b02658237a528bd0c677cf28c7be0d0";
	}
}

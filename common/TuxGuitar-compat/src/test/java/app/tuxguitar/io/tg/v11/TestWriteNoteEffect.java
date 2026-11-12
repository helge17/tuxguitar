package app.tuxguitar.io.tg.v11;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyNoteEffectWriterContract;

/** Pins how the 1.1 writer stores note effects. */
public class TestWriteNoteEffect extends LegacyNoteEffectWriterContract {

	protected TGSongWriter newWriter() {
		return new TGSongWriterImpl();
	}

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	protected boolean withLetRing() {
		return false;
	}

	protected String goldenSha256() {
		return "2c047174b85618e3fecf8fd43dbc02cfd88b327954171364dc59bd887ceb796a";
	}
}

package app.tuxguitar.io.tg.v12;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyNoteEffectWriterContract;

/** Pins how the 1.2 writer stores note effects. */
public class TestWriteNoteEffect extends LegacyNoteEffectWriterContract {

	protected TGSongWriter newWriter() {
		return new TGSongWriterImpl();
	}

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	protected boolean withLetRing() {
		return true;
	}

	protected String goldenSha256() {
		return "c9f39f588ed64e50768638f8a650b8dcf5bf1396d8444aa76c9a27bd5a71657c";
	}
}

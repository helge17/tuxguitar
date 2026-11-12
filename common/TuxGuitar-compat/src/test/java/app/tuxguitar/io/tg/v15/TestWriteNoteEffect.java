package app.tuxguitar.io.tg.v15;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyNoteEffectWriterContract;

/** Pins how the 1.5 writer stores note effects. */
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
		return "b0aba251c5aa1a3b6326761a16ac2bbe52c4b90389fec27a488f639f8a8b36cf";
	}
}

package app.tuxguitar.io.tg.v13;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyNoteEffectWriterContract;

/** Pins how the 1.3 writer stores note effects. */
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
		return "27fba573f1a9876978d5693d42f9ba33cecf9c9198d1bc99e92875dcd0f50aca";
	}
}
